package org.diagnosis.algorithms.reconstruction;

import org.diagnosis.algorithms.entities.HitVector;
import org.diagnosis.algorithms.entities.TestCase;
import org.diagnosis.algorithms.reconstruction.graph.ExecutionGraph;

public interface Reconstruction {
    HitVector reconstruct(HitVector hitVector, TestCase testCase) throws ReconstructionException;

    ExecutionGraph getExecutionGraph();
}
