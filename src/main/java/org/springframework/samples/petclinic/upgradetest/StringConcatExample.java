package org.springframework.samples.petclinic.upgradetest;

import java.util.List;

/**
 * String concatenation patterns Java 21: May use String templates or optimized
 * concatenation
 */
public class StringConcatExample {

	public String buildMessage(String name, int age) {
		return "Hello " + name + ", you are " + age + " years old.";
	}

	public String formatList(List<String> items) {
		String result = "";
		for (String item : items) {
			result = result + item + ", ";
		}
		return result;
	}

	public String createJson(String key, String value) {
		return "{\"" + key + "\": \"" + value + "\"}";
	}

}
