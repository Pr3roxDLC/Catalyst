package com.krazzzzymonkey.catalyst.module.modules.hud;

import com.krazzzzymonkey.catalyst.events.ClientTickEvent;
import com.krazzzzymonkey.catalyst.events.RenderGameOverlayEvent;
import com.krazzzzymonkey.catalyst.gui.click.HudGuiScreen;
import com.krazzzzymonkey.catalyst.managers.FontManager;
import com.krazzzzymonkey.catalyst.managers.FriendManager;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.utils.MathUtils;
import com.krazzzzymonkey.catalyst.utils.MouseUtils;
import com.krazzzzymonkey.catalyst.utils.font.CFontRenderer;
import com.krazzzzymonkey.catalyst.utils.system.Wrapper;
import com.krazzzzymonkey.catalyst.utils.visual.ColorUtils;
import com.krazzzzymonkey.catalyst.utils.visual.RenderUtils;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import com.krazzzzymonkey.catalyst.value.types.Number;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.Comparator;

import static com.krazzzzymonkey.catalyst.utils.EntityUtils.isHostileMob;

//TODO GET ACTUAL TARGET ENTITY(KA,CA)

public class TargetHUD extends Modules {

    private final BooleanValue players;
    private final BooleanValue monsters;
    private final BooleanValue animals;
    private final BooleanValue friends;
    private final BooleanValue ambient;
    private final BooleanValue squid;
    private Number xOffset;
    private Number yOffset;

    public TargetHUD() {
        super("TargetHUD", ModuleCategory.HUD, "Shows information about the nearest entity", true);
        this.players = new BooleanValue("Players", true, "Targets players in target hud");
        this.monsters = new BooleanValue("Monsters", true, "Targets monster creatures in target hud");
        this.animals = new BooleanValue("Animals", true, "Targets animals in target hud");
        this.friends = new BooleanValue("Friends", false, "Targets friended players in target hud");
        this.ambient = new BooleanValue("Ambient", false, "Targets ambient creatures in target hud");
        this.squid = new BooleanValue("Squid", false, "Targets squids in target hud");

        this.xOffset = new Number("X Offset", 0.0);
        this.yOffset = new Number("y Offset", 15.0);
        this.addValue(players, monsters, animals, friends, ambient, squid, xOffset, yOffset);
    }

    int x;
    int y;

    @EventHandler
    private final EventListener<ClientTickEvent> onClientTick = new EventListener<>(e -> {
        if (Minecraft.getMinecraft().world == null || Minecraft.getMinecraft().player == null)return;

        x = xOffset.getValue().intValue();
        y = yOffset.getValue().intValue();

    });

    int finalMouseX = 0, finalMouseY = 0;
    boolean isDragging = false;
    boolean isAlreadyDragging = false;
    public static CFontRenderer fontRenderer = new CFontRenderer(new Font(FontManager.font, Font.PLAIN, 15), true, true);

    @EventHandler
    private final EventListener<RenderGameOverlayEvent.Text> onRenderGameOverlay = new EventListener<>(e -> {

        EntityLivingBase Entity = Minecraft.getMinecraft().world.loadedEntityList.stream()
                .filter(entity -> IsValidEntity(entity))
                .map(entity -> (EntityLivingBase) entity)
                .min(Comparator.comparing(c -> Minecraft.getMinecraft().player.getDistance(c)))
                .orElse(null);

        if (Entity == null)
            return;
        float l_HealthPct = ((Entity.getHealth() + Entity.getAbsorptionAmount()) / Entity.getMaxHealth()) * 100.0f;
        float l_HealthBarPct = Math.min(l_HealthPct, 100.0f);
        final BlockPos pos = Entity.getPosition();
        BlockPos[] blockCheck = {pos.north(), pos.south(), pos.east(), pos.west()};
        int counter = 0;
        boolean bedrock = true;
        String hole = "None";
        for (BlockPos Pos : blockCheck) {
            Block l_Block = Minecraft.getMinecraft().world.getBlockState(Pos).getBlock();

            if (l_Block == Blocks.AIR)
                break;

            if (l_Block != Blocks.BEDROCK)
                bedrock = false;

            if (l_Block == Blocks.OBSIDIAN || l_Block == Blocks.BEDROCK)
                ++counter;
        }
        if (counter == 4) {
            if (bedrock)
                hole = "Bedrock";
            else
                hole = "Obsidian";
        }


        if (Entity instanceof EntityPlayer) {
            EntityPlayer l_Player = (EntityPlayer) Entity;

            int responseTime = -1;
            try {
                responseTime = (int) MathUtils.clamp(
                        Minecraft.getMinecraft().getConnection().getPlayerInfo(l_Player.getUniqueID()).getResponseTime(), 0,
                        300);
            } catch (NullPointerException np) {
            }

            //str = String.format("%s %s %s", entityIn.getName(), responseTime + "ms", ChatFormatting.GREEN + "" + l_Base.getHealth() + l_Base.getAbsorptionAmount());

        }

        DecimalFormat l_Format = new DecimalFormat("#.#");
        RenderUtils.drawRect(x, y, x + 120, y + 75, ColorUtils.color(0, 0, 0, 100));
        String inAir;
        if (Entity.fallDistance < 0) inAir = "In Air";
        else inAir = "On Ground";
        int color = ColorUtils.ColorSlider((l_HealthBarPct / 3) * 115, 1f);
        fontRenderer.drawStringWithShadow("Target: " + Entity.getName(), x + 20, y + 2, -1);
        fontRenderer.drawStringWithShadow("Distance: " + l_Format.format(Entity.getDistance(Minecraft.getMinecraft().player)), x + 20, y + 14, -1);
        fontRenderer.drawStringWithShadow("Health: " + l_Format.format(Entity.getHealth() + Entity.getAbsorptionAmount()) + " / " + l_Format.format(Entity.getMaxHealth() + Entity.getAbsorptionAmount()), x + 20, y + 26, -1);
        fontRenderer.drawStringWithShadow("Hole: " + hole, x + 20, y + 38, -1);
        RenderUtils.drawRect(x, y + 70, x + (l_HealthBarPct / 100) * 120, y + 75, color);


        ScaledResolution resolution = new ScaledResolution(Wrapper.INSTANCE.mc());
        RenderItem itemRender = Wrapper.INSTANCE.mc().getRenderItem();
        ItemStack mainHand = Entity.getHeldItemMainhand();
        ItemStack offHand = Entity.getHeldItemOffhand();
        int i = resolution.getScaledWidth() / 2;

        itemRender.zLevel = 0F;
        GlStateManager.enableTexture2D();
        int xPos = x;
        for (ItemStack is : Entity.getArmorInventoryList()) {


            if (is.isEmpty()) continue;

            GlStateManager.enableDepth();

            itemRender.zLevel = 200F;
            itemRender.renderItemAndEffectIntoGUI(is, xPos + 80, y + 46);
            itemRender.renderItemOverlayIntoGUI(Wrapper.INSTANCE.fontRenderer(), is, xPos + 80, y + 46, "");
            itemRender.zLevel = 0F;

            itemRender.zLevel = 200F;
            itemRender.renderItemAndEffectIntoGUI(mainHand, x + 16, y + 46);
            itemRender.renderItemOverlayIntoGUI(Wrapper.INSTANCE.fontRenderer(), mainHand, x + 16, y + 46, "");
            itemRender.zLevel = 0F;

            itemRender.zLevel = 200F;
            itemRender.renderItemAndEffectIntoGUI(offHand, x + 2, y + 46);
            itemRender.renderItemOverlayIntoGUI(Wrapper.INSTANCE.fontRenderer(), offHand, x + 2, y + 46, "");
            itemRender.zLevel = 0F;


            GlStateManager.enableTexture2D();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();

            String s = is.getCount() > 1 ? is.getCount() + "" : "";
            //Wrapper.INSTANCE.fontRenderer().drawStringWithShadow(s, x + 19 - 2 - Wrapper.INSTANCE.fontRenderer().getStringWidth(s), y + 9, 0xffffff);


            float green = ((float) is.getMaxDamage() - (float) is.getItemDamage()) / (float) is.getMaxDamage();
            float red = 1 - green;
            int dmg = 100 - (int) (red * 100);
            fontRenderer.drawStringWithShadow(dmg + "", xPos + 87 - (fontRenderer.getStringWidth(dmg + "") / 2), y + 60, -1);
            xPos -= 15;

            GlStateManager.enableDepth();
            GlStateManager.disableLighting();
        }

        if (Minecraft.getMinecraft().currentScreen instanceof HudGuiScreen) {


            if (MouseUtils.isLeftClicked() && !(MouseUtils.isMouseOver(x, x + 120, y, y + 75))) {
                isAlreadyDragging = true;
            }

            if (!MouseUtils.isLeftClicked() && isAlreadyDragging) {
                isAlreadyDragging = false;
            }

            if (!isAlreadyDragging || isDragging) {
                if (MouseUtils.isMouseOver(x, x + 120, y, y + 75)) {
                    isDragging = true;
                }


                if (MouseUtils.isLeftClicked() && isDragging) {
                    finalMouseX = MouseUtils.getMouseX();
                    finalMouseY = MouseUtils.getMouseY();

                    xOffset.setValue((double)finalMouseX - 60);
                    yOffset.setValue((double)finalMouseY);
                    MouseUtils.isDragging = true;
                } else isDragging = false;

            }
        }


        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);

        GlStateManager.color(1, 1, 1);
        if (Entity instanceof EntityPlayer || Entity instanceof EntityEnderman || Entity instanceof EntitySpider || Entity instanceof EntityChicken) {
            if (Entity instanceof EntityPlayer)
                GuiInventory.drawEntityOnScreen(x + 10, y + 35, 15, 0, 10, Entity);
            if (Entity instanceof EntityEnderman)
                GuiInventory.drawEntityOnScreen(x + 10, y + 40, 15, 0, 10, Entity);
            if (Entity instanceof EntitySpider)
                GuiInventory.drawEntityOnScreen(x + 10, y + 30, 15, 0, 10, Entity);
            if (Entity instanceof EntityChicken)
                GuiInventory.drawEntityOnScreen(x + 10, y + 30, 15, 0, 10, Entity);
        } else {
            GuiInventory.drawEntityOnScreen(x + 10, y + 35, 15, 0, 10, Entity);
        }

        GlStateManager.enableTexture2D();
        GlStateManager.enableBlend();

        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);



    });

    private boolean IsValidEntity(Entity p_Entity) {
        if (!(p_Entity instanceof EntityLivingBase))
            return false;

        if (p_Entity instanceof EntityPlayer) {
            if (p_Entity == Minecraft.getMinecraft().player)
                return false;

            if (!players.getValue())
                return false;

            if (!friends.getValue() && FriendManager.friendsList.contains(p_Entity.getName()))
                return false;
        }

        if (!ambient.getValue() && p_Entity instanceof EntityAmbientCreature) return false;

        if (!squid.getValue() && p_Entity instanceof EntitySquid) return false;

        if (!monsters.getValue() && isHostileMob(p_Entity) && !monsters.getValue() || (p_Entity instanceof EntityPigZombie))
            return false;

        return !(p_Entity instanceof EntityAnimal) || animals.getValue();
    }


}
