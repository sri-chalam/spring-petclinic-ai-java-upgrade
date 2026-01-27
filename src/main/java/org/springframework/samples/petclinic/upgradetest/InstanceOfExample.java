package org.springframework.samples.petclinic.upgradetest;

public class InstanceOfExample {

	public static String describe(Object obj) {
		if (obj instanceof String s) {
			return "String of length " + s.length();
		}
		else if (obj instanceof Integer i) {
			return "Integer value " + i;
		}
		return "Unknown";
	}

}
