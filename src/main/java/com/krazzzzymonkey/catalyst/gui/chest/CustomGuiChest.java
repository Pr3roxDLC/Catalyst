package com.krazzzzymonkey.catalyst.gui.chest;

import com.krazzzzymonkey.catalyst.utils.system.Wrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import java.io.IOException;

public class CustomGuiChest extends GuiChest {
    public CustomGuiChest(IInventory upperInv, IInventory lowerInv) {
        super(upperInv, lowerInv);

        this.mc = Minecraft.getMinecraft();
        ScaledResolution sr = new ScaledResolution(mc);

        this.setWorldAndResolution(this.mc, sr.getScaledWidth(), sr.getScaledHeight());
    }

    @Override
    public void initGui() {
        super.initGui();

        if (this.inventorySlots.inventorySlots.size() == 63) {
            this.buttonList.add(new CustomGuiButton(1338, this.width / 2 - 80, this.height / 2 - this.ySize + 70, 50, 10, "Store"));
            this.buttonList.add(new CustomGuiButton(1337, this.width / 2 - 28, this.height / 2 - this.ySize + 70, 50, 10, "Steal"));
            this.buttonList.add(new CustomGuiButton(1339, this.width / 2 + 24, this.height / 2 - this.ySize + 70, 50, 10, "Drop"));
        } else {
            this.buttonList.add(new CustomGuiButton(1338, this.width / 2 - 80, this.height / 2 - this.ySize + 100, 50, 10, "Store"));
            this.buttonList.add(new CustomGuiButton(1337, this.width / 2 - 28, this.height / 2 - this.ySize + 100, 50, 10, "Steal"));
            this.buttonList.add(new CustomGuiButton(1339, this.width / 2 + 24, this.height / 2 - this.ySize + 100, 50, 10, "Drop"));
        }

    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);

        switch (button.id) {
            case 1337:
                runThread("STEAL");
                break;
            case 1338:
                runThread("STORE");
                break;
            case 1339:
                runThread("DROP");
                break;
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
    }


    private void runThread(String action) {
        (new Thread(() -> {
            try {
                if (action.equals("STEAL")) {
                    if ((!Wrapper.INSTANCE.mc().inGameHasFocus)
                        && ((Wrapper.INSTANCE.mc().currentScreen instanceof GuiChest))) {
                        if (!isContainerEmpty(mc.player.openContainer)) {
                            for (int i = 0; i < mc.player.openContainer.inventorySlots.size() - 36; ++i) {
                                Slot slot = mc.player.openContainer.getSlot(i);
                                if (slot.getHasStack()) {
                                    Thread.sleep(200);
                                    if((Wrapper.INSTANCE.mc().currentScreen instanceof GuiChest)){
                                        Wrapper.INSTANCE.mc().playerController.windowClick(mc.player.openContainer.windowId, i, 1, ClickType.QUICK_MOVE, mc.player);
                                    }else break;

                                }
                            }
                        } else {
                            mc.player.closeScreen();
                        }
                    }
                } else if (action.equals("DROP")) {
                    if ((!Wrapper.INSTANCE.mc().inGameHasFocus)
                        && ((Wrapper.INSTANCE.mc().currentScreen instanceof GuiChest))) {
                        for (int i = 0; i < mc.player.openContainer.inventorySlots.size() - 36; ++i) {
                            final ItemStack itemStack = mc.player.openContainer.getInventory().get(i);
                            if (!itemStack.isEmpty() && itemStack.getItem() != Items.AIR) {
                                Thread.sleep(200);
                                if((Wrapper.INSTANCE.mc().currentScreen instanceof GuiChest)){
                                    mc.playerController.windowClick(mc.player.openContainer.windowId, i, 0, ClickType.PICKUP, mc.player);
                                    mc.playerController.windowClick(mc.player.openContainer.windowId, -999, 0, ClickType.PICKUP, mc.player);
                                }else break;

                            }
                        }
                    }
                } else if (action.equals("STORE")) {
                    if ((!Wrapper.INSTANCE.mc().inGameHasFocus)
                        && ((Wrapper.INSTANCE.mc().currentScreen instanceof GuiChest))) {

                        int i = 54;
                        if (this.inventorySlots.inventorySlots.size() == 63) {
                            i = 27;
                        }

                        for (; i < mc.player.openContainer.inventorySlots.size(); ++i) {

                            ItemStack itemStack = mc.player.openContainer.inventorySlots.get(i).getStack();
                            if (itemStack.isEmpty() || itemStack.getItem() == Items.AIR)
                                continue;
                            Thread.sleep(200);
                            if((Wrapper.INSTANCE.mc().currentScreen instanceof GuiChest)){
                                Wrapper.INSTANCE.mc().playerController.windowClick(mc.player.openContainer.windowId, i, 1, ClickType.QUICK_MOVE, mc.player);
                            }else break;

                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        })
        ).start();
    }

    boolean isContainerEmpty(Container container) {
        boolean temp = true;
        int i = 0;
        for (int slotAmount = container.inventorySlots.size() == 90 ? 54 : 35; i < slotAmount; i++) {
            if (container.getSlot(i).getHasStack()) {
                temp = false;
            }
        }
        return temp;
    }

}
