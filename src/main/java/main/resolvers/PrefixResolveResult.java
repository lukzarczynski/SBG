package main.resolvers;

import main.model.OneMove;

import java.util.Map;
import java.util.Set;

/**
 * Created by lzarczynski on 30.12.2016.
 */
public class PrefixResolveResult {

    private final Map<OneMove, Set<OneMove>> move;
    private final Set<OneMove> notMatchedPrefixes;

    public PrefixResolveResult(Map<OneMove, Set<OneMove>> move, Set<OneMove> notMatchedPrefixes) {
        this.move = move;
        this.notMatchedPrefixes = notMatchedPrefixes;
    }

    /**
     * @return key: prefix, value: set of suffixes
     */
    public Map<OneMove, Set<OneMove>> getMap() {
        return move;
    }

    public OneMove getAnySuffix() {
        return move.values().stream().filter(c -> !c.isEmpty()).findAny().get().stream().findAny().get();
    }

}
