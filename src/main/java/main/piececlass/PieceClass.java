package main.piececlass;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import main.OneMove;
import main.operator.None;
import main.operator.Operator;

/**
 * Created by lukasz on 06.12.16.
 */
public abstract class PieceClass {

  protected Set<OneMove> moves = new HashSet<>();

  public abstract boolean matches(Set<OneMove> moves, Collection<Operator> operators);

  public abstract boolean matchesPrefix(Set<OneMove> moves, Collection<Operator> operators);

  public abstract Set<OneMove> apply(Set<OneMove> moves, Collection<Operator> operators);

  public abstract Map<OneMove, OneMove> applyPrefix(Set<OneMove> moves, Collection<Operator>
      operators);

  public abstract String getDescription();

  public Set<OneMove> filterMoves(Collection<Operator> op) {
    return moves.stream()
        .filter(m -> op.stream().allMatch(o -> o.matches().test(m)))
        .map(m -> {
          OneMove r = m;
          for (Operator o : op) {
            r = o.map().apply(r);
          }
          return r;
        })
        .collect(Collectors.toSet());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    PieceClass that = (PieceClass) o;

    return moves.equals(that.moves);
  }

  @Override
  public int hashCode() {
    return moves.hashCode();
  }
}
