package me.ialistannen.toydnarnaasconverter.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Converts a base sequence to another.
 */
public class BaseSequenceConverter {

  private static final Map<Character, Character> mappings = new HashMap<>();

  static {
    mappings.put('A', 'T');
    mappings.put('T', 'A');

    mappings.put('C', 'G');
    mappings.put('G', 'C');
  }

  /**
   * Converts a DNA sequence to its complementary.
   *
   * <p><strong>Expects a DNA sequence!</strong>
   *
   * @param dnaSequence A DNA base sequence
   * @return The complementary sequence.
   */
  public static String getComplementarySequence(String dnaSequence) {
    StringBuilder result = new StringBuilder();

    for (char c : dnaSequence.toCharArray()) {
      Character complementaryChar = mappings.get(c);
      if (complementaryChar == null) {
        throw new IllegalArgumentException("Unknown character: '" + c + "'");
      }
      result.append(complementaryChar);
    }

    return result.toString();
  }

  /**
   * Converts a DNA sequence to the complementary mRNA sequence.
   *
   * @param dna The dna sequence
   * @return The complementary mRNA sequence
   */
  public static String dnaToMRna(String dna) {
    return getComplementarySequence(dna).replace('T', 'U');
  }
}
