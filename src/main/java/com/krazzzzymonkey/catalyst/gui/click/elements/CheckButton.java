package com.krazzzzymonkey.catalyst.gui.click.elements;

import com.krazzzzymonkey.catalyst.gui.click.base.Component;
import com.krazzzzymonkey.catalyst.gui.click.base.ComponentType;
import com.krazzzzymonkey.catalyst.gui.click.listener.CheckButtonClickListener;
import com.krazzzzymonkey.catalyst.value.Mode;
import com.krazzzzymonkey.catalyst.value.Value;
import com.krazzzzymonkey.catalyst.value.types.ModeValue;
import com.krazzzzymonkey.catalyst.value.types.SubMenu;

import java.util.ArrayList;

public class CheckButton extends Component {

    public ArrayList<CheckButtonClickListener> listeners = new ArrayList<CheckButtonClickListener>();

    private boolean enabled = false;
    private ModeValue modeValue = null;
    private final Value value = null;
    private final String description;
    private final SubMenu subMenu;
    public CheckButton(int xPos, int yPos, int width, int height, Component component, String text, boolean enabled, ModeValue modeValue, String description, SubMenu subMenu) {

        super(xPos, yPos, width, height, ComponentType.CHECK_BUTTON, component, text, description);
        this.enabled = enabled;
        this.modeValue = modeValue;
        this.description = description;
        this.subMenu = subMenu;
    }

    @Override
    public void onMousePress(int x, int y, int buttonID) {
    	if(modeValue != null) {
    		for(Mode mode : modeValue.getModes()) {
    			mode.setToggled(false);
    		}
    		this.enabled = true;
    	}
    	else {
    		this.enabled = !this.enabled;
    	}
        for (CheckButtonClickListener listener : listeners) {
            listener.onCheckButtonClick(this);
        }
    }

    public ArrayList<CheckButtonClickListener> getListeners() {

        return listeners;
    }

    public void addListeners(CheckButtonClickListener listener) {

        listeners.add(listener);
    }

    public boolean isEnabled() {

        return enabled;
    }
    public String getDescription() {
        return description;
    }

    public ModeValue getModeValue() {
		return modeValue;
	}

    public SubMenu getSubMenu() {
        return subMenu;
    }


    public void setEnabled(boolean enabled) {

        this.enabled = enabled;
    }
}
