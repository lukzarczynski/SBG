package main.tree;

import main.OneMove;
import main.Piece;
import main.operator.Operator;
import main.piececlass.XYClassSearcher;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by lukza on 28.12.2016.
 */
public class SearchingResolver {

    public static Collection<Resolver> search(Set<OneMove> moves, Piece piece, Set<Operator>
        operators) {
        return Stream.concat(
                XYClassSearcher.findLeapers(moves, piece, operators).stream(),
                XYClassSearcher.findRiders(moves, piece, operators).stream()
        )
                .map(pc -> new Resolver(pc, operators))
                .filter(Resolver::isValid).collect(Collectors.toSet());
    }
}
