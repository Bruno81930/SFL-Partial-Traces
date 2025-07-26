package org.diagnosis.algorithms.reconstruction.graph.nodes;

import spoon.reflect.code.CtInvocation;

public class MethodCallNode extends Node{

    CtInvocation<?> invocation;

    public MethodCallNode (CtInvocation<?> invocation, String callerNodeSignature, int callStackSize, int callStackHash) {
        super(callerNodeSignature, new Parent(invocation), callStackSize, callStackHash);
        this.invocation = invocation;
        this.init(invocation, invocation.getExecutable().getDeclaringType());
    }

    @Override
    protected String assignMethodName() {
        String methodName = this.invocation.getExecutable().getSimpleName();
        if (methodName.equals("<init>")) {
            return "new";
        }
        return methodName;
    }

    @Override
    protected String assignNodeType() {
        return "methodCall";
    }

}
