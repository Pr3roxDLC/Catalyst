package com.krazzzzymonkey.catalyst.managers;

import com.krazzzzymonkey.catalyst.gui.click.HudEditor;
import com.krazzzzymonkey.catalyst.gui.click.base.Component;
import com.krazzzzymonkey.catalyst.gui.click.elements.*;
import com.krazzzzymonkey.catalyst.gui.click.listener.CheckButtonClickListener;
import com.krazzzzymonkey.catalyst.gui.click.listener.ColorChangeListener;
import com.krazzzzymonkey.catalyst.gui.click.listener.ComponentClickListener;
import com.krazzzzymonkey.catalyst.gui.click.listener.SliderChangeListener;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.utils.visual.GLUtils;
import com.krazzzzymonkey.catalyst.value.Mode;
import com.krazzzzymonkey.catalyst.value.Value;
import com.krazzzzymonkey.catalyst.value.sliders.DoubleValue;
import com.krazzzzymonkey.catalyst.value.sliders.IntegerValue;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import com.krazzzzymonkey.catalyst.value.types.ColorValue;
import com.krazzzzymonkey.catalyst.value.types.ModeValue;
import com.krazzzzymonkey.catalyst.value.types.SubMenu;

public class HudEditorManager extends HudEditor {

    public void Initialization() {
        addCategoryPanels();
    }

    private void addCategoryPanels() {
        int right = GLUtils.getScreenWidth();
        int framePosX = 100;
        int framePosY = 20;

        ModuleCategory category = ModuleCategory.HUD;
        int frameHeight = 210;
        int frameWidth = 100;
        int hacksCount = 0;
        String name = Character.toString(category.toString().toLowerCase().charAt(0)).toUpperCase() + category.toString().toLowerCase().substring(1);
        Frame frame = new Frame(framePosX, framePosY, frameWidth, frameHeight, name);

        for (final Modules mod : ModuleManager.getModules()) {
            if (mod.getCategory() == category) {
                final ExpandingButton expandingButton = new ExpandingButton(0, 0, frameWidth, 14, frame, mod.getModuleName(), mod, null) {

                    @Override
                    public void onUpdate() {
                        setEnabled(mod.isToggled());
                    }
                };
                expandingButton.addListner(new ComponentClickListener() {

                    @Override
                    public void onComponenetClick(Component component, int button) {
                        mod.toggle();
                    }
                });
                expandingButton.setEnabled(mod.isToggled());

                if (!mod.getValues().isEmpty()) {
                    for (Value value : mod.getValues()) {
                        if (value instanceof BooleanValue) {
                            final BooleanValue booleanValue = (BooleanValue) value;
                            CheckButton button = new CheckButton(0, 0, expandingButton.getDimension().width, 14, expandingButton, booleanValue.getName(), booleanValue.getValue(), null, booleanValue.getDescription(), null);
                            button.addListeners(new CheckButtonClickListener() {

                                @Override
                                public void onCheckButtonClick(CheckButton checkButton) {
                                    for (Value value1 : mod.getValues()) {
                                        if (value1.getName().equals(booleanValue.getName())) {
                                            value1.setValue(checkButton.isEnabled());
                                        }
                                    }
                                }

                            });
                            expandingButton.addComponent(button);

                        } else if (value instanceof DoubleValue) {
                            DoubleValue doubleValue = (DoubleValue) value;
                            Slider slider = new Slider(doubleValue.getMin(), doubleValue.getMax(), doubleValue.getValue(), expandingButton, doubleValue.getName(), doubleValue.getDescription(), null);
                            slider.addListener(new SliderChangeListener() {

                                @Override
                                public void onSliderChange(Slider slider) {
                                    for (Value value1 : mod.getValues()) {
                                        if (value1.getName().equals(value.getName())) {
                                            value1.setValue(slider.getValue());
                                        }
                                    }
                                }
                            });
                            expandingButton.addComponent(slider);

                        } else if (value instanceof IntegerValue) {
                            IntegerValue integerValue = (IntegerValue) value;
                            Slider slider = new Slider(integerValue.getMin(), integerValue.getMax(), integerValue.getValue(), expandingButton, integerValue.getName(), integerValue.getDescription(), null);
                            slider.addListener(new SliderChangeListener() {

                                @Override
                                public void onSliderChange(Slider slider) {
                                    for (Value value1 : mod.getValues()) {
                                        if (value1.getName().equals(value.getName())) {
                                            value1.setValue(slider.getValue());
                                        }
                                    }
                                }
                            });
                            expandingButton.addComponent(slider);

                        } else if (value instanceof ModeValue) {
                            Dropdown dropdown = new Dropdown(0, 0, frameWidth - 2, 14, frame, value.getName(), null);

                            final ModeValue modeValue = (ModeValue) value;

                            for (Mode mode : modeValue.getModes()) {
                                CheckButton button = new CheckButton(0, 0, expandingButton.getDimension().width, 14, expandingButton, mode.getName(), mode.isToggled(), modeValue, "", null);

                                button.addListeners(new CheckButtonClickListener() {
                                    @Override
                                    public void onCheckButtonClick(CheckButton checkButton) {
                                        for (Mode mode1 : modeValue.getModes()) {
                                            if (mode1.getName().equals(mode.getName())) {
                                                mode1.setToggled(checkButton.isEnabled());
                                            }
                                        }
                                    }
                                });
                                dropdown.addComponent(button);
                            }
                            expandingButton.addComponent(dropdown);
                        } else if (value instanceof ColorValue) {
                            final ColorValue colorValue = (ColorValue) value;
                            ColorPicker colorPicker = new ColorPicker(0, 0, frameWidth, 67, colorValue.getValue(), colorValue.getLineColor().getRGB(), colorValue.getTriPos(), colorValue.getSelColorY(), colorValue.getSelOpacityY(), colorValue.getRainbow(), expandingButton, colorValue.getName(), colorValue.getDescription(), colorValue, null);

                            colorPicker.addListener(new ColorChangeListener() {

                                @Override
                                public void onColorChangeClick(ColorPicker colorPicker) {
                                    ((ColorValue) value).setSelColorY(colorPicker.getSelColorY());
                                    ((ColorValue) value).setColor(colorPicker.getColor());
                                    ((ColorValue) value).setLineColor(colorPicker.getLineColor().getRGB());
                                    ((ColorValue) value).setSelOpacityY(colorPicker.getSelOpacityY());
                                }

                            });

                            expandingButton.addComponent(colorPicker);

                        } else if (value instanceof SubMenu) {

                            Dropdown dropdown = new Dropdown(0, 0, frameWidth - 2, 14, frame, value.getName(), null);

                            final SubMenu subMenu = (SubMenu) value;

                            for (Value value1 : subMenu.getValues()) {


                                if (value1 instanceof BooleanValue) {
                                    final BooleanValue booleanValue = (BooleanValue) value1;
                                    CheckButton button = new CheckButton(0, 0, dropdown.getDimension().width, 14, dropdown, booleanValue.getName(), booleanValue.getValue(), null, booleanValue.getDescription(), subMenu);
                                    button.addListeners(new CheckButtonClickListener() {

                                        @Override
                                        public void onCheckButtonClick(CheckButton checkButton) {
                                            for (Value value2 : mod.getValues()) {
                                                if(value2 == subMenu){
                                                    for(Value value3 : subMenu.getValues()){
                                                        if (value3.getName().equals(booleanValue.getName())) {
                                                            value3.setValue(checkButton.isEnabled());
                                                        }
                                                    }
                                                }

                                            }
                                        }

                                    });
                                    dropdown.addComponent(button);

                                } else if (value1 instanceof DoubleValue) {
                                    DoubleValue doubleValue = (DoubleValue) value1;
                                    Slider slider = new Slider(doubleValue.getMin(), doubleValue.getMax(), doubleValue.getValue(), dropdown, doubleValue.getName(), doubleValue.getDescription(), subMenu);
                                    slider.addListener(new SliderChangeListener() {

                                        @Override
                                        public void onSliderChange(Slider slider) {
                                            for (Value value2 : mod.getValues()) {
                                                if(value2 == subMenu){
                                                    for(Value value3 : subMenu.getValues()){
                                                        if (value3.getName().equals(value1.getName())) {
                                                            value3.setValue(slider.getValue());
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    });
                                    dropdown.addComponent(slider);

                                } else if (value1 instanceof IntegerValue) {
                                    IntegerValue integerValue = (IntegerValue) value1;
                                    Slider slider = new Slider(integerValue.getMin(), integerValue.getMax(), integerValue.getValue(), dropdown, integerValue.getName(), integerValue.getDescription(), subMenu);
                                    slider.addListener(new SliderChangeListener() {

                                        @Override
                                        public void onSliderChange(Slider slider) {
                                            for (Value value2 : mod.getValues()) {
                                                if(value2 == subMenu){
                                                    for(Value value3 : subMenu.getValues()){
                                                        if (value3.getName().equals(value1.getName())) {
                                                            value3.setValue(slider.getValue());
                                                        }
                                                    }
                                                }


                                            }
                                        }
                                    });
                                    dropdown.addComponent(slider);

                                } else if (value1 instanceof ModeValue) {
                                    Dropdown dropdown1 = new Dropdown(0, 0, frameWidth - 2, 14, frame, value1.getName(), subMenu);

                                    final ModeValue modeValue = (ModeValue) value1;

                                    for (Mode mode : modeValue.getModes()) {
                                        CheckButton button = new CheckButton(0, 0, dropdown.getDimension().width, 14, dropdown, mode.getName(), mode.isToggled(), modeValue, "", subMenu);

                                        button.addListeners(new CheckButtonClickListener() {
                                            @Override
                                            public void onCheckButtonClick(CheckButton checkButton) {
                                                for (Mode mode1 : modeValue.getModes()) {
                                                    if (mode1.getName().equals(mode.getName())) {
                                                        mode1.setToggled(checkButton.isEnabled());
                                                    }
                                                }
                                            }
                                        });
                                        dropdown1.addComponent(button);
                                    }
                                    dropdown.addComponent(dropdown1);
                                } else if (value1 instanceof ColorValue) {
                                    final ColorValue colorValue = (ColorValue) value1;
                                    ColorPicker colorPicker = new ColorPicker(0, 0, frameWidth, 67, colorValue.getValue(), colorValue.getLineColor().getRGB(), colorValue.getTriPos(), colorValue.getSelColorY(), colorValue.getSelOpacityY(), colorValue.getRainbow(), dropdown, colorValue.getName(), colorValue.getDescription(), colorValue, subMenu);

                                    colorPicker.addListener(new ColorChangeListener() {

                                        @Override
                                        public void onColorChangeClick(ColorPicker colorPicker) {
                                            ((ColorValue) value1).setSelColorY(colorPicker.getSelColorY());
                                            ((ColorValue) value1).setColor(colorPicker.getColor());
                                            ((ColorValue) value1).setLineColor(colorPicker.getLineColor().getRGB());
                                            ((ColorValue) value1).setSelOpacityY(colorPicker.getSelOpacityY());
                                        }

                                    });

                                    dropdown.addComponent(colorPicker);

                                }
                            }
                            expandingButton.addComponent(dropdown);
                        }
                    }
                }
                KeybindMods keybind = new KeybindMods(-1, 0, 8, 14, expandingButton, mod);
                expandingButton.addComponent(keybind);
                frame.addComponent(expandingButton);
                hacksCount++;
            }


        }
        frame.setMaximizible(true);
        frame.setPinnable(true);
        frame.setMaximized(true);
        this.addFrame(frame);
        frame.updateComponents();
        if (!FileManager.CLICKGUI.exists())
            FileManager.saveClickGui();
        else FileManager.loadClickGui();
    }
}


