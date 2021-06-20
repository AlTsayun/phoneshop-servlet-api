package com.es.phoneshop.security.dosProtection.service;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

public class DosProtectionServiceImpl implements DosProtectionService {

    private long threshold;

    private Map<String, Long> ipToRequestsCount;

    public DosProtectionServiceImpl(long threshold, long clearInterval) {
        this.threshold = threshold;
        this.ipToRequestsCount = new ConcurrentHashMap<>();
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                ipToRequestsCount.clear();
            }
        }, clearInterval, clearInterval);
    }

    @Override
    public boolean isAllowed(String ip) {
        return ipToRequestsCount.merge(ip, 1L, Long::sum) <= threshold;
    }
}
