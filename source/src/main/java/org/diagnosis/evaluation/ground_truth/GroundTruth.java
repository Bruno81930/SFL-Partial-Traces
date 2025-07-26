package org.diagnosis.evaluation.ground_truth;

import org.diagnosis.algorithms.entities.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class GroundTruth {

    List<Component> fixedComponents;

    public GroundTruth() {
        this.fixedComponents = new ArrayList<>();
    }

    public static GroundTruth readFile(String bugPath) throws GroundTruthException{
        Path groundTruthPath = Paths.get(bugPath, "ground_truth.txt");
        if (!Files.exists(groundTruthPath)) {
            throw new GroundTruthException("Ground truth file does not exist");
        }
        GroundTruth groundTruth = new GroundTruth();
        try {
            for (String line : Files.readAllLines(groundTruthPath)) {
                groundTruth.addFixedMethod(line);
            }
        } catch (IOException e) {
            throw new GroundTruthException("Failed to read the ground truth", e);
        }
        return groundTruth;
    }


    public void addFixedMethod(String fixedMethod) throws GroundTruthException {
        int lastDotIndex = fixedMethod.lastIndexOf('.');
        int doubleColonIndex = fixedMethod.indexOf("::");

        if (lastDotIndex == -1 || doubleColonIndex == -1 || doubleColonIndex < lastDotIndex) {
            throw new GroundTruthException("Invalid input format");
        }

        String fullClassPath = fixedMethod.substring(0, doubleColonIndex);
        int secondLastDotIndex = fullClassPath.lastIndexOf('.');

        String packageName = "";
        String className = fullClassPath;

        if (secondLastDotIndex != -1) {
            packageName = fullClassPath.substring(0, secondLastDotIndex);
            className = fullClassPath.substring(secondLastDotIndex + 1);
        }

        String methodName = fixedMethod.substring(doubleColonIndex + 2);

        Component component = new Component(packageName, className, methodName);

        this.fixedComponents.add(component);
    }

    public List<Component> getComponents() {
        return new ArrayList<>(this.fixedComponents);
    }
}
