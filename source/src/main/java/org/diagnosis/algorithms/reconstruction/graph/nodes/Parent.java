package org.diagnosis.algorithms.reconstruction.graph.nodes;

import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtAnonymousExecutable;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;

public class Parent {
    String packageName;
    String className;
    String methodName;
    String type;

    private void setEmpty() {
        this.packageName = "";
        this.className = "";
        this.methodName = "";
        this.type = "";
    }

    public Parent() {
        setEmpty();
    }

    public Parent(CtMethod<?> method) {
        if (method.getDeclaringType() == null) {
            setEmpty();
            return;
        }

        if (method.getDeclaringType().getPackage() != null) {
            this.packageName = method.getDeclaringType().getPackage().getQualifiedName();
        } else {
            this.packageName = "";
        }
        this.className = method.getDeclaringType().getSimpleName();
        this.methodName = method.getSimpleName();
        this.type = "method";
    }

    public Parent(CtInvocation<?> methodCall) {
        if (methodCall.getExecutable().getDeclaringType() == null) {
            setEmpty();
            return;
        }

        if (methodCall.getExecutable().getDeclaringType().getPackage() != null) {
            this.packageName = methodCall.getExecutable().getDeclaringType().getPackage().getQualifiedName();
        } else {
            this.packageName = "";
        }
        this.className = methodCall.getExecutable().getDeclaringType().getSimpleName();
        this.methodName = methodCall.getExecutable().getSimpleName();
        this.type = "methodCall";
    }

    public Parent(CtConstructorCall<?> constructor) {
        if (constructor.getExecutable().getDeclaringType() == null) {
            setEmpty();
            return;
        }

        if (constructor.getExecutable().getDeclaringType().getPackage() != null) {
            this.packageName = constructor.getExecutable().getDeclaringType().getPackage().getQualifiedName();
        } else {
            this.packageName = "";
        }
        this.className = constructor.getExecutable().getDeclaringType().getSimpleName();
        this.methodName = "new";
        this.type = "constructor";
    }

    public Parent(CtAnonymousExecutable anonymousExecutable) {
        if (anonymousExecutable.getDeclaringType() != null) {
            setEmpty();
            return;
        }
        if (anonymousExecutable.getDeclaringType().getPackage() != null) {
            this.packageName = anonymousExecutable.getDeclaringType().getPackage().getQualifiedName();
        } else {
            this.packageName = "";
        }
        this.className = anonymousExecutable.getDeclaringType().getSimpleName();
        this.methodName = "<clinit>";
        this.type = "anonymous";
    }

    public Parent(CtElement ctElement) {
        if (ctElement.getParent(CtType.class).getPackage() != null) {
            if (ctElement.getParent(CtType.class).getPackage() != null) {
                this.packageName = ctElement.getParent(CtType.class).getPackage().getQualifiedName();
            } else {
                this.packageName = "";
            }
            this.className = ctElement.getParent(CtType.class).getSimpleName();
        } else {
            this.packageName = "";
            this.className = "";
        }
        this.type="other";
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

    public String getType() {
        return type;
    }

}
