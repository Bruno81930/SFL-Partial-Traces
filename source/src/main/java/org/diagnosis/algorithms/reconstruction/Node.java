package org.diagnosis.algorithms.reconstruction;

import org.diagnosis.algorithms.entities.Hit;
import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.*;
import spoon.reflect.reference.CtTypeReference;

import java.util.Objects;

public class Node {

    String packageName;
    String className;
    String methodName;
    Node parentNode;
    String callerPackageName;
    String callerClassName;
    String callerMethodName;
    String invocationPath;
    int invocationLine;
    int invocationColumn;
    boolean invoked;
    double factor = 1.0d;
    boolean isNOPNode = false;
    boolean isStartNode = false;
    boolean isEndNode = false;
    boolean isRecursiveCall = false;

    public void setRecursiveCall() {
        this.isRecursiveCall = true;
    }

    public static Node createStartNode() {
        Node startNode = new Node(null);
        startNode.makeEmptyNode(1.0d, "START");
        startNode.setStartNode();
        return startNode;
    }

    public static Node createEndNode() {
        Node endNode = new Node(null);
        endNode.makeEmptyNode(1.0d, "END");
        endNode.setEndNode();
        return endNode;
    }

    public static Node createNOPNode(Node parent, double factor) {
        Node emptyNode = new Node(parent);
        emptyNode.makeEmptyNode(factor, "NOP");
        emptyNode.setNOPNode();
        return emptyNode;
    }

    public Node(Node parentNode) {
        this.parentNode = parentNode;
    }

    public void makeEmptyNode(double factor, String placeholderMessage) {
        String message = "<" + placeholderMessage + ">";
        this.packageName = message;
        this.className = message;
        this.methodName = message;
        this.callerPackageName = message;
        this.callerClassName = message;
        this.callerMethodName = message;
        this.invocationPath = message;
        this.invocationLine = -1;
        this.invocationColumn = -1;
        this.invoked = false;
        this.factor = factor;
    }

    public Node(CtMethod<?> method, Node parentNode) {
        CtType<?> type = method.getDeclaringType();
        if (type != null) {
            if (type.getPackage() != null)
                this.packageName = type.getPackage().getQualifiedName();
            else
                this.packageName = "";
            this.className = method.getDeclaringType().getSimpleName();
        } else {
            this.packageName = "";
            this.className = "";
        }
        this.methodName = method.getSimpleName();
        if (this.methodName.equals("<init>")) {
            this.methodName = "new";
        }
        this.callerPackageName = "";
        this.callerClassName = "";
        this.callerMethodName = "";
        this.parentNode = parentNode;
        this.invocationPath = method.getPosition().getFile().getPath();
        this.invocationLine = method.getPosition().getLine();
        this.invocationColumn = method.getPosition().getColumn();
        this.invoked = false;
    }

    public Node(CtConstructorCall<?> constructorCall, Node parentNode) {
        CtTypeReference<?> type = constructorCall.getExecutable().getDeclaringType();
        if (type != null && type.getPackage() != null) {
            if (type.getPackage() != null)
                this.packageName = type.getPackage().getQualifiedName();
            else
                this.packageName = "";
            this.className = constructorCall.getExecutable().getDeclaringType().getSimpleName();
        } else {
            this.packageName = "";
            this.className = "";
        }
        this.methodName = "new";
        handleCaller(constructorCall);
        this.parentNode = parentNode;
        this.invoked = true;
    }

    public Node(CtAnonymousExecutable anonymousExecutable, Node parentNode) {
        CtType<?> type = anonymousExecutable.getDeclaringType().getDeclaringType();
        if (type != null && type.getPackage() != null) {
            if (type.getPackage() != null)
                this.packageName = type.getPackage().getQualifiedName();
            else
                this.packageName = "";
            this.className = type.getSimpleName();
        } else {
            this.packageName = "";
            this.className = "";
        }
        this.methodName = "<clinit>";
        this.handleCaller(anonymousExecutable);
        this.parentNode = parentNode;
        this.invoked = true;
    }

    public Node(CtInvocation<?> invocation, Node parentNode) {
        CtTypeReference<?> type = invocation.getExecutable().getDeclaringType();
        if (type != null && type.getPackage() != null) {
            if (type.getPackage() != null)
                this.packageName = type.getPackage().getQualifiedName();
            else
                this.packageName = "";
            this.className = invocation.getExecutable().getDeclaringType().getSimpleName();
        } else {
            this.packageName = "";
            this.className = "";
        }
        this.methodName = invocation.getExecutable().getSimpleName();
        if (this.methodName.equals("<init>")) {
            this.methodName = "<clinit>";
        }
        handleCaller(invocation);
        this.parentNode = parentNode;
        this.invoked = true;
    }

    private void handleCaller(CtElement element) {
        CtExecutable<?> parent = element.getParent(CtExecutable.class);
        if (parent instanceof CtMethod<?>) {
            CtMethod<?> method = (CtMethod<?>) parent;
            if (method.getDeclaringType().getPackage() != null) {
                this.callerPackageName = method.getDeclaringType().getPackage().getQualifiedName();
            } else {
                this.callerPackageName = "";
            }
            this.callerClassName = method.getDeclaringType().getSimpleName();
            this.callerMethodName = method.getSimpleName();
            this.invocationPath = element.getPosition().getFile().getPath();
            this.invocationLine = element.getPosition().getLine();
            this.invocationColumn = element.getPosition().getColumn();
        } else if (parent instanceof CtConstructorCall<?>) {
            CtConstructorCall<?> constructorCall = (CtConstructorCall<?>) parent;
            CtConstructor<?> constructor = (CtConstructor<?>) constructorCall.getExecutable().getExecutableDeclaration();
            if (constructor.getDeclaringType().getPackage() != null) {
                this.callerPackageName = constructor.getDeclaringType().getPackage().getQualifiedName();
            } else {
                this.callerPackageName = "";
            }
            this.callerClassName = constructor.getDeclaringType().getSimpleName();
            this.callerMethodName = "new";
            this.invocationPath = constructorCall.getPosition().getFile().getPath();
            this.invocationLine = constructorCall.getPosition().getLine();
            this.invocationColumn = constructorCall.getPosition().getColumn();
        } else if (parent instanceof CtAnonymousExecutable) {
            CtAnonymousExecutable anonymousExecutable = (CtAnonymousExecutable) parent;
            CtType<?> type = anonymousExecutable.getDeclaringType().getDeclaringType();
            if (type != null && type.getPackage() != null) {
                if (type.getPackage() != null) {
                    this.callerPackageName = type.getPackage().getQualifiedName();
                } else {
                    this.callerPackageName = "";
                }
                this.callerClassName = type.getSimpleName();
            } else {
                this.callerPackageName = "";
                this.callerClassName = "";
            }
            this.callerMethodName = "<clinit>";
            this.invocationPath = anonymousExecutable.getPosition().getFile().getPath();
            this.invocationLine = anonymousExecutable.getPosition().getLine();
            this.invocationColumn = anonymousExecutable.getPosition().getColumn();
        } else {
            this.callerPackageName = "";
            this.callerClassName = "";
            this.callerMethodName = "";
            this.invocationPath = "";
            this.invocationLine = -1;
            this.invocationColumn = -1;
        }
    }

    @Override
    public String toString() {
        return this.callerPackageName + "." + this.callerClassName + "::" + this.callerMethodName + " => " + this.packageName + "." + this.className + "::" + this.methodName + "__" + this.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Node) {
            Node other = (Node) obj;
            return this.packageName.equals(other.packageName) &&
                    this.className.equals(other.className) &&
                    this.methodName.equals(other.methodName) &&
                    this.callerPackageName.equals(other.callerPackageName) &&
                    this.callerClassName.equals(other.callerClassName) &&
                    this.callerMethodName.equals(other.callerMethodName) &&
                    this.invocationPath.equals(other.invocationPath) &&
                    this.invocationLine == other.invocationLine &&
                    this.invocationColumn == other.invocationColumn &&
                    this.parentNode == other.parentNode &&
                    this.hashCode() == other.hashCode();

        }
        return false;
    }

    @Override
    public int hashCode() {
        // Use a prime number to combine hash codes
        final int prime = 31;
        int result = 1;

        // Hash combined with parent's hash to ensure uniqueness in the context of the execution graph
        result = prime * result + ((parentNode == null) ? 0 : parentNode.hashCode());

        // Include the immutable properties that define the uniqueness of the node in execution
        result = prime * result + Objects.hashCode(packageName);
        result = prime * result + Objects.hashCode(className);
        result = prime * result + Objects.hashCode(methodName);
        result = prime * result + Objects.hashCode(callerPackageName);
        result = prime * result + Objects.hashCode(callerClassName);
        result = prime * result + Objects.hashCode(callerMethodName);
        result = prime * result + Objects.hashCode(invocationPath);
        result = prime * result + invocationLine;
        result = prime * result + invocationColumn;
        result = prime * result + (invoked ? 1231 : 1237);
        result = prime * result + (isNOPNode ? 1231 : 1237);
        result = prime * result + (isStartNode ? 1231 : 1237);
        result = prime * result + (isEndNode ? 1231 : 1237);
        result = prime * result + Double.hashCode(factor);

        return result;
    }

    public boolean isNOPNode() {
        return this.isNOPNode;
    }

    public void setNOPNode() {
        this.isNOPNode = true;
    }

    public boolean isStartNode() {
        return this.isStartNode;
    }

    public void setStartNode() {
        this.isStartNode = true;
    }

    public boolean isEndNode() {
        return this.isEndNode;
    }

    public void setEndNode() {
        this.isEndNode = true;
    }

    public Hit getHit() {
        return new Hit(packageName, className, methodName, 1);
    }

    public String getPackageName() {
        return packageName;
    }


}
