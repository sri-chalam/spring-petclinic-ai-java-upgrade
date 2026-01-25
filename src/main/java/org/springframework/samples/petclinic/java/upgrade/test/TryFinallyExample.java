package org.springframework.samples.petclinic.java.upgrade.test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class TryFinallyExample {

  public static String readFirstLine(String path) throws IOException {
    BufferedReader reader = new BufferedReader(new FileReader(path));
    try {
      return reader.readLine();
    } finally {
      reader.close();
    }
  }
}
