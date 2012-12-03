package com.hiwayne.commons.exception;

/**
 * 拿锁超时异常
 * 
 * @author wangwei
 * 
 */
public class MutualLockTimeOutException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -591763271123756218L;

	/**
 * 
 */
	public MutualLockTimeOutException() {
	}

	/**
	 * 
	 * @param message
	 */
	public MutualLockTimeOutException(String message) {
		super(message);
	}
}