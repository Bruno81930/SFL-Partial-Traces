package org.diagnosis.algorithms.reconstruction.techniques;

import org.diagnosis.algorithms.entities.HitVector;

public interface Technique {
    public HitVector reconstruct(HitVector hitVector);
}
