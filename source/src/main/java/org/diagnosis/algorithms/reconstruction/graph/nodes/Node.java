package org.diagnosis.algorithms.reconstruction.graph.nodes;

import org.diagnosis.algorithms.entities.Component;
import org.diagnosis.algorithms.entities.Hit;
import spoon.reflect.cu.position.NoSourcePosition;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtTypeReference;

import java.util.Objects;
import java.util.UUID;

public abstract class Node {
    protected String packageName;
    protected String className;
    protected String methodName;
    protected String path;
    protected int lineNumber;
    protected int columnNumber;
    protected String nodeType;
    protected String callerNodeSignature;
    protected Parent parent;
    protected int callStackSize;
    protected int callStackHash;
    protected String signature;

    protected Node (String callerNodeSignature, Parent parent, int callStackSize, int callStackHash) {
        this.callerNodeSignature = callerNodeSignature;
        this.parent = parent;
        this.callStackSize = callStackSize;
        this.callStackHash = callStackHash;
    }

    protected Node (Parent parent) {
        this.parent = parent;
    }

    protected Node () {
        this.parent = new Parent();
    }

    protected void init(CtElement element, CtTypeReference<?> type) {
        this.packageName = assignPackageName(type);
        this.className = assignClassName(type);
        this.init(element);
    }

    protected void init(CtElement element, CtType<?> type) {
        this.packageName = assignPackageName(type);
        this.className = assignClassName(type);
        this.init(element);
    }

    private void init(CtElement element) {
        this.methodName = assignMethodName();
        this.path = assignPath(element);
        this.lineNumber = assignLineNumber(element);
        this.columnNumber = assignColumnNumber(element);
        this.nodeType = assignNodeType();
    }

    protected void init() {
        this.packageName = "";
        this.className = "";
        this.methodName = "";
        this.path = "";
        this.lineNumber = 0;
        this.columnNumber = 0;
        this.nodeType = "";
    }

    protected String assignPackageName(CtType<?> type) {
        if (type != null && type.getPackage() != null) {
            return type.getPackage().getQualifiedName();
        } else {
            return "";
        }
    }

    protected String assignPackageName(CtTypeReference<?> type) {
        if (type != null && type.getPackage() != null) {
            return type.getPackage().getQualifiedName();
        } else {
            return "";
        }
    }

    protected String assignClassName(CtType<?> type) {
        if (type != null) {
            return type.getSimpleName();
        } else {
            return "";
        }
    }

    protected String assignClassName(CtTypeReference<?> type) {
        if (type != null) {
            return type.getSimpleName();
        } else {
            return "";
        }
    }

    abstract protected String assignMethodName();

    protected String assignPath(CtElement element) {
        if (element != null && element.getPosition() != null && element.getPosition().getFile() != null)
            return element.getPosition().getFile().getPath();
        return "";
    }

    protected int assignLineNumber(CtElement element) {
        if (element != null && element.getPosition() != null && !(element.getPosition() instanceof NoSourcePosition))
            return element.getPosition().getLine();
        return (int) -(UUID.randomUUID().getLeastSignificantBits() & Integer.MAX_VALUE);
    }

    protected int assignColumnNumber(CtElement element) {
        if (element != null && element.getPosition() != null && !(element.getPosition() instanceof NoSourcePosition))
            return element.getPosition().getColumn();
        return (int) -(UUID.randomUUID().getLeastSignificantBits() & Integer.MAX_VALUE);
    }

    abstract protected String assignNodeType();

    @Override
    public String toString() {
        if (this.callerNodeSignature == null) {
                return this.getSignature();
        }

        return this.callerNodeSignature + " => " + this.getSignature() + "#stack=" + this.callStackSize + "#hash=" + this.callStackHash;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Node)) {
            return false;
        }

        Node node = (Node) obj;
        return this.toString().equals(node.toString());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.toString());
    }

    public String getSignature() {
        if (signature == null) {
            signature =
                        "(" +
                            "[" + this.parent.getType() + "]" +
                            this.parent.getPackageName() +
                            "." +
                            this.parent.getClassName() +
                            "::" +
                            this.parent.getMethodName() +
                        ")" +
                            "[" + this.nodeType + "]" +
                            this.packageName +
                            "." +
                            this.className +
                            "::" +
                            this.methodName +
                            "@" +
                            this.lineNumber +
                            ":" +
                            this.columnNumber ;
        }
        return signature;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getClassName() {
        return className;
    }

    public String getMethodName() {
        return methodName;
    }

    public Parent getParent() {
        return parent;
    }

    public String getPath() {
        return path;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public int getColumnNumber() {
        return columnNumber;
    }

    public String getNodeType() {
        return nodeType;
    }

    public Hit createHit() {
        return new Hit(packageName, className, methodName, 1);
    }
    public Hit createMiss() {
        return new Hit(packageName, className, methodName, 0);
    }

    public Component toComponent() {
        return new Component(this.packageName, this.className, this.methodName);
    }
}
