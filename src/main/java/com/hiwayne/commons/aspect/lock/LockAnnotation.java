package com.hiwayne.commons.aspect.lock;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
/**
 *  LockAnnotation(maxLeft = 10, keyPrefix = "addLong", keyParMainIndex = 0)
 * <br>
 * @author wangwei
 *
 */
public @interface LockAnnotation {

	/**
	 * key的前缀
	 * 
	 * @return
	 */
	String keyPrefix();

	/**
	 * 锁定时间
	 * 
	 * @return
	 */
	int lockTime() default -1;

	/**
	 * 参与路由的key值是方法中的第几个参数<br>
	 * 根据cache存放节点的路由策略,此参数决定路由结果
	 * 
	 * @return
	 */
	int keyParMainIndex() default -1;

	/**
	 * 不参与路由的2级key值是方法中的第几个参数<br>
	 * 
	 * @return
	 */
	int keyParSubIndex() default -1;
}
