package edu.brown.cs.teams.ingredientParse;


/**
 * This class represents a 2 element tuple.
 *
 * @param <T1> - The type of the object of the first element in the Pair.
 * @param <T2> - The type of the object of the second element in the Pair.
 */
public class Pair<T1, T2> {
  private T1 first;
  private T2 second;
  static final int HASH = 17;
  static final int HASH_MULTIPLIER = 31;

  /**
   * This constructor takes in three Objects and sets the respective instance
   * variables one, two, and three to their values.
   *
   * @param one - The first object of the triple.
   * @param two - The second object of the triple.
   */
  public Pair(T1 one, T2 two) {
    this.first = one;
    this.second = two;
  }

  /**
   * Returns the first element of the triple.
   *
   * @return this.one - The first object in the triple.
   */
  public T1 getFirst() {
    return this.first;
  }

  /**
   * Returns the second element of the triple.
   *
   * @return this.two - The second object in the triple.
   */
  public T2 getSecond() {
    return this.second;
  }


  @Override
  public int hashCode() {
    int hash = HASH;
    hash = HASH_MULTIPLIER * hash * this.first.hashCode();
    hash = HASH_MULTIPLIER * hash * this.second.hashCode();
    return hash;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Pair)) {
      return false;
    }
    @SuppressWarnings("unchecked")
    Pair<T1, T2> trip = (Pair<T1, T2>) o;
    return (trip.getFirst().equals(first) && trip.getSecond().equals(second));

  }

}

