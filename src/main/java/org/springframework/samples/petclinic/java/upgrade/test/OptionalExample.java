package org.springframework.samples.petclinic.java.upgrade.test;

import java.util.Optional;

/**
 * Optional usage patterns
 * Java 21: May suggest using newer Optional methods or patterns
 */
public class OptionalExample {

    public String getValueOrDefault(Optional<String> opt) {
        if (opt.isPresent()) {
            return opt.get();
        } else {
            return "default";
        }
    }

    public void processIfPresent(Optional<String> opt) {
        if (opt.isPresent()) {
            String value = opt.get();
            System.out.println(value);
        }
    }
}
