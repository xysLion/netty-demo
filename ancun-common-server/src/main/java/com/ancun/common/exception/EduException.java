package com.ancun.common.exception;


/**
 * 异常类
 * 
 * @author Administrator
 * 
 */
public class EduException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private int code;

	private Object[] params = null;

	public EduException() {}

	public EduException(String msg) {
		super(msg);
	}

	public EduException(Throwable cause) {
		super(cause);
	}

	public EduException(String message, Throwable cause) {
		super(message, cause);
	}

	public EduException(int code) {
		this.code = code;
	}

	public EduException(int code, String msg) {
		super(msg);
		this.code = code;
	}

	public EduException(int code, String msg, Throwable cause) {
		super(msg, cause);
		this.code = code;
	}

	public EduException(int code, Object[] params) {
		this(code);
		this.params = params;
	}

	public int getCode() {
		return code;
	}

	public Object[] getParams() {
		return params;
	}
}
