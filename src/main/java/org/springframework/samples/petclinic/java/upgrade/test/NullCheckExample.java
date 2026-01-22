package org.springframework.samples.petclinic.java.upgrade.test;

/**
 * Traditional null checking
 * Java 21: May suggest Objects.requireNonNull or enhanced patterns
 */
public class NullCheckExample {

    public String processName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Name cannot be null");
        }
        return name.trim();
    }

    public int getLength(String str) {
        if (str != null) {
            return str.length();
        }
        return 0;
    }

    public String getDefault(String value, String defaultValue) {
        return value != null ? value : defaultValue;
    }
}

