package org.diagnosis.algorithms.reconstruction.graph.nodes;

import spoon.reflect.declaration.CtMethod;

public class MethodNode extends Node {
    CtMethod<?> method;

    public MethodNode(CtMethod<?> method, String callerNodeSignature, int callStackSize, int callStackHash) {
        super(callerNodeSignature, new Parent(method), callStackSize, callStackHash);
        this.method = method;
        this.init(method, method.getDeclaringType());
    }

    public MethodNode(CtMethod<?> method) {
        super(new Parent(method));
        this.method = method;
        this.init(method, method.getDeclaringType());
    }

    @Override
    protected String assignMethodName() {
        String methodName = this.method.getSimpleName();
        if (methodName.equals("<init>")) {
            return "new";
        }
        return methodName;
    }

    @Override
    protected String assignNodeType() {
        return "method";
    }
}
