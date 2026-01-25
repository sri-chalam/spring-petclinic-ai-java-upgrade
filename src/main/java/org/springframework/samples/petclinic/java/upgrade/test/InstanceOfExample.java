package org.springframework.samples.petclinic.java.upgrade.test;

public class InstanceOfExample {

	public static String describe(Object obj) {
		if (obj instanceof String) {
			String s = (String) obj;
			return "String of length " + s.length();
		}
		else if (obj instanceof Integer) {
			Integer i = (Integer) obj;
			return "Integer value " + i;
		}
		return "Unknown";
	}

}
