package com.krazzzzymonkey.catalyst.gui.click.theme.dark;

import com.krazzzzymonkey.catalyst.gui.click.base.ComponentType;
import com.krazzzzymonkey.catalyst.gui.click.theme.Theme;
import com.krazzzzymonkey.catalyst.utils.system.Wrapper;

public class DarkTheme extends Theme {

    public DarkTheme() {
        super("CatalystDark");
        this.fontRenderer = Wrapper.INSTANCE.fontRenderer();
        addRenderer(ComponentType.FRAME, new DarkFrame(this));
        addRenderer(ComponentType.BUTTON, new DarkButton(this));
        addRenderer(ComponentType.SLIDER, new DarkSlider(this));
        addRenderer(ComponentType.CHECK_BUTTON, new DarkCheckButton(this));
        addRenderer(ComponentType.COLOR_PICKER, new DarkColorPicker(this));
        addRenderer(ComponentType.EXPANDING_BUTTON, new DarkExpandingButton(this));
        addRenderer(ComponentType.TEXT, new DarkText(this));
        addRenderer(ComponentType.KEYBIND, new DarkKeybinds(this));
        addRenderer(ComponentType.DROPDOWN, new DarkDropDown(this));
        addRenderer(ComponentType.COMBO_BOX, new DarkComboBox(this));
    }
}
