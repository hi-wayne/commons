package com.hiwayne.commons.string;

import java.util.Random;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * 
 * @Description: string的一些操作封装
 * @date 2012
 * @version V1.0
 * @author wangwei
 * @since wangwei
 */
public class StringUtil {
	public static final String allChar = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	public static final String pex = "jkfdsjflkjoieruu..5;56[;2,7409064/*-4sfdsfrtdt56789oij478iuj5ryhgb3olkrl7.77gsr465454k67wn65489u6lkngfdz中国金口诀范德科伦sjlkiu..5;56[;2,7409064/*-4sfdsfrtdt56789oij478iuj5ryhgb3olkrl7.77gsr465454k67u.jkfdsjflkjoieruu..5;56[;2,7409064/*-4sfdsfrtdt56789oij478iuj5ryhgb3olkrl7.77gsr465454k67wn65489u6lkngfdz中国金口诀范德科伦sjlkiu..5;56[;2,7409064/*-4sfdsfrtdt56789oij478iuj5ryhgb3olkrl7.77gsr465454k67u..5;56[;2,7409064/*-4sfdsfrtdt56789oij478iuj5ryhgb3olkrl7.77gsr465454k67u..5;56[;2,7409064/*-4sfdsfrtdt56789oij478iuj5ryhgb3olkrl7.77gsr465454k67.5;56[;2,7409064/*-4sfdsfrtdt56789oij478iuj5ryhgb3olkrl7.77gsr465454k67u..5;56[;2,7409064/*-4sfdsfrtdt56789oij478iuj5ryhgb3olkrl7.77gsr465454k67";

	public static String md5(StringBuffer keyinfo) {
		if (keyinfo == null) {
			return "";
		}
		// 超长md5偏移量，预防社工库破解
		StringBuffer temp = new StringBuffer(keyinfo);
		temp.append(pex);
		return DigestUtils.md5Hex(temp.toString());
	}

	public static String getString(int[] intArray) {
		if (intArray == null) {
			return null;
		} else {
			StringBuffer info = new StringBuffer();
			for (int i : intArray) {
				info.append(i);
			}
			return info.toString();
		}
	}

	public static String getString(String[] strArray) {
		if (strArray == null) {
			return null;
		} else if (strArray.length == 0) {
			return "";
		} else {
			StringBuffer info = new StringBuffer();
			for (String i : strArray) {
				info.append(i);
			}
			return info.toString();
		}
	}

	/**
	 * 返回一个定长的随机字符串(只包含大小写字母、数字)
	 * 
	 * @param length
	 *            随机字符串长度
	 * @return 随机字符串
	 */
	public static String generateString(int length) {
		StringBuffer sb = new StringBuffer();
		Random random = new Random();
		for (int i = 0; i < length; i++) {
			sb.append(allChar.charAt(random.nextInt(allChar.length())));
		}
		return sb.toString();
	}

	/**
	 * 
	 * @param s
	 * @return
	 */
	public static String toHexString(String s) {
		String str = "";
		for (int i = 0; i < s.length(); i++) {
			int ch = (int) s.charAt(i);
			String s4 = Integer.toHexString(ch);
			str = str + s4;
		}
		return str;
	}

	public static void main(String[] args) {
		System.out.println(toHexString(generateString(6)));
	}
}
