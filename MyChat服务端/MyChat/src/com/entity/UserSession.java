package com.entity;

import org.apache.mina.core.session.IoSession;

public class UserSession {
	public static final int ONLINE = 1, UNONLINE = 2;
	private String username;
	private IoSession session;
	private String IMEI;
	private int status = ONLINE;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public IoSession getSession() {
		return session;
	}

	public void setSession(IoSession session) {
		this.session = session;
	}

	public String getIMEI() {
		return IMEI;
	}

	public void setIMEI(String iMEI) {
		IMEI = iMEI;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public static int getOnline() {
		return ONLINE;
	}

	public static int getUnonline() {
		return UNONLINE;
	}

	@Override
	public String toString() {
		return "UserSession [username=" + username + ", session=" + session + ", IMEI=" + IMEI + ", status=" + status
				+ "]";
	}

}
