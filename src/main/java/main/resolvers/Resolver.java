package main.resolvers;

import java.util.Set;

import main.OneMove;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Created by lukasz on 07.12.16.
 */
public abstract class Resolver {
  protected boolean valid = true;
  protected int value;

  public Resolver(int value) {
    this.value = value;
  }

  public abstract boolean isApplicable(Set<OneMove> moves, Pair<Integer,Integer> xy);

  public abstract ResolveResult apply(Set<OneMove> moves, Pair<Integer,Integer> xy);

  public abstract String getDescription();

  public int getValue() {
    return value;
  }

  public boolean isValid() {
    return valid;
  }

  public abstract boolean containsMove(OneMove oneMove, Pair<Integer, Integer> xy);

  public abstract boolean containsMovePrefix(OneMove oneMove, Pair<Integer, Integer> xy);

    @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Resolver resolver = (Resolver) o;

    return valid == resolver.valid && value == resolver.value;
  }

  @Override
  public int hashCode() {
    int result = (valid ? 1 : 0);
    result = 31 * result + value;
    return result;
  }
}
