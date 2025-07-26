package org.diagnosis.algorithms.reconstruction.graph.nodes;

public class EndNode extends Node{

    public EndNode() {
        this.init();
    }

    @Override
    protected String assignMethodName() {
        return "";
    }

    @Override
    protected String assignNodeType() {
        return "end";
    }

    @Override
    public String toString() {
        return "END<>END<>END<>END<>END<>END";
    }
}
