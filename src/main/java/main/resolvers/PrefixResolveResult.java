package main.resolvers;

import java.util.Map;
import java.util.Set;

import main.model.OneMove;

/**
 * Created by lzarczynski on 30.12.2016.
 */
public class PrefixResolveResult {

    private final Map<OneMove, Set<OneMove>> move;

    public PrefixResolveResult(Map<OneMove, Set<OneMove>> move) {
        this.move = move;
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
