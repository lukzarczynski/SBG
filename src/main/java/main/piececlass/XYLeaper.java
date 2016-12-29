package main.piececlass;

import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import main.MoveUtil;
import main.OneMove;
import main.operator.Operator;

/**
 * Created by lukasz on 06.12.16.
 */
public class XYLeaper extends PieceClass {

  private final String description;

  public XYLeaper(int x, int y) {
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

    description = String.format("(%s,%s) leaper", x, y);
  }

  @Override
  public boolean matches(Set<OneMove> pieceMoves, Operator... op) {
    final Set<OneMove> b = filterMoves(op);
    return !b.isEmpty() && MoveUtil.containsAll(pieceMoves, b);
  }

  @Override
  public boolean matchesPrefix(Set<OneMove> moves, Operator... op) {
    final Set<OneMove> b = filterMoves(op);
    return !b.isEmpty() && MoveUtil.containsAllPrefixes(moves, b);
  }

  @Override
  public Set<OneMove> apply(Set<OneMove> pieceMoves, Operator... op) {
    final Set<OneMove> b = filterMoves(op);
    return b.isEmpty() ? pieceMoves : MoveUtil.subtract(pieceMoves, b);
  }

  @Override
  public Set<OneMove> applyPrefix(Set<OneMove> moves, Operator... op) {
    final Set<OneMove> b = filterMoves(op);

    final Set<String> setStrings = b.stream().map(OneMove::toString).collect(Collectors.toSet());

    return b.isEmpty() ? moves :
        moves.stream()
            .map(OneMove::toString)
            .filter(om -> setStrings.stream().anyMatch(om::startsWith))
            .map(om -> {
              String bestMatch = setStrings.stream().filter(om::startsWith).sorted((c, a) -> Integer.compare(a
                  .length(), c.length())).findFirst().get();
              String ns = StringUtils.replaceOnce(om, bestMatch, "").trim();
              if (ns.startsWith("+")) {
                ns = ns.replaceFirst("\\+", "");
              }
              return ns;
            })
            .map(OneMove::parse)
            .flatMap(Collection::stream)
            .collect(Collectors.toSet());
  }

  @Override
  public String getDescription() {
    return description;
  }


}
