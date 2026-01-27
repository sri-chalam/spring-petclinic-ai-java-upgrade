package org.springframework.samples.petclinic.upgradetest;

import java.util.Random;

/**
 * Using java.util.Random Java 21: May suggest using RandomGenerator or enhanced random
 * APIs
 */
public class RandomExample {

	private final Random random = new Random();

	public int generateToken() {
		return random.nextInt(1000000);
	}

	public String generateId() {
		return "ID-" + random.nextInt(100000);
	}

}
