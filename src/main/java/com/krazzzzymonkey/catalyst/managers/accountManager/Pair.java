package com.krazzzzymonkey.catalyst.managers.accountManager;

import java.io.Serializable;

/**
 * Simple Pair system with 2 variables.
 * @author MRebhan
 * @author The_Fireplace
 *
 * @param <V1> First variable (mostly {@link String})
 * @param <V2> Second variable
 */

public class Pair<V1, V2> implements Serializable {
    private static final long serialVersionUID = 2586850598481149380L;

    private final V1 obj1;
    private final V2 obj2;

    public Pair(V1 obj1, V2 obj2) {
        this.obj1 = obj1;
        this.obj2 = obj2;
    }

    public V1 getValue1() {
        return this.obj1;
    }

    public V2 getValue2() {
        return this.obj2;
    }

    @Override public String toString() {
        return Pair.class.getName() + "@" + Integer.toHexString(this.hashCode()) + " [" + this.obj1.toString() + ", " + this.obj2.toString() + "]";
    }
}
