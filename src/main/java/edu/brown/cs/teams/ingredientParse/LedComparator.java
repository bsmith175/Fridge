package edu.brown.cs.teams.ingredientParse;

import java.util.Comparator;

public class LedComparator implements Comparator<String> {
  private String dest;

  public void setDest(String s) {
    dest = s;
  }

  @Override
  public int compare(String o1, String o2) {
    return Trie.getLedDistance(o1, dest) - Trie.getLedDistance(o2, dest);
  }
}
