package main.resolvers;

import main.ParamsAndEvaluators;

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
    public String getDescription() {
        return resolver1.getDescription() + " and then " + resolver2.getDescription();
    }

}
