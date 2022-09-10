package com.krazzzzymonkey.catalyst.value.types;

import com.krazzzzymonkey.catalyst.value.Mode;
import com.krazzzzymonkey.catalyst.value.Value;

import java.util.Arrays;
import java.util.Optional;

public class ModeValue extends Value<Mode> {

    private final Mode[] modes;
    private final String modeName;

    public Mode[] getModes() {
        return modes;
    }

    public ModeValue(String modeName, Mode... modes) {
        super(modeName, null, "");
        this.modeName = modeName;
        this.modes = modes;
    }

    public Mode getMode(String name) {
        Mode m = null;
        for (Mode mode : modes) {
            if (mode.getName().equals(name)) {
                m = mode;
            }
        }
        return m;
    }

    public Mode getActiveMode() {
        Optional<Mode> active = Arrays.stream(modes).filter(n -> n.isToggled()).findFirst();
        if (active.isPresent()) {
            return active.get();
        } else {
            throw new Error("Encountered a ModeValue without an active mode");
        }
    }

    public String getModeName() {
        return modeName;
    }
}
