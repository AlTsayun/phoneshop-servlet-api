package com.es.phoneshop.utils.sessionLock;

import javax.servlet.http.HttpSession;
import java.util.concurrent.locks.Lock;

public interface SessionLockProvider {
    Lock getLock(HttpSession session);
}
