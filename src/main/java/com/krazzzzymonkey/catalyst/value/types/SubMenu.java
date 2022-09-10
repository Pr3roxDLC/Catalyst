package com.krazzzzymonkey.catalyst.value.types;

import com.krazzzzymonkey.catalyst.value.Value;

public class SubMenu extends Value {

    private final Value[] values;
    private final String subMenuName;

    public Value[] getValues() {
        return values;
    }

    public SubMenu(String subMenuName, Value... modes) {
        super(subMenuName, null, "");
        this.subMenuName = subMenuName;
        this.values = modes;
    }

    public Value getValue(String name) {
        Value v = null;
        for (Value value : values) {
            if ( value.getName().equals(name)) {
                v = value;
            }
        }
        return v;
    }

    public String getSubMenuName() {
        return subMenuName;
    }


}
