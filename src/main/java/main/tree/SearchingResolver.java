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

    protected Set<Operator> operators;
    protected int priority;

    public SearchingResolver(Set<Operator> operators) {
        this.operators = operators;
        this.priority = this.operators.stream().map(o -> o.value).reduce(0, Integer::sum);
        this.priority += (operators.size() - 1) * 20;
    }

    public Collection<Resolver> search(Set<OneMove> moves, Piece piece) {
        return Stream.concat(
                XYClassSearcher.findLeapers(moves, piece, operators).stream(),
                XYClassSearcher.findRiders(moves, piece, operators).stream()
        )
                .map(pc -> new Resolver(pc, operators))
                .filter(Resolver::isValid).collect(Collectors.toSet());
    }
}
