package org.diagnosis.algorithms.sfl.ssfl;

public class CoverageMatrix {
    private float n00;
    private float n01;
    private float n10;
    private float n11;

    public CoverageMatrix(float n00, float n01, float n10, float n11) {
        this.n00 = n00;
        this.n01 = n01;
        this.n10 = n10;
        this.n11 = n11;
    }

    public float getN00() {
        return n00;
    }

    public void setN00(float n00) {
        this.n00 = n00;
    }

    public float getN01() {
        return n01;
    }

    public void setN01(float n01) {
        this.n01 = n01;
    }

    public float getN10() {
        return n10;
    }

    public void setN10(float n10) {
        this.n10 = n10;
    }

    public float getN11() {
        return n11;
    }

    public void setN11(float n11) {
        this.n11 = n11;
    }
}
