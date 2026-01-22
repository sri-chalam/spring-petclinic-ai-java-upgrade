package org.springframework.samples.petclinic.java.upgrade.test;

public class EnumSwitchExample {

  enum Level {
    LOW,
    MEDIUM,
    HIGH
  }

  public static int priority(Level level) {
    switch (level) {
      case HIGH:
        return 3;
      case MEDIUM:
        return 2;
      case LOW:
        return 1;
      default:
        throw new IllegalStateException("Unexpected value: " + level);
    }
  }
}
