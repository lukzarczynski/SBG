package main;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import main.resolvers.Resolver;
import main.resolvers.SimplePieceResolver;

/**
 * Created by lukza on 19.03.2017.
 */
public class SuperResolver extends Resolver {

    private List<SimplePieceResolver> resolvers;

    public SuperResolver(List<SimplePieceResolver> resolvers, SimplePieceResolver r) {
        super(resolvers.stream().map(ParamsAndEvaluators::fko).reduce(1, (r1, r2) -> r1 * r2));
        this.resolvers = new ArrayList<>(resolvers);
        this.resolvers.add(r);
    }

    @Override
    public String getDescription() {
        return resolvers.stream().map(SimplePieceResolver::getDescription).collect(Collectors.joining(", "));
    }

    public List<SimplePieceResolver> getResolvers() {
        return resolvers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        SuperResolver that = (SuperResolver) o;

        return resolvers.equals(that.resolvers);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + resolvers.hashCode();
        return result;
    }
}
