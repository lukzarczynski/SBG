package main.piececlass;

import main.OneMove;
import main.operator.Operator;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by lukasz on 06.12.16.
 */
public abstract class PieceClass {

    protected Set<OneMove> moves = new HashSet<>();

    public abstract boolean matches(Set<OneMove> moves, Collection<Operator> operators, Pair<Integer, Integer> xy);

    public abstract boolean matchesPrefix(Set<OneMove> moves, Collection<Operator> operators, Pair<Integer, Integer> xy);

    public abstract Set<OneMove> apply(Set<OneMove> moves, Collection<Operator> operators, Pair<Integer, Integer> xy);

    public Map<OneMove, OneMove> applyPrefix(Set<OneMove> moves,
                                             Collection<Operator> op, Pair<Integer, Integer> xy) {
        final Set<OneMove> b = filterMoves(op, xy);

        final Set<String> setStrings = b.stream().map(OneMove::toString).collect(Collectors.toSet());

        return b.isEmpty() ? null :
                moves.stream()
                        .filter(om -> setStrings.stream().anyMatch(ob -> om.toString().startsWith(ob)))
                        .collect(Collectors.toMap(Function.identity(),
                                o -> getOneMove(setStrings, o)));
    }

    public abstract String getDescription();

    public Set<OneMove> filterMoves(Collection<Operator> op, Pair<Integer, Integer> xy) {
        return moves.stream()
                .filter(m -> op.stream().allMatch(o -> o.matches().test(m)))
                .map(m -> {
                    OneMove r = m;
                    for (Operator o : op) {
                        r = o.map().apply(r);
                    }
                    return r;
                })
                .filter(om -> om.isValid(xy))
                .collect(Collectors.toSet());
    }

    protected OneMove getOneMove(Set<String> setStrings, OneMove o) {
        String om = o.toString();
        String bestMatch =
                setStrings.stream()
                        .filter(om::startsWith)
                        .sorted((c, a) -> Integer.compare(a.length(), c.length())).findFirst().get();

        OneMove next = OneMove.parse(bestMatch).iterator().next();
        Pair<Integer, Integer> vector = next.getMoves().stream()
                .map(m -> Pair.of(m.getDx(), m.getDy())).reduce(Pair.of(0, 0), (p1, p2) -> Pair.of(p1.getKey() + p2.getKey(), p1.getValue() + p2.getValue()));

        String ns = StringUtils.replaceOnce(om, bestMatch, "").trim();
        if (ns.startsWith("+")) {
            ns = ns.replaceFirst("\\+", "");
        }
        return OneMove.parse(ns, vector).stream().findAny().orElse(OneMove.EMPTY_MOVE);
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
