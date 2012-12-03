package com.hiwayne.commons;
import java.lang.reflect.Array;
public class ArrayUtil {

	/**
	 * 增加数组长度
	 * 
	 * @param array
	 * @param addLength
	 * @return new array
	 */
	public static Object arrayAddLength(Object array, int addLength) {
		Class<? extends Object> c = array.getClass();
		if (!c.isArray()) {
			return null;
		}
		Class<?> componentType = c.getComponentType();
		int length = Array.getLength(array);
		int newLength = length + addLength;
		Object newArray = Array.newInstance(componentType, newLength);
		System.arraycopy(array, 0, newArray, 0, length);
		return newArray;
	}
	
	public static void main(String[] args){
		String[] aa = new String[]{"aa","bb"};
		aa = (String[])ArrayUtil.arrayAddLength(aa, 2);
		System.out.println(aa.length);
		for (String s : aa) {
			System.out.println(s);
		}
	}
}
