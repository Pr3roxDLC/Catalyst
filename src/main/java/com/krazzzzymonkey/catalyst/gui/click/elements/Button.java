package com.krazzzzymonkey.catalyst.gui.click.elements;

import java.util.ArrayList;

import com.krazzzzymonkey.catalyst.gui.click.base.Component;
import com.krazzzzymonkey.catalyst.gui.click.base.ComponentType;
import com.krazzzzymonkey.catalyst.gui.click.listener.ComponentClickListener;
import com.krazzzzymonkey.catalyst.module.Modules;

public class Button extends Component {

    public ArrayList<ComponentClickListener> listeners = new ArrayList<ComponentClickListener>();

    private Modules mod;

    private boolean enabled = false;

    public Button(int xPos, int yPos, int width, int height, Component component, String text) {

        super(xPos, yPos, width, height, ComponentType.BUTTON, component, text, "");
    }

    public Button(int xPos, int yPos, int width, int height, Component component, String text, Modules mod) {

        super(xPos, yPos, width, height, ComponentType.BUTTON, component, text, "");
        this.mod = mod;
    }

    public void addListeners(ComponentClickListener listener) {

        listeners.add(listener);
    }

    public void onMousePress(int x, int y, int button) {

        if (button != 0) {
            return;
        }

        this.enabled = !this.enabled;

        for (ComponentClickListener listener : listeners) {
            listener.onComponenetClick(this, button);
        }
    }

    public boolean isEnabled() {

        return enabled;
    }

    public void setEnabled(boolean enabled) {

        this.enabled = enabled;
    }

    public ArrayList<ComponentClickListener> getListeners() {

        return listeners;
    }

    public Modules getMod() {

        return mod;
    }
}
