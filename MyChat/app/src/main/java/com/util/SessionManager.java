package com.util;

import org.apache.mina.core.session.IoSession;

public class SessionManager {
    private static SessionManager mInstance = null;
    private IoSession mSession;

    public static SessionManager getInstance() {
        if (mInstance == null) {
            synchronized (SessionManager.class) {
                if (mInstance == null) {
                    mInstance = new SessionManager();
                }
            }
        }
        return mInstance;
    }

    private SessionManager() {
    }

    void setSession(IoSession session) {
        this.mSession = session;
    }

    public void writeToServer(Object message) {
        if (mSession != null) {
            mSession.write(message);
        }
    }

    public void closeSession() {
        if (mSession != null) {
            mSession.closeOnFlush();
        }
    }

    public void removeSession() {
        this.mSession = null;
    }

}
