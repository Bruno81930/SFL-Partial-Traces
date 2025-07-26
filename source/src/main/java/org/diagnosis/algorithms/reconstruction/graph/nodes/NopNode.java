package org.diagnosis.algorithms.reconstruction.graph.nodes;

import java.util.UUID;

public class NopNode extends Node{
    UUID uniqueID;

    public NopNode() {
        this.init();
        uniqueID = UUID.randomUUID();
    }

    protected String assignMethodName() {
        return "";
    }

    protected String assignNodeType() {
        return "nop";
    }

    @Override
    public String toString() {
        return "NOP<>" + uniqueID.toString() + "<>NOP";
    }
}
