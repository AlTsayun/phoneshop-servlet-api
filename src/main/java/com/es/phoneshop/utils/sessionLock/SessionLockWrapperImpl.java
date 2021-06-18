package com.es.phoneshop.utils.sessionLock;

import javax.servlet.http.HttpSession;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SessionLockWrapperImpl implements SessionLockWrapper {

    private final Lock globalDistributionLock = new ReentrantLock();

    @Override
    public SessionLockProvider getSessionLockProvider(String lockAttributeName) {
        return (session) -> {
            ReadWriteLock lock = (ReadWriteLock) session.getAttribute(lockAttributeName);
            if (lock == null) {
                globalDistributionLock.lock();
                try {
                    lock = (ReadWriteLock) session.getAttribute(lockAttributeName);
                    if (lock == null) {
                        lock = new ReentrantReadWriteLock();
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
