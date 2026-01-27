package org.springframework.samples.petclinic.upgradetest;

public class PatternSwitchCandidate {

	public static String format(Object obj) {
		if (obj == null) {
			return "null";
		}
		else if (obj instanceof String) {
			return "String";
		}
		else if (obj instanceof Integer) {
			return "Integer";
		}
		else if (obj instanceof Long) {
			return "Long";
		}
		else {
			return "Other";
		}
	}

}
