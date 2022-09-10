package com.krazzzzymonkey.catalyst.value.sliders;

import com.krazzzzymonkey.catalyst.value.Value;

public class DoubleValue extends Value<Double> {

    protected double min, max;
    public DoubleValue(String name, double defaultValue, double min, double max, String description) {
        super(name, defaultValue, description);
        this.min = min;
        this.max = max;
    }

    @Override
    public Double getValue() {
        java.lang.Number number = super.getValue();
        return number.doubleValue();
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }
}
