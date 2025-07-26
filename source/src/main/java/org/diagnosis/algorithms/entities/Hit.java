package org.diagnosis.algorithms.entities;

import java.io.Serializable;
import java.util.Objects;

public class Hit implements Cloneable, Serializable {
    private final Component component;
    private float numberOfHits;
    private double averageCounterForPartialComponentsInRandomWalks;

    public Hit(String packageName, String className, String methodName, int numberOfHits) {
        this.component = new Component(packageName, className, methodName);
        this.numberOfHits = (float) numberOfHits;
    }

    public Hit(Component component, int numberOfHits) {
        this.component = component;
        this.numberOfHits = (float) numberOfHits;
    }

    public Hit(Component component, float hitPercentage) {
        this.component = component;
        this.numberOfHits = hitPercentage;
    }

    public Component getComponent() {
        return this.component;
    }

    public String getPackageName() {
        return component.getPackageName();
    }

    public String getClassName() {
        return component.getClassName();
    }

    public String getMethodName() {
        return component.getMethodName();
    }

    public float getNumberOfHits() {
        return numberOfHits;
    }

    public void incrementNumberOfHits() {
        numberOfHits++;
    }

    public Boolean isHit() {
        return numberOfHits > 0;
    }

    public boolean isMiss() {
        return numberOfHits == 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Hit hit = (Hit) o;
        return Objects.equals(component, hit.getComponent());
    }

    @Override
    public int hashCode() {
        return Objects.hash(component);
    }

    @Override
    public String toString() {
        return String.format("%s.%s::%s", this.component.getPackageName(), this.component.getClassName(), this.component.getMethodName());
    }

    public void addNumberOfHits(float numberOfHits) {
        this.numberOfHits += numberOfHits;
    }

    public void addNumberOfHits(int numberOfHits) {
        this.numberOfHits += numberOfHits;
    }


    public boolean isFromTestFile() {
        return component.isFromTestFile();
    }

    public void setNumberOfHits(int i) {
        this.numberOfHits = i;
    }

    public void setHitPercentage(float i) {
        assert (i >= 0 && i <= 1);
        this.numberOfHits = i;
    }

    @Override
    public Hit clone() {
        try {
            super.clone();
            return new Hit(this.component, (int) this.numberOfHits);
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    public float getProbability() {
        if (this.numberOfHits > 1)
            return 1.0f;
        else if (this.numberOfHits < 0)
            return 0.0f;
        else
            return this.numberOfHits;
    }

    public void setAverageCounterForPartialComponentsInRandomWalks(double counter) {
        this.averageCounterForPartialComponentsInRandomWalks = counter;
    }

    public double getAverageCounterForPartialComponentsInRandomWalks() {
        return this.averageCounterForPartialComponentsInRandomWalks;
    }
}
