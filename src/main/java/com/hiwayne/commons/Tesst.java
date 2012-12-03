package com.hiwayne.commons;

public class Tesst {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		System.out.println(ttt(null));
	}

	public static String ttt(String abc) {
		try {
			System.out.println("try");
			abc.trim();
			return abc;
		} catch (Exception e) {
			System.out.println("catch");
			throw new RuntimeException(e);
		} finally {
			System.out.println("finally");
		}
	}

}
