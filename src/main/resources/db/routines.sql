CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- trg_after_bill_insert:
-- Creates a BILL_GENERATED notification after a new bill is inserted.
CREATE OR REPLACE FUNCTION fn_after_bill_insert()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
DECLARE
    customer_name VARCHAR;
BEGIN
    SELECT c.full_name
    INTO customer_name
    FROM customers c
    WHERE c.id = NEW.customer_id;

    INSERT INTO notifications (
        id,
        created_at,
        updated_at,
        customer_id,
        message,
        sent_at,
        type
    )
    VALUES (
        gen_random_uuid(),
        NOW(),
        NOW(),
        NEW.customer_id,
        'Dear ' || customer_name || ', Your ' || NEW.month || '/' || NEW.year
            || ' utility bill of ' || NEW.total_amount
            || ' FRW has been successfully processed.',
        NOW(),
        'BILL_GENERATED'
    );

    RETURN NEW;
END;
$$;

DROP TRIGGER IF EXISTS trg_after_bill_insert ON bills;

CREATE TRIGGER trg_after_bill_insert
AFTER INSERT ON bills
FOR EACH ROW
EXECUTE FUNCTION fn_after_bill_insert();

-- trg_after_payment_insert:
-- Marks a fully settled bill as PAID and creates a PAYMENT_CONFIRMED notification.
CREATE OR REPLACE FUNCTION fn_after_payment_insert()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
DECLARE
    bill_balance NUMERIC(19, 2);
    bill_customer_id UUID;
    customer_name VARCHAR;
BEGIN
    SELECT b.outstanding_balance, b.customer_id, c.full_name
    INTO bill_balance, bill_customer_id, customer_name
    FROM bills b
    JOIN customers c ON c.id = b.customer_id
    WHERE b.id = NEW.bill_id;

    IF bill_balance <= 0 THEN
        UPDATE bills
        SET status = 'PAID',
            updated_at = NOW()
        WHERE id = NEW.bill_id;

        INSERT INTO notifications (
            id,
            created_at,
            updated_at,
            customer_id,
            message,
            sent_at,
            type
        )
        VALUES (
            gen_random_uuid(),
            NOW(),
            NOW(),
            bill_customer_id,
            'Dear ' || customer_name
                || ', Your payment has been received. Your bill is now fully paid.',
            NOW(),
            'PAYMENT_CONFIRMED'
        );
    END IF;

    RETURN NEW;
END;
$$;

DROP TRIGGER IF EXISTS trg_after_payment_insert ON payments;

CREATE TRIGGER trg_after_payment_insert
AFTER INSERT ON payments
FOR EACH ROW
EXECUTE FUNCTION fn_after_payment_insert();

-- generate_monthly_bills:
-- Generates bills for all eligible readings in a month/year and logs row-level failures.
CREATE OR REPLACE PROCEDURE generate_monthly_bills(p_month INT, p_year INT)
LANGUAGE plpgsql
AS $$
DECLARE
    reading_cursor CURSOR FOR
        SELECT
            mr.id AS reading_id,
            mr.meter_id,
            mr.consumption,
            m.customer_id,
            m.meter_type
        FROM meter_readings mr
        JOIN meters m ON m.id = mr.meter_id
        JOIN customers c ON c.id = m.customer_id
        WHERE mr.month = p_month
          AND mr.year = p_year
          AND m.status = 'ACTIVE'
          AND c.status = 'ACTIVE';
    reading_record RECORD;
    applicable_tariff RECORD;
    calculated_unit_charge NUMERIC(19, 2);
    calculated_service_charge NUMERIC(19, 2);
    calculated_vat_amount NUMERIC(19, 2);
    calculated_total_amount NUMERIC(19, 2);
BEGIN
    OPEN reading_cursor;

    LOOP
        FETCH reading_cursor INTO reading_record;
        EXIT WHEN NOT FOUND;

        BEGIN
            IF EXISTS (
                SELECT 1
                FROM bills b
                WHERE b.meter_reading_id = reading_record.reading_id
            ) THEN
                RAISE EXCEPTION 'Bill already exists for reading %', reading_record.reading_id;
            END IF;

            SELECT t.unit_price, t.service_charge, t.vat_rate
            INTO STRICT applicable_tariff
            FROM tariffs t
            WHERE t.meter_type = reading_record.meter_type
              AND t.active = TRUE
              AND t.effective_date <= CURRENT_DATE
            ORDER BY t.effective_date DESC
            LIMIT 1;

            calculated_unit_charge :=
                ROUND((reading_record.consumption::NUMERIC * applicable_tariff.unit_price), 2);
            calculated_service_charge := ROUND(applicable_tariff.service_charge, 2);
            calculated_vat_amount := ROUND(
                (calculated_unit_charge + calculated_service_charge)
                * applicable_tariff.vat_rate::NUMERIC / 100,
                2
            );
            calculated_total_amount := ROUND(
                calculated_unit_charge + calculated_service_charge + calculated_vat_amount,
                2
            );

            INSERT INTO bills (
                id,
                created_at,
                updated_at,
                customer_id,
                meter_id,
                meter_reading_id,
                month,
                year,
                total_consumption,
                unit_charge,
                service_charge,
                vat_amount,
                penalty_amount,
                total_amount,
                amount_paid,
                outstanding_balance,
                status,
                generated_date,
                due_date,
                approved_by
            )
            VALUES (
                gen_random_uuid(),
                NOW(),
                NOW(),
                reading_record.customer_id,
                reading_record.meter_id,
                reading_record.reading_id,
                p_month,
                p_year,
                reading_record.consumption,
                calculated_unit_charge,
                calculated_service_charge,
                calculated_vat_amount,
                0.00,
                calculated_total_amount,
                0.00,
                calculated_total_amount,
                'PENDING',
                CURRENT_DATE,
                CURRENT_DATE + 30,
                NULL
            );
        EXCEPTION
            WHEN OTHERS THEN
                RAISE WARNING 'Skipping meter reading %: %',
                    reading_record.reading_id,
                    SQLERRM;
        END;
    END LOOP;

    CLOSE reading_cursor;
END;
$$;
