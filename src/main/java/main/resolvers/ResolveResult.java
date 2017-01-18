package main.resolvers;

import main.model.OneMove;

import java.util.Set;

/**
 * Created by lukza on 26.12.2016.
 */
public class ResolveResult {

    private final Set<OneMove> parsed;
    private final Set<OneMove> notParsed;

    public ResolveResult(Set<OneMove> notParsed, Set<OneMove> parsed) {
        this.parsed = parsed;
        this.notParsed = notParsed;
    }

    public Set<OneMove> getParsed() {
        return parsed;
    }

    public Set<OneMove> getNotParsed() {
        return notParsed;
    }
}
