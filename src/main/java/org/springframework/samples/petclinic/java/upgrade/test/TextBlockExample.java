package org.springframework.samples.petclinic.java.upgrade.test;

/**
 * Multi-line string concatenation
 * Java 21: Could use text blocks (already available in Java 17, but may be refined)
 */
public class TextBlockExample {

    public String getJsonTemplate() {
        return "{\n" +
                "  \"name\": \"John\",\n" +
                "  \"age\": 30,\n" +
                "  \"city\": \"New York\"\n" +
                "}";
    }

    public String getSqlQuery() {
        return "SELECT u.id, u.name, u.email\n" +
                "FROM users u\n" +
                "WHERE u.active = true\n" +
                "ORDER BY u.created_at DESC";
    }
}
