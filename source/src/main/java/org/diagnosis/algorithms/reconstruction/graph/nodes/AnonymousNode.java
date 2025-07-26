package org.diagnosis.algorithms.reconstruction.graph.nodes;

import spoon.reflect.declaration.CtAnonymousExecutable;

public class AnonymousNode extends Node{
    public AnonymousNode(CtAnonymousExecutable anonymousExecutable, String callerNodeSignature, int callStackSize, int callStackHash) {
        super(callerNodeSignature, new Parent(anonymousExecutable), callStackSize, callStackHash);
        this.init(anonymousExecutable, anonymousExecutable.getDeclaringType());
    }

    @Override
    protected String assignMethodName() {
        return "<clinit>";
    }

    @Override
    protected String assignNodeType() {
        return "anonymous";
    }
}
