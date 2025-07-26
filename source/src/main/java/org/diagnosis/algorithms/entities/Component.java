package org.diagnosis.algorithms.entities;

import java.io.Serializable;
import java.util.Objects;

public class Component implements Comparable<Component>, Serializable {
    private final String packageName;
    private final String className;
    private final String methodName;

    public Component(String packageName, String className, String methodName) {
        this.packageName = packageName;
        this.className = className;
        this.methodName = methodName;
    }

    public Component(String component) {
        int lastPeriodIndex = component.lastIndexOf('.');
        int secondLastPeriodIndex = component.lastIndexOf('.', lastPeriodIndex - 1);
        this.packageName = component.substring(0, secondLastPeriodIndex);
        this.className = component.substring(secondLastPeriodIndex + 1, lastPeriodIndex);
        this.methodName = component.substring(lastPeriodIndex + 1);
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

    @Override
    public boolean equals(Object component) {
        if (component == null) {
            return false;
        }
        if (component == this) {
            return true;
        }
        if (!(component instanceof Component)) {
            return false;
        }
        Component other = (Component) component;

        return this.packageName.equals(other.packageName)
                && this.className.equals(other.className)
                && this.methodName.equals(other.methodName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(packageName, className, methodName);
    }

    @Override
    public int compareTo(Component other) {
        int packageNameCompare = this.packageName.compareTo(other.packageName);
        if (packageNameCompare != 0) return packageNameCompare;

        int classNameCompare = this.className.compareTo(other.className);
        if (classNameCompare != 0) return classNameCompare;

        return this.methodName.compareTo(other.methodName);
    }

    public boolean isFromTestFile() {
        boolean isTestClass = className.contains("Test");

        boolean isTestPackage = packageName.toLowerCase().contains("test");

        return isTestClass || isTestPackage;
    }

    @Override
    public String toString() {
        return String.format("%s.%s.%s", packageName, className, methodName);
    }
}
