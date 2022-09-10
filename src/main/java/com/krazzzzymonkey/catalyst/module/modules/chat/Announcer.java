package com.krazzzzymonkey.catalyst.module.modules.chat;

import com.krazzzzymonkey.catalyst.events.ClientTickEvent;
import com.krazzzzymonkey.catalyst.events.PacketEvent;
import com.krazzzzymonkey.catalyst.events.ReachEvent;
import com.krazzzzymonkey.catalyst.managers.ModuleManager;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import com.krazzzzymonkey.catalyst.value.sliders.IntegerValue;

import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.item.ItemExpBottle;
import net.minecraft.item.ItemFood;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketEnchantItem;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumHand;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.text.DecimalFormat;
import java.util.concurrent.ThreadLocalRandom;

public class Announcer extends Modules {

    public IntegerValue settingDelay;

    static long lastPositionUpdate;
    static double lastPositionX;
    static double lastPositionY;
    static double lastPositionZ;
    private static double speed;

    String heldItem = "";

    public BooleanValue walkDistance;
    public BooleanValue placeBlock;
    public BooleanValue breakBlock;
    public BooleanValue eatItem;
    public BooleanValue throwItem;
    public BooleanValue booleanSneak;
    public BooleanValue booleanGui;
    public BooleanValue enchantItem;

    public BooleanValue jumping;
    public BooleanValue greenText;

    //TODO ADD MORE EVENTS TO BE ANNOUNCED
    public Announcer() {
        super("Announcer", ModuleCategory.CHAT, "Announces Player actions");

        settingDelay = new IntegerValue("Chat Delay", 50, 1, 500, "");

        walkDistance = new BooleanValue("Walking", true, "Announces how many blocks you have traveled");
        placeBlock = new BooleanValue("PlaceBlock", true, "Announces when you place blocks");
        breakBlock = new BooleanValue("BreakBlock", true, "Announces when you break blocks");
        eatItem = new BooleanValue("ItemEat", true, "Announces when you eat food");
        throwItem = new BooleanValue("ItemThrow", true, "Announces when you throw an item out of your inventory");
        booleanSneak = new BooleanValue("Sneaking", true, "Announces when you sneak");
        booleanGui = new BooleanValue("Click Gui", true, "Announces when you open the Catalyst ClickGui");
        enchantItem = new BooleanValue("EnchantItem", true, "Announces when you enchant an item");
        jumping = new BooleanValue("Jumping", true, "Announces when you jump");
        // booleanGainHealth = new BooleanValue("GainHealth", true);
        greenText = new BooleanValue("GreenText", true, "Adds \">\" to the beginning of your message making it green on some severs");

        this.addValue(settingDelay, walkDistance, placeBlock, breakBlock, eatItem, throwItem, booleanSneak, booleanGui, enchantItem, greenText);
    }


    public static String walkMessage = "I just walked {blocks} blocks thanks to Catalyst!";
    public static String throwMessage = "I just threw {amount} {name} thanks to Catalyst! ";
    public static String placeMessage = "I just placed {amount} {name} thanks to Catalyst!";
    public static String jumpMessage = "I just jumped thanks to Catalyst!";
    public static String sneakMessage = "I started sneaking thanks to Catalyst!";
    public static String unSneakMessage = "I stopped sneaking thanks to Catalyst!";
    public static String breakMessage = "I just broke {amount} {name} thanks to Catalyst!";
    public static String attackMessage = "I just attacked {name} with a {item} thanks to Catalyst!";
    public static String eatMessage = "I just ate {amount} {name} thanks to Catalyst!";
    public static String openGuiMessage = "I just opened Catalyst ClickGUI!";
    public static String closeGuiMessage = "I just closed Catalyst ClickGUI!";
    public static String enchantMessage = "I just enchanted an item thanks to Catalyst!";

    int blocksPlaced = 0;
    int blocksBroken = 0;
    int xpThrown = 0;
    static int blockPlacedDelay = 0;
    public static int blockBrokeDelay = 0;
    static int jumpDelay = 0;
    static int attackDelay = 0;
    static int eatingDelay = 0;
    int eaten = 0;
    float health;
    boolean clickGuiIsOpen = false;
    boolean sneak = false;
    boolean isEating = false;
    int itemStack = 0;
    boolean hasHealth = false;
    boolean isInAir = false;
    int Delay = 0;
    int InAir = 0;
    int healthUpdate = 0;
    String prefix = "";
    int delay = 0;

    @EventHandler
    private final EventListener<ClientTickEvent> onClientTick = new EventListener<>(e -> {

        if (Minecraft.getMinecraft().world == null || Minecraft.getMinecraft().player == null || Minecraft.getMinecraft().player.getUniqueID() == null)
            return;

        delay = 1 + settingDelay.getValue().intValue();

        if (greenText.getValue()) {
            prefix = "> ";
        }
        if (!greenText.getValue()) {
            prefix = "";
        }
        if (Minecraft.getMinecraft().world == null || Minecraft.getMinecraft().player == null || Minecraft.getMinecraft().player.getUniqueID() == null)
            return;
        InAir++;
        Delay++;
        Modules clickGUI = ModuleManager.getModule("ClickGui");

        blockBrokeDelay++;
        blockPlacedDelay++;
        jumpDelay++;
        attackDelay++;
        eatingDelay++;
        heldItem = Minecraft.getMinecraft().player.getHeldItemMainhand().getDisplayName();

        if (walkDistance.getValue()) {
            if (lastPositionUpdate + (5000L * 2) < System.currentTimeMillis()) {

                double d0 = lastPositionX - Minecraft.getMinecraft().player.lastTickPosX;
                double d2 = lastPositionY - Minecraft.getMinecraft().player.lastTickPosY;
                double d3 = lastPositionZ - Minecraft.getMinecraft().player.lastTickPosZ;

                speed = Math.sqrt(d0 * d0 + d2 * d2 + d3 * d3);

                if (!(speed <= 1) && !(speed > 5000)) {
                    String walkAmount = new DecimalFormat("0").format(speed);
                    if (Delay > delay) {
                        Minecraft.getMinecraft().player.sendChatMessage(prefix + walkMessage.replace("{blocks}", walkAmount));
                        Delay = 0;

                    }
                }
                lastPositionUpdate = System.currentTimeMillis();
                lastPositionX = Minecraft.getMinecraft().player.lastTickPosX;
                lastPositionY = Minecraft.getMinecraft().player.lastTickPosY;
                lastPositionZ = Minecraft.getMinecraft().player.lastTickPosZ;
            }
        }
        if (booleanSneak.getValue()) {
            if (Minecraft.getMinecraft().player.isSneaking()) {
                if (Delay > delay && !sneak) {
                    Minecraft.getMinecraft().player.sendChatMessage(prefix + sneakMessage);
                    sneak = true;
                    Delay = 0;
                }
            }
            if (!Minecraft.getMinecraft().player.isSneaking() && sneak) {
                if (Delay > delay) {
                    Minecraft.getMinecraft().player.sendChatMessage(prefix + unSneakMessage);
                    sneak = false;
                    Delay = 0;
                }
            }
        }
        if (booleanGui.getValue()) {
            if (clickGUI.isToggled() && !clickGuiIsOpen) {
                if (Delay > delay) {
                    Minecraft.getMinecraft().player.sendChatMessage(prefix + openGuiMessage);
                    clickGuiIsOpen = true;
                    Delay = 0;
                }
                if (!clickGUI.isToggled() && clickGuiIsOpen) {
                    if (Delay > delay) {
                        Minecraft.getMinecraft().player.sendChatMessage(prefix + closeGuiMessage);
                        clickGuiIsOpen = false;
                        Delay = 0;
                    }
                }
            }
        }
        if (jumping.getValue()) {
            if (Minecraft.getMinecraft().player.fallDistance > 1.1) {
                isInAir = true;
            }
        }
        if (jumping.getValue()) {
            if (isInAir && Minecraft.getMinecraft().player.fallDistance == 0) {
                if (InAir > 15) {
                    if (Delay - 200 > delay) {
                        Minecraft.getMinecraft().player.sendChatMessage(prefix + jumpMessage);
                        isInAir = false;
                        Delay = 0;
                    }
                } else {
                    isInAir = false;
                    InAir = 0;
                }
            }
        }
    });

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent e) {
        Block block = e.getState().getBlock();
        blocksBroken++;
        int randomNum = ThreadLocalRandom.current().nextInt(1, 10 + 1);
        if (breakBlock.getValue() && blocksBroken > randomNum) {
            String msg = breakMessage.replace("{amount}", blocksBroken + "").replace("{name}", block.getLocalizedName());

            if (Delay > delay) {
                Minecraft.getMinecraft().player.sendChatMessage(prefix + msg);
                Delay = 0;
                blocksBroken = 0;
            }
        }
    }

    @EventHandler
    private final EventListener<PacketEvent> onPacket = new EventListener<>(e -> {
        if(e.getSide() == PacketEvent.Side.OUT) {
            Packet packet = e.getPacket();

            if (packet instanceof CPacketPlayerTryUseItemOnBlock && Minecraft.getMinecraft().player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemBlock) {
                if (Minecraft.getMinecraft().player.getHeldItemMainhand().getDisplayName() != "Air") {
                    blocksPlaced++;
                    int randomNum = ThreadLocalRandom.current().nextInt(1, 10 + 1);

                    if (placeBlock.getValue() && blocksPlaced > randomNum) {

                        String msg = placeMessage.replace("{amount}", blocksPlaced + "").replace("{name}", Minecraft.getMinecraft().player.getHeldItemMainhand().getDisplayName());

                        if (Delay > delay) {
                            Minecraft.getMinecraft().player.sendChatMessage(prefix + msg);
                            Delay = 0;
                            blocksPlaced = 0;
                        }
                    }
                }
            }
            if (packet instanceof CPacketPlayerTryUseItem && Minecraft.getMinecraft().player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemExpBottle) {
                xpThrown++;
                int randomNum = ThreadLocalRandom.current().nextInt(1, 30 + 1);
                if (throwItem.getValue() && xpThrown > randomNum) {
                    String msg = throwMessage.replace("{amount}", xpThrown + "").replace("{name}", Minecraft.getMinecraft().player.getHeldItemMainhand().getDisplayName());

                    if (Delay > delay) {
                        Minecraft.getMinecraft().player.sendChatMessage(prefix + msg);
                        Delay = 0;
                        xpThrown = 0;
                    }
                }
            }
            if (packet instanceof CPacketPlayerTryUseItem && Minecraft.getMinecraft().player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemEnderPearl) {
                if (throwItem.getValue()) {
                    String msg = throwMessage.replace("{amount}", 1 + "").replace("{name}", Minecraft.getMinecraft().player.getHeldItemMainhand().getDisplayName());
                    if (Delay > delay) {
                        Minecraft.getMinecraft().player.sendChatMessage(prefix + msg);
                        Delay = 0;
                    }
                }
            }
            if (packet instanceof CPacketEnchantItem) {
                if (enchantItem.getValue()) {
                    String msg = enchantMessage;
                    if (Delay > delay) {
                        Minecraft.getMinecraft().player.sendChatMessage(prefix + msg);
                        Delay = 0;
                    }
                }
            }
            if (packet instanceof CPacketPlayerTryUseItem) {
                if (eatItem.getValue()) {
                    if (Minecraft.getMinecraft().player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemFood) {
                        if (!isEating) {
                            itemStack = Minecraft.getMinecraft().player.getHeldItem(EnumHand.MAIN_HAND).getCount();

                            isEating = true;
                        }
                        if (isEating) {
                            if (Minecraft.getMinecraft().player.getHeldItem(EnumHand.MAIN_HAND).getCount() < itemStack) {
                                eaten++;

                            }
                        }
                        if (eaten > 0) {
                            String msg = (eatMessage.replace("{amount}", eaten + "").replace("{name}", Minecraft.getMinecraft().player.getHeldItemMainhand().getDisplayName()));

                            if (Delay > delay) {
                                Minecraft.getMinecraft().player.sendChatMessage(prefix + msg);
                                isEating = false;
                                Delay = 0;
                                eaten = 0;
                            }

                        }


                    }

                }
            }
        }
    });
}

