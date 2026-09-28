package com.scf.driver;

import java.util.concurrent.Semaphore;

/**
 * Caps how many browsers can be open at once across all threads.
 * <p>
 * With parallel scenarios <em>and</em> multiple sessions per scenario, the
 * number of live browser processes can grow quickly. Each session acquires a
 * permit before its browser launches and releases it on quit, so extra
 * sessions wait for a free slot instead of overloading the machine.
 */
public class BrowserSessionManager {

    private final Semaphore semaphore;

    public BrowserSessionManager(int maxSessions) {
        this.semaphore = new Semaphore(maxSessions, true);
    }

    public void acquire() {
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while waiting for a free browser session", e);
        }
    }

    public void release() {
        semaphore.release();
    }
}
