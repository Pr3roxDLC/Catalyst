package com.krazzzzymonkey.catalyst.module.modules.gui;

import com.krazzzzymonkey.catalyst.Main;
import com.krazzzzymonkey.catalyst.command.Command;
import com.krazzzzymonkey.catalyst.events.*;
import com.krazzzzymonkey.catalyst.gui.chest.CustomGuiChest;
import com.krazzzzymonkey.catalyst.gui.click.ClickGuiScreen;
import com.krazzzzymonkey.catalyst.managers.*;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.module.modules.chat.ChatMention;
import com.krazzzzymonkey.catalyst.module.modules.chat.CustomChat;
import com.krazzzzymonkey.catalyst.module.modules.misc.NoEntityTrace;
import com.krazzzzymonkey.catalyst.module.modules.misc.Sounds;
import com.krazzzzymonkey.catalyst.module.modules.render.*;
import com.krazzzzymonkey.catalyst.module.modules.world.Scaffold;
import com.krazzzzymonkey.catalyst.utils.system.Wrapper;
import com.krazzzzymonkey.catalyst.utils.visual.ColorUtils;
import com.krazzzzymonkey.catalyst.utils.visual.RenderUtils;
import com.krazzzzymonkey.catalyst.value.Mode;
import com.krazzzzymonkey.catalyst.value.sliders.DoubleValue;
import com.krazzzzymonkey.catalyst.value.sliders.IntegerValue;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import com.krazzzzymonkey.catalyst.value.types.ColorValue;
import com.krazzzzymonkey.catalyst.value.types.ModeValue;
import com.krazzzzymonkey.catalyst.value.types.Number;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.krazzzzymonkey.catalyst.managers.ModuleManager.EVENT_MANAGER;

//TODO FIX SLIDERS, FIX DRAGGING THE WRONG FRAME
//BIG BIG BIG TODO, REFACTOR THIS MIXIN FUCKERY TO A MixinProxy class TO COMMUNICATE WITH THE OBFED CLIENT AND THE NON OBFED MIXINS
public class ClickGui extends Modules {

    public static ModeValue rainbowMode;
    public static BooleanValue rainbow;
    public static BooleanValue shadow;
    public static BooleanValue blur;
    public static BooleanValue tooltip;
    public static IntegerValue rainbowSpeed;
    public static DoubleValue rainbowHue;
    public static Number clickGuiScale;
    public static ColorValue clickGuiColor;
    public static ColorValue clickGuiBackGroundColor;
    public static ColorValue clickGuiToggledColor;

    private static int color;


    public ClickGui() {
        super("ClickGui", ModuleCategory.GUI, "Settings for ClickGui");
        this.setKey(Keyboard.KEY_RCONTROL);
        tooltip = new BooleanValue("Tooltip", true, "Shows these tooltips you are reading right now");
        shadow = new BooleanValue("Shadow", true, "Darkens the background of the click gui");
        blur = new BooleanValue("Blur", false   , "Blurs the background of the click gui");
        clickGuiColor = new ColorValue("HeaderColor", Color.CYAN, "Changes the header color of the click gui");
        clickGuiBackGroundColor = new ColorValue("BackgroundColor", Color.BLACK, "Changes the background color of the click gui");
        clickGuiToggledColor = new ColorValue("ToggledColor", Color.CYAN, "Changes the color of all toggled modules in the click gui");
        rainbow = new BooleanValue("Rainbow", false, "Makes the whole click gui cycle through colors");
        rainbowMode = new ModeValue("RainbowMode", new Mode("RainbowFlow", true), new Mode("Static", false));
        rainbowSpeed = new IntegerValue("RainbowSpeed", 50, 0, 100, "The speed at which the rainbow flow is");
        rainbowHue = new DoubleValue("RainbowHue", 0.1, 0.1D, 1, "The difference of color between each module when in rainbow flow");
        clickGuiScale = new Number("Scale", 1.0);

        this.addValue(clickGuiScale, tooltip, shadow, blur, clickGuiColor, clickGuiBackGroundColor, clickGuiToggledColor, rainbow, rainbowMode, rainbowSpeed, rainbowHue);
        setColor();
    }

    public static int getColor() {
        return rainbow.getValue() ? ColorUtils.rainbow().getRGB() : color;
    }

    public static void setColor() {
        color = clickGuiColor.getColor().getRGB();
    }

    @Override
    public void onEnable() {
        FileManager.saveModules(ProfileManager.currentProfile);
        super.onEnable();
        if (mc.player == null || mc.world == null) {
            return;
        }
        Wrapper.INSTANCE.mc().displayGuiScreen(Main.moduleManager.getGui());
    }

    @EventHandler
    private final EventListener<ClientTickEvent> onClientTick = new EventListener<>(e -> {
        setColor();

        if (Minecraft.getMinecraft().currentScreen instanceof ClickGuiScreen) return;
        this.toggle();

    });

    @EventHandler
    private final EventListener<RenderGameOverlayEvent.Text> onRenderGameOverlay = new EventListener<>(e -> {
        if (Minecraft.getMinecraft().world == null || Minecraft.getMinecraft().player == null)
            return;

        if (Minecraft.getMinecraft().currentScreen instanceof GuiContainer) return;
        if (Minecraft.getMinecraft().currentScreen instanceof GuiChat) return;
        if (Minecraft.getMinecraft().currentScreen instanceof GuiIngameMenu) return;

        if (Minecraft.getMinecraft().entityRenderer.getShaderGroup() == null && blur.getValue()) {
            try {
                Minecraft.getMinecraft().entityRenderer.loadShader(new ResourceLocation("shaders/post/blur.json"));
            } catch (Exception ignored) {
            }
        }

        if (shadow.getValue()) {
            ScaledResolution sr = new ScaledResolution(Wrapper.INSTANCE.mc());
            RenderUtils.drawRect(0, 0, sr.getScaledWidth(), sr.getScaledHeight(), ColorUtils.color(0.0F, 0.0F, 0.0F, 0.5F));
        }

    });

    @Override
    public void onDisable() {
        FileManager.saveModules(ProfileManager.currentProfile);
        super.onDisable();
        if (mc.player == null || mc.world == null) {
            return;
        }
        if (Minecraft.getMinecraft().entityRenderer.getShaderGroup() != null) {
            Minecraft.getMinecraft().entityRenderer.getShaderGroup().deleteShaderGroup();
        }
        mc.currentScreen = null;

    }


    // this is mixin shit

    //Moved to MixinProxy
}
