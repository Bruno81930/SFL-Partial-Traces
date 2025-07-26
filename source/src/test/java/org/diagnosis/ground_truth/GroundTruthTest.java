package org.diagnosis.ground_truth;

import org.diagnosis.algorithms.entities.Component;
import org.diagnosis.evaluation.ground_truth.GroundTruth;
import org.diagnosis.evaluation.ground_truth.GroundTruthException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GroundTruthTest {
    @Test
    public void test() throws GroundTruthException {
        GroundTruth groundTruth = new GroundTruth();
        groundTruth.addFixedMethod("openpnp.machine.reference.ReferenceCamera::new");
        assertEquals(groundTruth.getComponents().get(0), new Component("org.openpnp.machine.reference", "ReferenceCamera", "new"));
    }

    @Test
    public void givenAGroundTruthFile_whenReadFile_thenGroundTruthIsCreated() throws GroundTruthException {
        GroundTruth groundTruth = GroundTruth.readFile("src/test/resources/data/openpnp/1");
        assertEquals(groundTruth.getComponents().get(0), new Component("org.openpnp.machine.reference", "ReferenceCamera", "new"));
    }

    @Test
    public void givenAPatchFile_whenReadFile_thenGroundTruthIsCreated() throws GroundTruthException {
        GroundTruth groundTruth = GroundTruth.readFile("src/test/resources/data/cli/1");
    }

    @Test
    public void givenAPatchFile_whenReadFile_thenGroundTruthIsCreated2() throws GroundTruthException {
        GroundTruth groundTruth = GroundTruth.readFile("src/test/resources/data/compress/4");
    }
}
