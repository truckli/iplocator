package com.chanct.cddos.iplocator;

public class TestCase {

	public static void main(String[] args) {

		String fileName = TestCase.class.getClass().getResource("/").getPath()
				+ "mydata4vipday2.datx";
		System.out.println("fileName: " + fileName);

		IPLocator locator = new IPLocator(fileName);
		IPLocation IPregion = locator.locate("182.207.255.255");

		System.out.println("nation: " + IPregion.nation());
		System.out.println("province: " + IPregion.province());
		System.out.println("city: " + IPregion.city());
		System.out.println("county: " + IPregion.county());
		System.out.println("operator: " + IPregion.operator());
		System.out.println("longitude: " + IPregion.longitude());
		System.out.println("latitude: " + IPregion.latitude());

	}

}
