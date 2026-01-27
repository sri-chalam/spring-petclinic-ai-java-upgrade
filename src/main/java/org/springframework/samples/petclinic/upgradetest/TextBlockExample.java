package org.springframework.samples.petclinic.upgradetest;

/**
 * Multi-line string concatenation Java 21: Could use text blocks (already available in
 * Java 17, but may be refined)
 */
public class TextBlockExample {

	public String getJsonTemplate() {
		return "{\n" + "  \"name\": \"John\",\n" + "  \"age\": 30,\n" + "  \"city\": \"New York\"\n" + "}";
	}

	public String getSqlQuery() {
		return """
				SELECT u.id, u.name, u.email
				FROM users u
				WHERE u.active = true
				ORDER BY u.created_at DESC""";
	}

}
