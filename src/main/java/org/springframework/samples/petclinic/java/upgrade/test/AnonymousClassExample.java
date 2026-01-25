package org.springframework.samples.petclinic.java.upgrade.test;

public class AnonymousClassExample {

	public static Runnable createTask() {
		return new Runnable() {
			@Override
			public void run() {
				System.out.println("Running task");
			}
		};
	}

}
