package main.resolvers;

import main.ParamsAndEvaluators;

/**
 * Created by lzarczynski on 30.12.2016.
 */
public class SimpleCompositeResolver extends Resolver {

    private final SimplePieceResolver resolver1;
    private final SimplePieceResolver resolver2;

    public SimpleCompositeResolver(SimplePieceResolver resolver1, SimplePieceResolver resolver2) {
        super(ParamsAndEvaluators.fko1ko2(resolver1, resolver2));
        this.resolver1 = resolver1;
        this.resolver2 = resolver2;
    }

    @Override
    public String getDescription() {
        return resolver1.getDescription() + " and then " + resolver2.getDescription();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        SimpleCompositeResolver that = (SimpleCompositeResolver) o;

        if (!resolver1.equals(that.resolver1)) return false;
        return resolver2.equals(that.resolver2);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + resolver1.hashCode();
        result = 31 * result + resolver2.hashCode();
        return result;
    }

    public SimplePieceResolver getResolver1() {
        return resolver1;
    }

    public SimplePieceResolver getResolver2() {
        return resolver2;
    }
}
