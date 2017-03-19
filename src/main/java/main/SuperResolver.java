package main;

import main.resolvers.Resolver;
import main.resolvers.SimplePieceResolver;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by lukza on 19.03.2017.
 */
public class SuperResolver extends Resolver {

    private List<SimplePieceResolver> resolvers;

    public SuperResolver(List<SimplePieceResolver> resolvers, SimplePieceResolver r) {
        super(0);
        this.resolvers = new ArrayList<>(resolvers);
        this.resolvers.add(r);
    }

    @Override
    public String getDescription() {
        return resolvers.stream().map(SimplePieceResolver::getDescription).collect(Collectors.joining(", "));
    }
}
