package com.krazzzzymonkey.catalyst.module.modules.combat;

import com.krazzzzymonkey.catalyst.events.MotionEvent;
import com.krazzzzymonkey.catalyst.events.StopUsingItemEvent;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.utils.Timer;
import com.krazzzzymonkey.catalyst.value.sliders.IntegerValue;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumHand;

public class Quiver extends Modules {

    private final BooleanValue speed = new BooleanValue("Swiftness", false, "Shoot swiftness arrows");
    private final BooleanValue strength = new BooleanValue("Strength", false, "Shoot strength arrows");
    private final BooleanValue autoSwitch = new BooleanValue("AutoSwitch", false, "Automatically switch to a bow when toggled");
    private final BooleanValue rearrange = new BooleanValue("Rearrange", false, "Rearrange arrows in inventory");
    private final BooleanValue noGapSwitch = new BooleanValue("NoGapSwitch", false, "Will not auto switch when eating");
    private final IntegerValue health = new IntegerValue("MinHealth", 10, 1, 36, "Minimum health needed to shoot the arrow");

    public Quiver() {
        super("Quiver", ModuleCategory.COMBAT, "Shoots yourself with positive arrows");
        this.addValue(speed, strength, autoSwitch, rearrange, noGapSwitch, health);
    }


    private final Timer timer = new Timer();

    private boolean cancelStopUsingItem = false;
    int oldSlot = -1;

    @EventHandler
    private final EventListener<MotionEvent.PRE> onClientTick = new EventListener<>(e -> {
        if (mc.player == null || mc.world == null) return;

        if (e.isCancelled()) return;

        if (!timer.passedMs(2500)) return;

        if (mc.player.getHealth() + mc.player.getAbsorptionAmount() < health.getValue()) return;

        if (noGapSwitch.getValue() && mc.player.getActiveItemStack().getItem() instanceof ItemFood) return;

        if (strength.getValue() && !mc.player.isPotionActive(MobEffects.STRENGTH)) {
            if (isFirstAmmoValid("Arrow of Strength")) {
                shootBow();
            }/* else if (toggelable.getValue()) {
                toggle();
            }*/
        }

        if (speed.getValue() && !mc.player.isPotionActive(MobEffects.SPEED)) {
            if (isFirstAmmoValid("Arrow of Swiftness")) {
                shootBow();
            } /*else if (toggelable.getValue()) {
                toggle();
            }*/
        }

    });

    @EventHandler
    private final EventListener<StopUsingItemEvent> onStopUsingItem = new EventListener<>(e -> {
        if (cancelStopUsingItem) {
            e.setCancelled(true);
        }else if(autoSwitch.getValue() && oldSlot != -1 && mc.player.inventory.getCurrentItem().getItem() == Items.BOW){
            mc.player.inventory.currentItem = oldSlot;
            mc.playerController.updateController();
        }
    });

    private void shootBow() {
        if (mc.player.inventory.getCurrentItem().getItem() == Items.BOW) {
            mc.player.connection.sendPacket(new CPacketPlayer.Rotation(0, -90, mc.player.onGround));
            mc.player.lastReportedYaw = 0;
            mc.player.lastReportedPitch = -90;
            if (mc.player.getItemInUseMaxCount() >= 3) {
                cancelStopUsingItem = false;
                mc.playerController.onStoppedUsingItem(mc.player);
                timer.reset();
            } else if (mc.player.getItemInUseMaxCount() == 0) {
                mc.playerController.processRightClick(mc.player, mc.world, EnumHand.MAIN_HAND);
                cancelStopUsingItem = true;
            }
        } else if (autoSwitch.getValue()) {
            int bowSlot = getBowSlot();
            if(mc.player.inventory.currentItem != bowSlot){
                oldSlot = mc.player.inventory.currentItem;
            }
            if (bowSlot != -1 && bowSlot != mc.player.inventory.currentItem) {
                mc.player.inventory.currentItem = bowSlot;
                mc.playerController.updateController();
            }
        }
    }

    public int getBowSlot() {
        int bowSlot = -1;

        if (mc.player.getHeldItemMainhand().getItem() == Items.BOW) {
            bowSlot = mc.player.inventory.currentItem;
        }


        if (bowSlot == -1) {
            for (int l = 0; l < 9; ++l) {
                if (mc.player.inventory.getStackInSlot(l).getItem() == Items.BOW) {
                    bowSlot = l;
                    break;
                }
            }
        }
        return bowSlot;
    }

    private boolean isFirstAmmoValid(String type) {
        for (int i = 0; i < 36; i++) {
            ItemStack itemStack = mc.player.inventory.getStackInSlot(i);
            if (itemStack.getItem() == Items.TIPPED_ARROW) {
                boolean matches = itemStack.getDisplayName().equalsIgnoreCase(type);
                if (matches) {
                    return true;
                } else if (rearrange.getValue()) {
                    return rearrangeArrow(i, type);
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    private boolean rearrangeArrow(int fakeSlot, String type) {
        for (int i = 0; i < 36; i++) {
            ItemStack itemStack = mc.player.inventory.getStackInSlot(i);
            if (itemStack.getItem() == Items.TIPPED_ARROW) {
                if (itemStack.getDisplayName().equalsIgnoreCase(type)) {
                    mc.playerController.windowClick(0, fakeSlot, 0, ClickType.PICKUP, mc.player);
                    mc.playerController.windowClick(0, i, 0, ClickType.PICKUP, mc.player);
                    mc.playerController.windowClick(0, fakeSlot, 0, ClickType.PICKUP, mc.player);
                    return true;
                }
            }
        }
        return false;
    }
}
