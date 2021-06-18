package com.es.phoneshop.utils.sessionLock;

import javax.servlet.http.HttpSession;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

public interface SessionLockProvider {
    ReadWriteLock getLock(HttpSession session);
}
