package com.krazzzzymonkey.catalyst.gui.click.elements;

import com.krazzzzymonkey.catalyst.gui.click.base.Component;
import com.krazzzzymonkey.catalyst.gui.click.base.ComponentType;

public class Text extends Component {

    private final String[] text;

    public Text(int xPos, int yPos, int width, int height, Component component, String[] text) {

        super(xPos, yPos, width, height, ComponentType.TEXT, component, "", "");
        this.text = text;
    }

    public String[] getMessage() {

        return text;
    }
}
