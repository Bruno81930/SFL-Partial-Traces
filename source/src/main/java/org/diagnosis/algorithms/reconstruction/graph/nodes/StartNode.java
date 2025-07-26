package org.diagnosis.algorithms.reconstruction.graph.nodes;

public class StartNode extends Node{

    public StartNode() {
        this.init();
    }

    @Override
    protected String assignMethodName() {
        return "";
    }

    @Override
    protected String assignNodeType() {
        return "start";
    }

    @Override
    public String toString() {
        return "START<>START<>START<>START<>START<>START";
    }
}
