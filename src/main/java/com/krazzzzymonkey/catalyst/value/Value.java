package com.krazzzzymonkey.catalyst.value;

public class Value<T> {

    public T value;

    private final String name;

    private final T defaultValue;

    private final String description;

    public Value(String name, T defaultValue, String description) {
        this.name = name;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
        this.description = description;
    }

    public String getName() {

        return name;
    }

    public T getDefaultValue() {

        return defaultValue;
    }

    public String getDescription() {

        return description;

    }

    public T getValue() {

        return value;
    }

    public void setValue(T value) {

        this.value = value;
    }
}
