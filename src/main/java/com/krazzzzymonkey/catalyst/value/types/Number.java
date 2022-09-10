package com.krazzzzymonkey.catalyst.value.types;

import com.krazzzzymonkey.catalyst.value.Value;

public class Number extends Value<Double> {

    protected Double min, max;

    public Number(String name, Double defaultValue) {
        super(name, defaultValue, "");
    }

}
