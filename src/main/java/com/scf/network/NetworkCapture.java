package com.scf.network;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Reads Chrome DevTools Protocol network events off the browser's
 * "performance" log.
 * <p>
 * This is deliberately version-agnostic: instead of depending on Selenium's
 * versioned {@code org.openqa.selenium.devtools.vNNN} packages (which must be
 * kept in lockstep with the installed Chrome version), we ask the browser to
 * forward raw CDP log entries to {@code driver.manage().logs()} and parse the
 * JSON ourselves. It is slightly less type-safe, but it never breaks when
 * Chrome auto-updates, which matters a lot for a framework meant to be
 * long-lived.
 * <p>
 * Requires the driver to have been created with performance logging enabled
 * (see {@link com.scf.driver.DriverFactory}) - i.e. Chrome or Edge.
 */
public class NetworkCapture {

    private final WebDriver driver;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public NetworkCapture(WebDriver driver) {
        this.driver = driver;
    }

    /** One HTTP request/response pair observed via CDP. */
    public record NetworkEvent(String method, String requestId, String url, Integer status, String mimeType) {
    }

    /**
     * Drains the performance log accumulated since the last call (Selenium
     * clears entries once you read them) and returns every network event
     * found in it.
     */
    public List<NetworkEvent> captureEvents() {
        List<NetworkEvent> events = new ArrayList<>();
        for (LogEntry entry : driver.manage().logs().get(LogType.PERFORMANCE)) {
            parseEntry(entry).ifPresent(events::add);
        }
        return events;
    }

    private java.util.Optional<NetworkEvent> parseEntry(LogEntry entry) {
        try {
            JsonNode root = objectMapper.readTree(entry.getMessage());
            JsonNode message = root.path("message");
            String method = message.path("method").asText("");
            if (!method.startsWith("Network.")) {
                return java.util.Optional.empty();
            }
            JsonNode params = message.path("params");
            String requestId = params.path("requestId").asText(null);

            if (method.equals("Network.requestWillBeSent")) {
                String url = params.path("request").path("url").asText(null);
                return java.util.Optional.of(new NetworkEvent(method, requestId, url, null, null));
            }
            if (method.equals("Network.responseReceived")) {
                JsonNode response = params.path("response");
                String url = response.path("url").asText(null);
                Integer status = response.has("status") ? response.path("status").asInt() : null;
                String mimeType = response.path("mimeType").asText(null);
                return java.util.Optional.of(new NetworkEvent(method, requestId, url, status, mimeType));
            }
            return java.util.Optional.empty();
        } catch (Exception e) {
            return java.util.Optional.empty();
        }
    }

    /**
     * Drains the performance log and returns the request headers seen on
     * every {@code Network.requestWillBeSent} event, keyed by request URL.
     * Useful for asserting that a header injected via
     * {@link NetworkConditions#setExtraHeaders(Map)} actually went out on
     * the wire.
     */
    public List<Map<String, String>> captureRequestHeaders() {
        List<Map<String, String>> allHeaders = new ArrayList<>();
        for (LogEntry entry : driver.manage().logs().get(LogType.PERFORMANCE)) {
            try {
                JsonNode root = objectMapper.readTree(entry.getMessage());
                JsonNode message = root.path("message");
                if (!"Network.requestWillBeSent".equals(message.path("method").asText(""))) {
                    continue;
                }
                JsonNode headersNode = message.path("params").path("request").path("headers");
                Map<String, String> headers = new LinkedHashMap<>();
                headersNode.fieldNames().forEachRemaining(name -> headers.put(name, headersNode.get(name).asText()));
                allHeaders.add(headers);
            } catch (Exception ignored) {
                // malformed/unrelated log line - skip
            }
        }
        return allHeaders;
    }

    /** Convenience filter: every response whose URL contains {@code fragment}. */
    public List<NetworkEvent> responsesContaining(String fragment) {
        return captureEvents().stream()
                .filter(e -> "Network.responseReceived".equals(e.method()))
                .filter(e -> e.url() != null && e.url().contains(fragment))
                .toList();
    }
}
