package org.diagnosis.algorithms.reconstruction;

import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.visitor.filter.TypeFilter;

import java.nio.file.Paths;
import java.util.Arrays;

public class SourceModel {

    CtModel model;

    public SourceModel(String dataPath, String[] srcPaths){
        Launcher launcher = new Launcher();
        Arrays.stream(srcPaths).map(src -> Paths.get(dataPath, src).toString()).forEach(launcher::addInputResource);
        launcher.buildModel();
        this.model = launcher.getModel();
    }

    public CtMethod<?> getEntryMethod(String packageName, String className, String methodName) throws SourceModelException {
        CtClass<?> testClass = model.getElements(new TypeFilter<>(CtClass.class)).stream()
                .filter(ctClass -> ctClass.getSimpleName().equals(className) && ctClass.getPackage().getQualifiedName().equals(packageName))
                .findFirst()
                .orElseThrow(() -> new SourceModelException(String.format("Class %s not found", className)));

        return getTest(testClass, methodName);
    }

    public CtMethod<?> getEntryMethod(String className, String methodName) throws SourceModelException {
        CtClass<?> testClass = model.getElements(new TypeFilter<>(CtClass.class)).stream()
                .filter(ctClass -> ctClass.getSimpleName().equals(className))
                .findFirst()
                .orElseThrow(() -> new SourceModelException(String.format("Class %s not found", className)));

        return getTest(testClass, methodName);
    }

    private CtMethod<?> getTest(CtClass<?> testClass, String methodName) throws SourceModelException {
        final String sanitizedMethodName = sanitizeMethodName(methodName);
        try {
            return testClass.getAllMethods().stream()
                    .filter(method -> method.getSimpleName().equals(sanitizedMethodName))
                    .findFirst()
                    .orElseThrow(() -> new SourceModelException(String.format("Test method %s not found", methodName)));
        } catch (SourceModelException e) {
            return testClass.getSuperclass().getTypeDeclaration().getMethods().stream()
                    .filter(method -> method.getSimpleName().equals(sanitizedMethodName))
                    .findFirst()
                    .orElseThrow(() -> new SourceModelException(String.format("Test method %s not found", methodName)));
        }
    }

    private String sanitizeMethodName(String methodName) {
        return methodName.replaceAll("\\[.*?\\]", "");
    }
}
