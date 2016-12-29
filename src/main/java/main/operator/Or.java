package main.operator;

import main.OneMove;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by lukza on 28.12.2016.
 */
public class Or extends Operator {

    private final Set<Operator> operatorSet;
    private final String description;

    public Or(Operator... operatorSet) {
        this(new HashSet<>(Arrays.asList(operatorSet)));
    }

    public Or(Set<Operator> operatorSet) {
        super(operatorSet.stream().map(Operator::getValue).reduce(0, Integer::sum));
        this.operatorSet = operatorSet;
        this.description = operatorSet.stream()
                .map(Operator::getDescription)
                .sorted()
                .collect(Collectors.joining(" or "));
    }

    @Override
    public Predicate<OneMove> matches() {
        return om -> operatorSet.stream().anyMatch(o -> o.matches().test(om));
    }

    @Override
    public String getDescription() {
        return description;
    }
}
