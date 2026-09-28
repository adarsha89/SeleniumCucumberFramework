package com.scf.network;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chromium.HasCdp;

import java.util.List;
import java.util.Map;

/**
 * Manipulates the network via raw Chrome DevTools Protocol commands
 * ({@code Network.*} domain): blocking URLs, throttling bandwidth/latency,
 * forcing offline mode, and injecting extra headers.
 * <p>
 * Only Chromium-based browsers (Chrome, Edge) implement
 * {@link HasCdp#executeCdpCommand}; Firefox does not, so callers should guard
 * with {@link #isSupported(WebDriver)} first and skip/mark-pending otherwise.
 */
public class NetworkConditions {

    private final HasCdp cdp;

    public NetworkConditions(WebDriver driver) {
        if (!isSupported(driver)) {
            throw new UnsupportedOperationException(
                    "Network manipulation via CDP is only supported on Chromium-based browsers (Chrome/Edge), got: "
                            + driver.getClass().getSimpleName());
        }
        this.cdp = (HasCdp) driver;
        cdp.executeCdpCommand("Network.enable", Map.of());
    }

    public static boolean isSupported(WebDriver driver) {
        return driver instanceof HasCdp;
    }

    /** Blocks any request whose URL matches one of the given glob patterns, e.g. {@code "*.css"} or {@code "*analytics*"}. */
    public void blockUrls(String... urlPatterns) {
        cdp.executeCdpCommand("Network.setBlockedURLs", Map.of("urls", List.of(urlPatterns)));
    }

    public void clearBlockedUrls() {
        cdp.executeCdpCommand("Network.setBlockedURLs", Map.of("urls", List.of()));
    }

    /** Simulates the browser losing its network connection entirely. */
    public void goOffline() {
        emulateConditions(true, 0, 0, 0);
    }

    public void goOnline() {
        emulateConditions(false, 0, -1, -1);
    }

    /**
     * Throttles the connection. Throughput values are in bytes/sec;
     * pass -1 to leave a value unthrottled. Latency is in milliseconds.
     */
    public void throttle(int latencyMs, long downloadThroughputBytesPerSec, long uploadThroughputBytesPerSec) {
        emulateConditions(false, latencyMs, downloadThroughputBytesPerSec, uploadThroughputBytesPerSec);
    }

    /** Convenience preset approximating a slow 3G connection. */
    public void throttleToSlow3G() {
        throttle(400, 50 * 1024 / 8, 50 * 1024 / 8);
    }

    private void emulateConditions(boolean offline, int latencyMs, long download, long upload) {
        cdp.executeCdpCommand("Network.emulateNetworkConditions", Map.of(
                "offline", offline,
                "latency", latencyMs,
                "downloadThroughput", download,
                "uploadThroughput", upload
        ));
    }

    /** Adds/overwrites headers sent with every subsequent request. */
    public void setExtraHeaders(Map<String, String> headers) {
        cdp.executeCdpCommand("Network.setExtraHTTPHeaders", Map.of("headers", headers));
    }

    /** Disables the HTTP cache so every request hits the network - useful before capturing traffic. */
    public void disableCache() {
        cdp.executeCdpCommand("Network.setCacheDisabled", Map.of("cacheDisabled", true));
    }

    /** Restores default (unthrottled, online, no blocked URLs) network behaviour. */
    public void reset() {
        clearBlockedUrls();
        goOnline();
    }
}
