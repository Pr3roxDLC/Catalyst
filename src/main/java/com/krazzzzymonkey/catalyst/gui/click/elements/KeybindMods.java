package com.krazzzzymonkey.catalyst.gui.click.elements;

import com.krazzzzymonkey.catalyst.gui.click.base.Component;
import com.krazzzzymonkey.catalyst.gui.click.base.ComponentType;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.utils.system.Wrapper;
import org.lwjgl.input.Keyboard;

public class KeybindMods extends Component {

    private final Modules mod;

    private boolean editing;

    public KeybindMods(int xPos, int yPos, int width, int height, Component component, Modules mod) {

        super(xPos, yPos, width, height, ComponentType.KEYBIND, component, "", "");
        this.mod = mod;
    }

    @Override
    public void onUpdate() {
        if (Keyboard.getEventKeyState()) {
            if (editing) {
                if (Keyboard.getEventKey() == Keyboard.KEY_DELETE)
                    mod.setKey(-1);
                else if (Keyboard.getEventKey() == Keyboard.KEY_BACK)
                    mod.setKey(-1);
                else {
                    mod.setKey(Keyboard.getEventKey());
                }
                editing = false;
            }
        }
    }


    @Override
    public void onMousePress(int x, int y, int buttonID) {
        if (x > this.getX() + Wrapper.INSTANCE.fontRenderer().getStringWidth("Key") + 6 && x < this.getX() + this.getDimension().width && y > this.getY() && y < this.getY() + this.getDimension().height) {
            editing = !editing;
        }else {
            mod.setBindHold(!mod.isBindHold());
        }
    }

    public Modules getMod() {
        return mod;
    }

    public boolean isEditing() {
        return editing;
    }

    public void setEditing(boolean editing) {
        this.editing = editing;
    }
}
