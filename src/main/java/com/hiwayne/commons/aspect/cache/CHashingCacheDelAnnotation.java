package com.hiwayne.commons.aspect.cache;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface CHashingCacheDelAnnotation {

	/**
	 * 对传入的key是否进行md5处理
	 */
	boolean cacheKeyMD5() default false;

	/**
	 * key的前缀
	 * 
	 * @return
	 */
	String[] keyPrefix();

	/**
	 * 参与路由的key值是方法中的第几个参数<br>
	 * 根据cache存放节点的路由策略,此参数决定路由结果
	 * 
	 * @return
	 */
	int[] keyParMainIndex() default {};

	/**
	 * 不参与路由的2级key值是方法中的第几个参数<br>
	 * 
	 * @return
	 */
	int[] keyParSubIndex() default {};
}
