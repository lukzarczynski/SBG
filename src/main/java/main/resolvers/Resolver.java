package main.resolvers;

import java.util.Set;

import main.OneMove;
import main.tree.ResolveResult;

/**
 * Created by lukasz on 07.12.16.
 */
public abstract class Resolver {
  protected boolean valid = true;
  protected int value;

  public Resolver(int value) {
    this.value = value;
  }

  public abstract boolean isApplicable(Set<OneMove> moves);

  public abstract ResolveResult apply(Set<OneMove> moves);

  public abstract String getDescription();

  public abstract boolean containsMove(OneMove oneMove);

  public int getValue() {
    return value;
  }

  public boolean isValid() {
    return valid;
  }

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

  public abstract boolean containsMovePrefix(OneMove om);
}
