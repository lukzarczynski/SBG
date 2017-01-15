package main.piececlass;

import main.MoveUtil;
import main.OneMove;
import main.operator.Operator;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by lukasz on 06.12.16.
 */
public class XYYXLeaper extends PieceClass {

  private final String description;

  public XYYXLeaper(int x, int y) {
    final String[] targets = {"e", "p"};
    for (String t : targets) {
      moves.add(OneMove.parse(x, y, t));
      moves.add(OneMove.parse(-x, y, t));
      moves.add(OneMove.parse(x, -y, t));
      moves.add(OneMove.parse(-x, -y, t));

      if (x != y) {
        moves.add(OneMove.parse(y, x, t));
        moves.add(OneMove.parse(-y, x, t));
        moves.add(OneMove.parse(y, -x, t));
        moves.add(OneMove.parse(-y, -x, t));
      }
    }

    description = String.format("(%s,%s) or (%s,%s) leaper", x, y, y, x);
  }

  @Override
  public boolean matches(Set<OneMove> pieceMoves, Collection<Operator> op, Pair<Integer,Integer> xy) {
    final Set<OneMove> b = filterMoves(op, xy);
    return !b.isEmpty() && MoveUtil.containsAll(pieceMoves, b);
  }

  @Override
  public boolean matchesPrefix(Set<OneMove> moves, Collection<Operator> op, Pair<Integer,Integer> xy) {
    final Set<OneMove> b = filterMoves(op, xy);
    return !b.isEmpty() && MoveUtil.containsAllPrefixes(moves, b);
  }

  @Override
  public Set<OneMove> apply(Set<OneMove> pieceMoves, Collection<Operator> op, Pair<Integer,Integer> xy) {
    final Set<OneMove> b = filterMoves(op, xy);
    return b.isEmpty() ? pieceMoves : MoveUtil.subtract(pieceMoves, b);
  }


  @Override
  public String getDescription() {
    return description;
  }


}
