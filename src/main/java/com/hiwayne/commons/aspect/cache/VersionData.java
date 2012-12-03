package com.hiwayne.commons.aspect.cache;

public interface VersionData {

	// 信息版本号
	public Long getVersionNo();

	// 信息是否还有效
	public boolean isExist();

	// 是否预先失效
	public boolean isBeforehandLost();

	// 延迟此信息失效的版本号
	public void delayLoseThis();

}
