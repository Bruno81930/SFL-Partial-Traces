package org.diagnosis.algorithms.reconstruction.graph.nodes;

import spoon.reflect.code.CtConstructorCall;

public class ConstructorCallNode extends Node {

    public ConstructorCallNode(CtConstructorCall<?> constructorCall, String callerNodeSignature, int callStackSize, int callStackHash) {
        super(callerNodeSignature, new Parent(constructorCall), callStackSize, callStackHash);
        this.init(constructorCall, constructorCall.getExecutable().getDeclaringType());
    }

    @Override
    protected String assignMethodName() {
        return "new";
    }

    @Override
    protected String assignNodeType() {
        return "constructor";
    }
}

