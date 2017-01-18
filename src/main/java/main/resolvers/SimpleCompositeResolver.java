package main.resolvers;

import main.MoveUtil;
import main.model.OneMove;
import main.ParamsAndEvaluators;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by lzarczynski on 30.12.2016.
 */
public class SimpleCompositeResolver extends Resolver {

    private final SimplePieceResolver resolver1;
    private final SimplePieceResolver resolver2;

    public SimpleCompositeResolver(SimplePieceResolver resolver1, SimplePieceResolver resolver2) {
        super(ParamsAndEvaluators.evaluateCompositResolver(resolver1, resolver2));
        this.resolver1 = resolver1;
        this.resolver2 = resolver2;
    }

    @Override
    public boolean isApplicable(Set<OneMove> moves, Pair<Integer, Integer> xy) {
        if (resolver1.isApplicableForPrefixes(moves, xy)) {
            PrefixResolveResult prefixResolveResult = resolver1.applyForPrefixes(moves, xy);
            if (resolver2.isApplicable(prefixResolveResult.getSuffixes(), xy)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public ResolveResult apply(Set<OneMove> moves, Pair<Integer, Integer> xy) {
        PrefixResolveResult prefixResolveResult = resolver1.applyForPrefixes(moves, xy);
        ResolveResult apply = resolver2.apply(prefixResolveResult.getSuffixes(), xy);


        Set<OneMove> notParsed = prefixResolveResult.getNotMatchedPrefixes();
        notParsed.addAll
                (apply.getNotParsed()
                        .stream()
                        .map(notP ->
                                prefixResolveResult.getMove().entrySet().stream()
                                        .filter(p -> p.getValue().equals(notP))
                                        .map(Map.Entry::getKey)
                                        .collect(Collectors.toSet()))
                        .flatMap(Collection::stream)
                        .collect(Collectors.toSet()));

        return new ResolveResult(notParsed, MoveUtil.subtract(moves, notParsed));
    }

    @Override
    public String getDescription() {
        return resolver1.getDescription() + " and then " + resolver2.getDescription();
    }

}
