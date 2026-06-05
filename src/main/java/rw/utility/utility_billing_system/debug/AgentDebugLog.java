package rw.utility.utility_billing_system.debug;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.LinkedHashMap;
import java.util.Map;

public final class AgentDebugLog {

    private static final String LOG_PATH = "debug-d3af32.log";
    private static final String SESSION_ID = "d3af32";
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private AgentDebugLog() {
    }

    public static void log(
            String location,
            String hypothesisId,
            String message,
            Map<String, Object> data,
            String runId
    ) {
        // #region agent log
        try {
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("sessionId", SESSION_ID);
            payload.put("hypothesisId", hypothesisId);
            payload.put("location", location);
            payload.put("message", message);
            payload.put("data", data);
            payload.put("timestamp", System.currentTimeMillis());
            if (runId != null) {
                payload.put("runId", runId);
            }
            Files.writeString(
                    Path.of(LOG_PATH),
                    MAPPER.writeValueAsString(payload) + System.lineSeparator(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            );
        } catch (Exception ignored) {
        }
        // #endregion
    }
}
