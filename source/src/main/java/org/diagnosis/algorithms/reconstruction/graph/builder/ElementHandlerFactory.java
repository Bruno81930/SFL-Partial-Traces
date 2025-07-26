package org.diagnosis.algorithms.reconstruction.graph.builder;

import spoon.reflect.code.*;
import spoon.reflect.declaration.CtAnonymousExecutable;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;

public class ElementHandlerFactory {
    public static ElementHandler getHandler(CtElement element) {
        if (element instanceof CtInvocation) {
            return new InvocationHandler(element);
        } else if (element instanceof CtConstructorCall) {
            return new ConstructorCallHandler(element);
        } else if (element instanceof CtAnonymousExecutable && ((CtAnonymousExecutable) element).isStatic()) {
            return new AnonymousCallHandler(element);
        } else if (element instanceof CtIf) {
            return new IfHandler(element);
        } else if (element instanceof CtLoop) {
            return new LoopHandler(element);
        } else if (element instanceof CtSynchronized) {
            return new SynchronizedHandler(element);
        }else if (element instanceof CtSwitch<?>) {
            return new SwitchHandler(element);
        } else if (element instanceof CtBlock<?>) {
            return new BodyHandler(element);
        } else if (element instanceof CtStatement) {
            return new StatementHandler(element);
        } else if (element instanceof CtMethod<?>) {
            return new MethodHandler(element);
        } else {
            return new OtherHandler();
        }
    }
}
