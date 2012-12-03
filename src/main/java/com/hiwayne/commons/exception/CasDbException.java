package com.hiwayne.commons.exception;


/**
 * cas进行数据库操作版本号已经变更异常
 * 
 * @author Administrator
 * 
 */
public class CasDbException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 251059565016300869L;

	private ExceptionInfo exceptionInfo;

	public CasDbException() {
		super();
	}

	public CasDbException(String s) {
		super(s);
	}

	public CasDbException(String message, Throwable cause) {
		super(message, cause);
	}

	public CasDbException(Throwable cause) {
		super(cause);
	}

	public CasDbException(ExceptionInfo exceptionInfo) {
		super();
		this.exceptionInfo = exceptionInfo;
	}

	public CasDbException(String s, ExceptionInfo exceptionInfo) {
		super(s);
		this.exceptionInfo = exceptionInfo;
	}

	public CasDbException(String message, Throwable cause,
			ExceptionInfo exceptionInfo) {
		super(message, cause);
		this.exceptionInfo = exceptionInfo;
	}

	public CasDbException(Throwable cause, ExceptionInfo exceptionInfo) {
		super(cause);
		this.exceptionInfo = exceptionInfo;
	}

	public ExceptionInfo getExceptionInfo() {
		return exceptionInfo;
	}

	public void setExceptionInfo(ExceptionInfo exceptionInfo) {
		this.exceptionInfo = exceptionInfo;
	}

	@Override
	public String toString() {
		return "CasDbException [exceptionInfo=" + exceptionInfo
				+ ", toString()=" + super.toString() + "]";
	}

}
