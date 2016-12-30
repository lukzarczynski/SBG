package main.piececlass;

import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import main.MoveUtil;
import main.OneMove;
import main.operator.Operator;

/**
 * Created by lukasz on 06.12.16.
 */
public class XYRider extends PieceClass {

  private final String description;

  public XYRider(int x, int y) {
    addAnyNumberOf(x, y);
    addAnyNumberOf(-x, y);
    addAnyNumberOf(x, -y);
    addAnyNumberOf(-x, -y);
    description = String.format("(%s,%s) rider", x, y);
  }

  @Override
  public boolean matches(Set<OneMove> pieceMoves, Collection<Operator> op) {
    final Set<OneMove> b = filterMoves(op);
    return !b.isEmpty() && MoveUtil.containsAll(pieceMoves, b);
  }

  @Override
  public boolean matchesPrefix(Set<OneMove> moves, Collection<Operator> op) {
    final Set<OneMove> b = filterMoves(op);
    return !b.isEmpty() && MoveUtil.containsAllPrefixes(moves, b);
  }

  @Override
  public Set<OneMove> apply(Set<OneMove> pieceMoves, Collection<Operator> op) {
    final Set<OneMove> b = filterMoves(op);
    return b.isEmpty() ? pieceMoves : MoveUtil.subtract(pieceMoves, b);
  }

  @Override
  public Map<OneMove, OneMove> applyPrefix(Set<OneMove> moves,
                                           Collection<Operator> op) {
    final Set<OneMove> b = filterMoves(op);

    final Set<String> setStrings = b.stream().map(OneMove::toString).collect(Collectors.toSet());

    return b.isEmpty() ? null :
        moves.stream()
            .filter(om -> setStrings.stream().anyMatch(ob -> om.toString().startsWith(ob)))
            .collect(Collectors.toMap(Function.identity(),
                o -> {
                  String om = o.toString();
                  String bestMatch =
                      setStrings.stream()
                          .filter(om::startsWith)
                          .sorted((c, a) -> Integer.compare(a.length(), c.length())).findFirst().get();
                  String ns = StringUtils.replaceOnce(om, bestMatch, "").trim();
                  if (ns.startsWith("+")) {
                    ns = ns.replaceFirst("\\+", "");
                  }
                  return OneMove.parse(ns).stream().findAny().orElse(null);
                }));
  }

  @Override
  public String getDescription() {
    return description;
  }

  private void addAnyNumberOf(int x, int y) {
    for (int i = 1; i < 8; i++) {
      for (int j = 1; j <= i; j++) {
        if (j * x > 7 || j * y > 7) {
          continue;
        }
        moves.addAll(OneMove.parse(String.format("(%s,%s,e)^%s", x, y, j)));
      }
    }
    addAnyNumberTaking(x, y);
  }

  private void addAnyNumberTaking(int x, int y) {
    for (int i = 1; i < 8; i++) {
      for (int j = 0; j < i; j++) {
        if (j * x > 7 || j * y > 7) {
          continue;
        }
        moves.addAll(OneMove.parse(String.format("(%s,%s,e)^%s(%s,%s,p)", x, y, j, x, y)));
      }
    }
  }
}
