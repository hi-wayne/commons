package com.hiwayne.commons.aspect.cache;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface CHashingCacheGetAnnotation {
	/**
	 * 对传入的key是否进行md5处理
	 */
	boolean cacheKeyMD5() default false;

	/**
	 * 是否保存空值
	 * 
	 * @return
	 */
	boolean saveNull() default false;

	/**
	 * 保存空值的有效期
	 * 
	 * @return
	 */
	int saveNullLeftSecond() default 60;

	/**
	 * key的前缀
	 * 
	 * @return
	 */
	String keyPrefix();

	/**
	 * cache的时效,默认100秒
	 * 
	 * @return
	 */
	int maxLeftSecond() default 600;

	/**
	 * 是否进行互斥处理
	 */
	boolean mutual() default false;

	/**
	 * 是否使用cache标志参数
	 * 
	 * @return
	 */
	int useCacheIndex() default -1;

	/**
	 * 存活时间定义参数
	 * 
	 * @return
	 */
	int leftSecondIndex() default -1;

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

	/**
	 * 不参与路由的3级key值是方法中的第几个参数<br>
	 * 
	 * @return
	 */
	int keyParSub2Index() default -1;

	/**
	 * 不参与路由的4级key值是方法中的第几个参数<br>
	 * 
	 * @return
	 */
	int keyParSub3Index() default -1;
}
