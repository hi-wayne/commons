package com.hiwayne.commons.pattern.check;
 
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

public class PatternCollect {

	public static final String realname_shield = "111|222";

	/**
	 * 注册的用户正式姓名，必填 不能有关键字，第一个必须为中文
	 * 
	 * @param realname
	 * @return
	 */
	public static int reg_realname(String realname) {
		if (realname == null) {
			return -1;
		}
		if (realname
				.matches("[\\u4e00-\\u9fa5]{2,4}([0-9a-zA-Z]){0,2}([\\u4e00-\\u9fa5]){0,2}")) {
			return 0;
		} else {
			return -1;
		}
	}

	/**
	 * 邮件是否合法，必填 最少九位，最大36
	 * 
	 * @param email
	 * @return
	 */
	public static int reg_mail(String email) {
		if (email == null || email.length() <= 9 || email.length() >= 36) {
			return -1;
		}
		String mailreg = "^([0-9a-zA-Z]([-.\\w]*[0-9a-zA-Z])*@(([0-9a-zA-Z])+([-\\w]*[0-9a-zA-Z]){1,12}\\.)+[a-zA-Z]{2,5})$";

		if (email.matches(mailreg)) {//
			return 0;
		} else {
			return -1;
		}
	}

	/**
	 * 注册通行证名称(小写字母下划线4到15位)，必填
	 * 
	 * @param passport
	 * @return
	 */
	public static int reg_passport(String passport) {
		if (passport == null) {
			return -1;
		}
		if (passport.matches("[a-z0-9]{4,16}")) {
			return 0;
		} else {
			return -1;
		}
	}

	/**
	 * 密码
	 * 
	 * @param passport
	 * @return
	 */
	public static int reg_pwd(String pwd) {
		if (pwd == null) {
			return -1;
		}
		if (pwd.matches("[a-zA-Z0-9\\x21-\\x7E]{6,16}")) {
			return 0;
		} else {
			return -1;
		}
	}

	/**
	 * 手机号码
	 * 
	 * @param phone
	 * @return
	 */
	public static int reg_mobilephone(String phone) {
		String ss = "^(13[0-9]|15[0|3|6|7|8|9]|18[8|9])\\d{8}$";
		if (phone == null || phone.length() <= 4 || phone.length() >= 16) {
			return -1;
		}
		if (phone.matches(ss)) {
			return 0;
		} else {
			return -1;
		}
	}

	public static int chinaWord(String mbAnswer, int minLength, int maxLength) {
		if (mbAnswer == null) {
			return -1;
		}
		if (mbAnswer.matches("[\\w\\u4e00-\\u9fa5]{" + minLength + ","
				+ maxLength + "}")) {
			return 0;
		} else {
			return -1;
		}
	}

	public static int numberWord(String realname, int minLength, int maxLength) {
		if (realname == null) {
			return -1;
		}
		if (realname.matches("[0-9]{" + minLength + "," + maxLength + "}")) {
			return 0;
		} else {
			return -1;
		}
	}

	// 正数
	public static boolean isNumeric(String info) {
		if (info == null) {
			return false;
		}
		if (info.matches("^[0-9]*$")) {// a.a@a.a.a
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 密码等级计算1低级 2中级 3高级
	 * 
	 * @param pwd
	 * @return
	 */
	public static int pwdLevel(String pwd) {
		if (pwd == null || !pwd.matches("[a-z0-9A-Z]{6,12}$")) {
			return -1;
		}
		int ple = 0;
		if (Pattern.compile("[0-9]").matcher(pwd).find()) {//
			ple = ple + 1;
		}
		if (Pattern.compile("[a-z]").matcher(pwd).find()) {//
			ple = ple + 1;
		}
		if (Pattern.compile("[A-Z]").matcher(pwd).find()) {//
			ple = ple + 1;
		}
		char[] charArray = pwd.toUpperCase().toCharArray();
		int arrayLe = charArray.length;
		int charSum = 0;
		int max = 0;
		int avg = 0;
		for (int i = 0; i < arrayLe; i++) {
			int cui = (int) charArray[i];
			charSum = charSum + cui;
			if (cui > max) {
				max = cui;
			}
		}
		avg = charSum / arrayLe;
		if (Math.abs(avg - max) <= 4) {
			if (ple > 1) {
				ple = ple - 1;
			}
		}
		return ple;
	}

	public static Date idnumberTobirthday(String IDStr) throws ParseException {

		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String idstr17 = "";
		if (IDStr.length() == 18) {// 18位号码取前17位
			idstr17 = IDStr.substring(0, 17);
		} else if (IDStr.length() == 15) {// 15位号码在日期前补19
			idstr17 = IDStr.substring(0, 6) + "19" + IDStr.substring(6, 15);
		}
		String strYear = idstr17.substring(6, 10);// 年份
		String strMonth = idstr17.substring(10, 12);// 月份
		String strDay = idstr17.substring(12, 14);// 月份

		StringBuffer strDateBuffer = new StringBuffer(strYear);
		strDateBuffer.append("-");
		strDateBuffer.append(strMonth);
		strDateBuffer.append("-");
		strDateBuffer.append(strDay);
		Date inputDate = null;
		inputDate = df.parse(strDateBuffer.toString());
		return inputDate;
	}

	/**
	 * 身份证号码 大于6岁，并且是1950-10-31后出生的15或者18位身份证 -1不能为空 -2位数不对 -3身份证有无效符号 -4生日不对
	 * -5校验位
	 * 
	 * @param IDStr
	 * @return
	 */
	public static int reg_idnumber(String IDStr) {
		try {
			/**
			 * 检验码校对表
			 */
			String[] ValCodeArr = { "1", "0", "x", "9", "8", "7", "6", "5",
					"4", "3", "2" };
			/**
			 * 从第18到第1位的权
			 */
			int verify18Rights[] = { 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10,
					5, 8, 4, 2, 1 };
			String idstr17 = "";

			if (IDStr == null) {
				return -1;
			}
			if (IDStr.length() != 15 && IDStr.length() != 18) {// 码长度应该为15位或18位
				return -2;
			}

			// 全部转换成小写
			IDStr = IDStr.toLowerCase();

			if (IDStr.length() == 18) {// 18位号码取前17位
				idstr17 = IDStr.substring(0, 17);
			} else if (IDStr.length() == 15) {// 15位号码在日期前补19
				idstr17 = IDStr.substring(0, 6) + "19" + IDStr.substring(6, 15);
			}
			if (isNumeric(idstr17) == false) {// 验证是否都为数字构成（18位验证前17位，15位全部验证）
				return -3;
			}

			String strYear = idstr17.substring(6, 10);// 年份
			String strMonth = idstr17.substring(10, 12);// 月份
			String strDay = idstr17.substring(12, 14);// 月份

			if (isDate(strYear + "-" + strMonth + "-" + strDay) == false) {// 出生年月是否有效,必须位有效日期并且是1930-01-01日后的
				return -4;
			}

			// if (IdNumberArea.getAreaCodeMap() != null
			// && IdNumberArea.getAreaCodeMap().get(
			// idstr17.substring(0, 6)) == null) {// 地区码
			// return -4;
			// }

			// 计算校验位
			int TotalmulAiWi = 0;
			for (int i = 0; i < 17; i++) {
				TotalmulAiWi = TotalmulAiWi
						+ Integer.parseInt(String.valueOf(idstr17.charAt(i)))
						* verify18Rights[i];
			}
			int modValue = TotalmulAiWi % 11;
			String strVerifyCode = ValCodeArr[modValue];// 计算出的校验位
			idstr17 = idstr17 + strVerifyCode;

			if (IDStr.length() == 18) {
				if (idstr17.equals(IDStr.toLowerCase()) == false) {// 身份证无效，最后一位字母错误
					return -5;
				} else {
					return 0;
				}
			} else if (IDStr.length() == 15) {
				return 0;
			} else {
				return -1;
			}
		} catch (Exception e) {
			return -1;
		}
	}

	private static boolean isDate(String strDate) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date inputDate = null;
		Date startDate = null;
		if (strDate == null) {
			return false;
		}
		try {
			df.setLenient(false);// 这个的功能是不把1996-13-3 转换为1997-1-3
			inputDate = df.parse(strDate);
			if (startDate == null) {
				startDate = df.parse("1930-01-01");
			}
			if (inputDate.getTime() < startDate.getTime()) {
				return false;
			}
			return inputDate.after(startDate);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static void main(String[] a4rg) {
		System.out.println(reg_pwd("?>}{1111"));
	}
}
