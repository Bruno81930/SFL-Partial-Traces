package org.diagnosis.algorithms.entities;

public class TestCase {
    final String packageName;
    final String className;
    final String methodName;
    final boolean failed;

    public TestCase(String packageName, String className, String methodName, boolean failed) {
        this.packageName = packageName;
        this.className = className;
        this.methodName = methodName;
        this.failed = failed;
    }

    public TestCase(String testcase, boolean failed){
        int lastPeriodIndex = testcase.lastIndexOf('.');
        int secondLastPeriodIndex = testcase.lastIndexOf('.', lastPeriodIndex - 1);
        this.packageName = testcase.substring(0, secondLastPeriodIndex);
        this.className = testcase.substring(secondLastPeriodIndex + 1, lastPeriodIndex);
        this.methodName = testcase.substring(lastPeriodIndex + 1);
        this.failed = failed;
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

    public boolean hasFailed() {
        return failed;
    }

    @Override
    public String toString() {
        return String.format("%s.%s.%s", packageName, className, methodName);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TestCase) {
            TestCase other = (TestCase) obj;
            if (packageName.equals("")) {
                return className.equals(other.className) && methodName.equals(other.methodName);
            }
            return packageName.equals(other.packageName) && className.equals(other.className) && methodName.equals(other.methodName);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return packageName.hashCode() + className.hashCode() + methodName.hashCode();
    }
}
