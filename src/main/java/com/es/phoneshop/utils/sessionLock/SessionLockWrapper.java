package com.es.phoneshop.utils.sessionLock;

public interface SessionLockWrapper {
    SessionLockProvider getSessionLockProvider(String lockAttributeName);
}
