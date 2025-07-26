package org.diagnosis.algorithms.walker;

import org.diagnosis.algorithms.walker.weightModifiers.NoModifier;
import org.diagnosis.algorithms.walker.weightModifiers.WeightModifier;

public class InformedRandomWalkStrategy extends WalkStrategy {
    WeightModifier modifier;
    public InformedRandomWalkStrategy(WeightModifier modifier) {
        super();
        this.modifier = modifier;
    }

    public InformedRandomWalkStrategy() {
        super();
        this.modifier = new NoModifier();
    }

    public WeightModifier getModifier() {
        return modifier;
    }

    @Override
    public String report() {
        return "(IR" + this.modifier.report() + ")";
    }
}
