package main.resolvers;

import main.model.OneMove;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by lzarczynski on 30.12.2016.
 */
public class PrefixResolveResult {

    private final Map<OneMove, OneMove> move;
    private final Set<OneMove> notMatchedPrefixes;

    public PrefixResolveResult(Map<OneMove, OneMove> move, Set<OneMove> notMatchedPrefixes) {
        this.move = move;
        this.notMatchedPrefixes = notMatchedPrefixes;
    }

    public Map<OneMove, OneMove> getMove() {
        return move;
    }

    public Set<OneMove> getNotMatchedPrefixes() {
        return notMatchedPrefixes;
    }

    public Set<OneMove> getSuffixes() {
        return move.values().stream().collect(Collectors.toSet());
    }

    public Set<OneMove> getPrefixes() {
        return move.keySet().stream().collect(Collectors.toSet());
    }
}
