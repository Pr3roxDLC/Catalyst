package com.krazzzzymonkey.catalyst.value.sliders;

import com.krazzzzymonkey.catalyst.value.Value;

public class IntegerValue extends Value<Integer> {

    protected int min, max;

    public IntegerValue(String name, int defaultValue, int min, int max, String description) {
        super(name, defaultValue, description);
        this.min = min;
        this.max = max;
    }

    @Override
    public Integer getValue() {
        java.lang.Number number = super.getValue();
        return number.intValue();
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }
}
