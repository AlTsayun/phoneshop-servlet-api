package com.es.phoneshop.utils.sessionLock;

import javax.servlet.http.HttpSession;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SessionLockWrapperImpl implements SessionLockWrapper {

    private final Lock globalDistributionLock = new ReentrantLock();

    @Override
    public SessionLockProvider getSessionLockProvider(String lockAttributeName) {
        return (session) -> {
            Lock lock = (Lock) session.getAttribute(lockAttributeName);
            if (lock == null) {
                globalDistributionLock.lock();
                try {
                    lock = (Lock) session.getAttribute(lockAttributeName);
                    if (lock == null) {
                        lock = new ReentrantLock();
                        session.setAttribute(lockAttributeName, lock);
                    }
                } finally {
                    globalDistributionLock.unlock();
                }
            }
            return lock;
        };
    }
}
