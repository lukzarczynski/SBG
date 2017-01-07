package main.resolvers;

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
public class SimplePieceResolverSearcher {

    public static Collection<SimplePieceResolver> search(Set<OneMove> moves, Set<Operator>
            operators) {
        return Stream.concat(
                XYClassSearcher.findLeapers(moves, operators).stream(),
                XYClassSearcher.findRiders(moves, operators).stream()
        )
                .map(pc -> new SimplePieceResolver(pc, operators))
                .filter(Resolver::isValid).collect(Collectors.toSet());
    }

    public static Collection<SimplePieceResolver> searchPrefix(Set<OneMove> moves, Set<Operator>
            operators) {
        return Stream.concat(
                XYClassSearcher.findLeapersForPrefix(moves, operators).stream(),
                XYClassSearcher.findRidersForPrefix(moves, operators).stream()
        )
                .map(pc -> new SimplePieceResolver(pc, operators))
                .filter(Resolver::isValid).collect(Collectors.toSet());
    }
}
