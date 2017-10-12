package main.resolvers;

import java.util.Collection;

import main.model.OneMove;

/**
 * Created by lukza on 26.12.2016.
 */
public class ResolveResult {

    private final Collection<OneMove> parsed;
    private final Collection<OneMove> notParsed;

    public ResolveResult(Collection<OneMove> notParsed, Collection<OneMove> parsed) {
        this.parsed = parsed;
        this.notParsed = notParsed;
    }

    public Collection<OneMove> getParsed() {
        return parsed;
    }

    public Collection<OneMove> getNotParsed() {
        return notParsed;
    }
}
