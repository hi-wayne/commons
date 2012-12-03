package com.hiwayne.commons.exception;

import java.io.Serializable;

public class ExceptionInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8837397648473663793L;
	private Integer code;
	private String desc;
	private String showstr;

	public ExceptionInfo(Integer code, String desc, String showstr) {
		super();
		this.code = code;
		this.desc = desc;
		this.showstr = showstr;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getShowstr() {
		return showstr;
	}

	public void setShowstr(String showstr) {
		this.showstr = showstr;
	}

	@Override
	public String toString() {
		return "ExceptionInfo [code=" + code + ", desc=" + desc + ", showstr="
				+ showstr + "]";
	}

}
