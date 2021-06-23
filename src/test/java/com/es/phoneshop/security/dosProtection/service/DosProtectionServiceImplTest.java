package com.es.phoneshop.security.dosProtection.service;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DosProtectionServiceImplTest {

    private DosProtectionServiceImpl dosProtectionService;
    private int threshold;
    private String ip;
    private int clearInterval;

    @Before
    public void setup() {
        threshold = 5;
        ip = "0.0.0.1";
        clearInterval = 500;
        dosProtectionService = new DosProtectionServiceImpl(threshold, clearInterval);
    }

    @Test
    public void testIsAllowed() {
        for (int i = 0; i < threshold; i++) {
            assertTrue(dosProtectionService.isAllowed(ip));
        }
        assertFalse(dosProtectionService.isAllowed(ip));
    }

    @Test
    public void testClearsAfterInterval() throws InterruptedException {
        for (int i = 0; i < threshold; i++) {
            assertTrue(dosProtectionService.isAllowed(ip));
        }
        assertFalse(dosProtectionService.isAllowed(ip));

        Thread.sleep(clearInterval + 100);

        for (int i = 0; i < threshold; i++) {
            assertTrue(dosProtectionService.isAllowed(ip));
        }
        assertFalse(dosProtectionService.isAllowed(ip));


    }


}