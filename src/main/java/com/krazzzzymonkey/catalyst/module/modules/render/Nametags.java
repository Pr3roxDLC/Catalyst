package com.krazzzzymonkey.catalyst.module.modules.render;

import com.krazzzzymonkey.catalyst.events.RenderWorldLastEvent;
import com.krazzzzymonkey.catalyst.managers.FontManager;
import com.krazzzzymonkey.catalyst.managers.FriendManager;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.utils.ChatColor;
import com.krazzzzymonkey.catalyst.utils.EntityUtils;
import com.krazzzzymonkey.catalyst.utils.MathUtils;
import com.krazzzzymonkey.catalyst.utils.font.CFontRenderer;
import com.krazzzzymonkey.catalyst.value.sliders.DoubleValue;
import com.krazzzzymonkey.catalyst.value.sliders.IntegerValue;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemElytra;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.krazzzzymonkey.catalyst.utils.BlockUtils.getInterpolatedAmount;

public class Nametags extends Modules {

    private BooleanValue players;
    private BooleanValue animals;
    private BooleanValue mobs;

    private final DoubleValue scale;
    private IntegerValue range;

    private final BooleanValue armor;
    private final BooleanValue ping;
    private final BooleanValue health;
    private final BooleanValue durability;
    private final BooleanValue gamemode;
    private final BooleanValue simplifyEnchants;
    public static IntegerValue fontStyle= new IntegerValue("FontStyle", 0, 0 , 3, "Changes the font style of the name tags");

    //todo Make Fonts Customizable
    public Nametags() {
        super("Nametags", ModuleCategory.RENDER, "Renders more detailed nametags above entities");
        this.players = new BooleanValue("Players", true, "Renders name tags for players");
        this.animals = new BooleanValue("Animals", false, "Renders name tags for animals");
        this.mobs = new BooleanValue("Mobs", false, "Renders name tags for mobs");
        this.armor = new BooleanValue("Armor", true, "Renders armor of the entity");
        this.simplifyEnchants = new BooleanValue("SimplifyEnchants", true, "Doesn't render enchantments if it is maxed out");
        this.durability = new BooleanValue("Durability", true, "Renders durability for armor and items in the entities hand");
        this.ping = new BooleanValue("Ping", true, "Renders the ping of the player");
        this.health = new BooleanValue("Health", true, "Renders the the health of the entity");
        this.gamemode = new BooleanValue("Gamemode", true, "Renders the gamemode of the player");
        this.scale = new DoubleValue("Scale", 1d, 0.5, 10f, "Changes the scale of the name tag");
        this.range = new IntegerValue("Range", 200, 10, 500, "The max range of the entity to render the name tags");
        this.addValue(players, animals, mobs, armor, simplifyEnchants, durability, ping, health, gamemode, scale, range, fontStyle);
    }

    int fontStyleCache = fontStyle.getValue();

    public static CFontRenderer fontRendererIn = new CFontRenderer(new Font(FontManager.font, fontStyle.getValue(), 20), true, true);
    public static CFontRenderer fontRendererSmall = new CFontRenderer(new Font(FontManager.font, Font.PLAIN, 20), true, false);

    @EventHandler
    private final EventListener<RenderWorldLastEvent> onRenderWorldLast = new EventListener<>(e -> {

        if(fontStyleCache != fontStyle.getValue()){
            fontRendererIn = new CFontRenderer(new Font(FontManager.font, fontStyle.getValue(), 20), true, true);
            fontStyleCache = fontStyle.getValue();
        }

        if (Minecraft.getMinecraft().world == null || Minecraft.getMinecraft().player == null || Minecraft.getMinecraft().player.getUniqueID() == null || !this.isToggled())
            return;
        try {
            GlStateManager.disableDepth();
            Minecraft.getMinecraft().world.loadedEntityList.stream().filter(EntityUtils::isLiving).filter(entity -> !EntityUtils.isFakeLocalPlayer(entity)).filter(entity -> (entity instanceof EntityPlayer) ? (this.players.getValue() && Nametags.mc.player != entity) : (EntityUtils.isPassive(entity) ? this.animals.getValue() : ((boolean) this.mobs.getValue()))).filter(entity -> Nametags.mc.player.getDistance(entity) < this.range.getValue()).sorted(Comparator.comparing(entity -> -Nametags.mc.player.getDistance(entity))).forEach(this::drawNametag);
            GlStateManager.enableDepth();
        } catch (Exception ignored) {
        }
    });


    private void drawNametag(final Entity entityIn) {
        GlStateManager.pushMatrix();
        final Vec3d interp = getInterpolatedRenderPos(entityIn, Nametags.mc.getRenderPartialTicks());
        final float yAdd = entityIn.height + 0.5f - (entityIn.isSneaking() ? 0.25f : 0.0f);
        final double x = interp.x;
        final double y = interp.y + yAdd;
        final double z = interp.z;
        final float viewerYaw = Nametags.mc.getRenderManager().playerViewY;
        final float viewerPitch = Nametags.mc.getRenderManager().playerViewX;
        final boolean isThirdPersonFrontal = Nametags.mc.getRenderManager().options.thirdPersonView == 2;
        GlStateManager.translate(x, y, z);
        GlStateManager.rotate(-viewerYaw, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate((isThirdPersonFrontal ? -1 : 1) * viewerPitch, 1.0f, 0.0f, 0.0f);
        float f = Nametags.mc.player.getDistance(entityIn);
        if (f <= 5) f = 5;
        final float m = f / 8.0f * (float) Math.pow(1.258925437927246, this.scale.getValue());
        GlStateManager.scale(m, m, m);

        String responseTime = "";
        if (entityIn instanceof EntityPlayer) {
            try {
                int ping = (int) MathUtils.clamp(
                    mc.getConnection().getPlayerInfo(entityIn.getUniqueID()).getResponseTime(), 0,
                    1000);


                if (ping < 100) {
                    responseTime = ChatColor.GREEN + String.valueOf(ping) + "ms";
                } else if (ping < 200) {
                    responseTime = ChatColor.YELLOW + String.valueOf(ping) + "ms";
                } else if (ping < 300) {
                    responseTime = ChatColor.GOLD + String.valueOf(ping) + "ms";
                } else {
                    responseTime = ChatColor.RED + String.valueOf(ping) + "ms";
                }

            } catch (NullPointerException ignored) {
            }
        }


        int health = Math.round(((EntityLivingBase) entityIn).getHealth() + ((entityIn instanceof EntityPlayer) ? ((EntityPlayer) entityIn).getAbsorptionAmount() : 0.0f));
        String playerHealth = "";
        if (health <= ((EntityLivingBase) entityIn).getMaxHealth() * 0.25D) {
            playerHealth = "\u00a74";
        } else if (health <= (((EntityLivingBase) entityIn).getMaxHealth() + ((EntityLivingBase) entityIn).getAbsorptionAmount()) * 0.5D) {
            playerHealth = "\u00a76";
        } else if (health <= (((EntityLivingBase) entityIn).getMaxHealth() + ((EntityLivingBase) entityIn).getAbsorptionAmount()) * 0.75D) {
            playerHealth = "\u00a7e";
        } else playerHealth = "\u00a72";


        playerHealth = playerHealth + +health;


        GlStateManager.scale(-0.025f, -0.025f, 0.025f);
        String str = "";
        if (entityIn instanceof EntityPlayer && !isFakePlayer((EntityPlayer) entityIn)) {
            try {
                str = (this.gamemode.getValue() ? ChatColor.DARK_GRAY + "[" + getShortGameType(mc.player.connection.getPlayerInfo(((EntityPlayer) entityIn).getGameProfile().getId()).getGameType().getName()) + ChatColor.DARK_GRAY + "] " : "") + ChatColor.RESET + entityIn.getName() + (this.ping.getValue() && !responseTime.equals("") ? " " + responseTime : "") + (this.health.getValue() ? (" " + playerHealth) : "");
            } catch (Exception e) {
                str = entityIn.getName() + (this.ping.getValue() && !responseTime.equals("") ? " " + responseTime : "") + (this.health.getValue() ? (" " + playerHealth) : "");
            }
        } else {
            str = entityIn.getName() + (this.ping.getValue() && !responseTime.equals("") ? " " + responseTime : "") + (this.health.getValue() ? (" " + playerHealth) : "");

        }
        final int i = fontRendererIn.getStringWidth(str) / 2;
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.disableTexture2D();
        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder bufferbuilder = tessellator.getBuffer();
        GlStateManager.disableDepth();
        GL11.glTranslatef(0.0f, -20.0f, 0.0f);
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(-i - 2, 8.0, 0.0).color(0.0f, 0.0f, 0.0f, 0.5f).endVertex();
        bufferbuilder.pos(-i - 2, 19.0, 0.0).color(0.0f, 0.0f, 0.0f, 0.5f).endVertex();
        bufferbuilder.pos(i + 2, 19.0, 0.0).color(0.0f, 0.0f, 0.0f, 0.5f).endVertex();
        bufferbuilder.pos(i + 2, 8.0, 0.0).color(0.0f, 0.0f, 0.0f, 0.5f).endVertex();
        tessellator.draw();
        bufferbuilder.begin(2, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(-i - 1, 8.0, 0.0).color(0.1f, 0.1f, 0.1f, 0.1f).endVertex();
        bufferbuilder.pos(-i - 1, 19.0, 0.0).color(0.1f, 0.1f, 0.1f, 0.1f).endVertex();
        bufferbuilder.pos(i + 1, 19.0, 0.0).color(0.1f, 0.1f, 0.1f, 0.1f).endVertex();
        bufferbuilder.pos(i + 1, 8.0, 0.0).color(0.1f, 0.1f, 0.1f, 0.1f).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.glNormal3f(0.0f, 1.0f, 0.0f);
        if (!entityIn.isSneaking()) {
            fontRendererIn.drawString(str, -i, 9, (entityIn instanceof EntityPlayer) ? (FriendManager.friendsList.contains(entityIn.getName()) ? 49151 : 16777215) : 16777215);
        } else {
            fontRendererIn.drawString(str, -i, 9, 16755200);
        }
        if (entityIn instanceof EntityPlayer && this.armor.getValue()) {
            this.renderArmor((EntityPlayer) entityIn, 0, -(fontRendererIn.getHeight() + 1) - 5);
        }
        GlStateManager.glNormal3f(0.0f, 0.0f, 0.0f);
        GL11.glTranslatef(0.0f, 20.0f, 0.0f);
        GlStateManager.scale(-40.0f, -40.0f, 40.0f);
        GlStateManager.enableDepth();
        GlStateManager.popMatrix();
    }


    public void renderArmor(final EntityPlayer player, int x, final int y) {
        GlStateManager.enableTexture2D();
        GlStateManager.depthMask(true);
        GlStateManager.clear(GL11.GL_DEPTH_BUFFER_BIT);
        GlStateManager.enableDepth();
        GlStateManager.disableAlpha();
        final InventoryPlayer items = player.inventory;
        final ItemStack inHand = player.getHeldItemMainhand();
        final ItemStack boots = items.armorItemInSlot(0);
        final ItemStack leggings = items.armorItemInSlot(1);
        final ItemStack body = items.armorItemInSlot(2);
        final ItemStack helm = items.armorItemInSlot(3);
        final ItemStack offHand = player.getHeldItemOffhand();
        ItemStack[] stack = null;
        if (inHand != null && offHand != null) {
            stack = new ItemStack[]{inHand, helm, body, leggings, boots, offHand};
        } else if (inHand != null && offHand == null) {
            stack = new ItemStack[]{inHand, helm, body, leggings, boots};
        } else if (inHand == null && offHand != null) {
            stack = new ItemStack[]{helm, body, leggings, boots, offHand};
        } else {
            stack = new ItemStack[]{helm, body, leggings, boots};
        }
        final List<ItemStack> stacks = new ArrayList<ItemStack>();
        ItemStack[] array;
        for (int length = (array = stack).length, j = 0; j < length; ++j) {
            final ItemStack i = array[j];
            if (i != null && i.getItem() != null) {
                stacks.add(i);
            }
        }
        final int width = 16 * stacks.size() / 2;
        x -= width;
        GlStateManager.disableDepth();
        for (final ItemStack itemStack : stacks) {
            this.renderItem(itemStack, x, y);
            if (this.durability.getValue()) {
                renderItemDurability(itemStack, x, y);
            }
            x += 16;
        }
        GlStateManager.enableDepth();
    }


    public void renderItem(final ItemStack stack, final int x, int y) {
        final FontRenderer fontRenderer = Nametags.mc.fontRenderer;
        final RenderItem renderItem = Nametags.mc.getRenderItem();

        GlStateManager.pushMatrix();
        GlStateManager.depthMask(true);
        GlStateManager.clear(GL11.GL_ACCUM);

        RenderHelper.enableStandardItemLighting();
        mc.getRenderItem().zLevel = -150.0F;
        GlStateManager.disableAlpha();
        GlStateManager.enableDepth();
        GlStateManager.disableCull();

        mc.getRenderItem().renderItemAndEffectIntoGUI(stack, x, y);
        mc.getRenderItem().renderItemOverlays(mc.fontRenderer, stack, x, y);

        mc.getRenderItem().zLevel = 0.0F;
        RenderHelper.disableStandardItemLighting();

        GlStateManager.enableCull();
        GlStateManager.enableAlpha();

        GlStateManager.scale(0.5F, 0.5F, 0.5F);
        GlStateManager.disableDepth();
        GlStateManager.enableDepth();
        GlStateManager.scale(2F, 2F, 2F);
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.scale(0.75f, 0.75f, 0.75f);
        GlStateManager.scale(1.33f, 1.33f, 1.33f);
        EnchantEntry[] array;
        y = y - 6;
        if (simplifyEnchants.getValue()) {
            if (isMaxEnchants(stack)) {
                GlStateManager.translate((float) (x - 1), (float) (y + 2), 0.0f);
                GlStateManager.scale(0.42f, 0.42f, 0.42f);
                GlStateManager.disableDepth();
                GlStateManager.disableLighting();
                GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                fontRendererSmall.drawString(ChatColor.RED + "MAX", (float) (20 - fontRendererSmall.getStringWidth("MAX" + 6) / 2), 0.0f, -1, false);
                GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                GlStateManager.enableLighting();
                GlStateManager.enableDepth();
                GlStateManager.scale(2.42f, 2.42f, 2.42f);
                GlStateManager.translate((float) (-x + 1), (float) (-y), 0.0f);
                renderItem.zLevel = 0.0f;
                RenderHelper.disableStandardItemLighting();
                GlStateManager.enableAlpha();
                GlStateManager.disableBlend();
                GlStateManager.disableLighting();
                GlStateManager.popMatrix();
                return;
            }


        }
        for (int length = (array = enchants).length, i = 0; i < length; ++i) {
            final EnchantEntry enchant = array[i];
            final int level = EnchantmentHelper.getEnchantmentLevel(enchant.getEnchant(), stack);
            String levelDisplay = "" + level;
            if (level > 10) {
                levelDisplay = "10+";
            }

            if (level > 0) {
                final float scale2 = 0.32f;
                GlStateManager.translate((float) (x - 1), (float) (y + 2), 0.0f);
                GlStateManager.scale(0.42f, 0.42f, 0.42f);
                GlStateManager.disableDepth();
                GlStateManager.disableLighting();
                GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                fontRendererSmall.drawString("§f" + enchant.getName() + " " + levelDisplay, (float) (20 - fontRendererSmall.getStringWidth("§f" + enchant.getName() + " " + 6) / 2), 0.0f, -1, false);
                GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                GlStateManager.enableLighting();
                GlStateManager.enableDepth();
                GlStateManager.scale(2.42f, 2.42f, 2.42f);
                GlStateManager.translate((float) (-x + 1), (float) (-y), 0.0f);
                y += -(int) ((fontRendererSmall.getHeight() + 5) * 0.5f);
            }
        }
        renderItem.zLevel = 0.0f;
        RenderHelper.disableStandardItemLighting();
        GlStateManager.enableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.disableLighting();
        GlStateManager.popMatrix();
    }

    public void drawDamage(final ItemStack itemstack, final int x, final int y) {
        final float green = (itemstack.getMaxDamage() - (float) itemstack.getItemDamage()) / itemstack.getMaxDamage();
        final float red = 1.0f - green;
        final int dmg = 100 - (int) (red * 100.0f);
        GlStateManager.disableDepth();
        Nametags.mc.fontRenderer.drawStringWithShadow(dmg + "", x - Nametags.mc.fontRenderer.getStringWidth(dmg + "") / 2.0f, (float) (y + 50), new Color((int) (red * 255.0f), (int) (green * 255.0f), 0).getRGB());
        GlStateManager.enableDepth();
    }

    final EnchantEntry[] enchants = {new EnchantEntry(Enchantments.PROTECTION, "Pro"),
        new EnchantEntry(Enchantments.THORNS, "Thr"),
        new EnchantEntry(Enchantments.SHARPNESS, "Sha"),
        new EnchantEntry(Enchantments.FIRE_ASPECT, "Fia"),
        new EnchantEntry(Enchantments.KNOCKBACK, "Knb"),
        new EnchantEntry(Enchantments.UNBREAKING, "Unb"),
        new EnchantEntry(Enchantments.POWER, "Pow"),
        new EnchantEntry(Enchantments.FIRE_PROTECTION, "Fpr"),
        new EnchantEntry(Enchantments.FEATHER_FALLING, "Fea"),
        new EnchantEntry(Enchantments.BLAST_PROTECTION, "Bla"),
        new EnchantEntry(Enchantments.PROJECTILE_PROTECTION, "Ppr"),
        new EnchantEntry(Enchantments.RESPIRATION, "Res"),
        new EnchantEntry(Enchantments.AQUA_AFFINITY, "Aqu"),
        new EnchantEntry(Enchantments.DEPTH_STRIDER, "Dep"),
        new EnchantEntry(Enchantments.FROST_WALKER, "Fro"),
        new EnchantEntry(Enchantments.BINDING_CURSE, "Bin"),
        new EnchantEntry(Enchantments.SMITE, "Smi"),
        new EnchantEntry(Enchantments.BANE_OF_ARTHROPODS, "Ban"),
        new EnchantEntry(Enchantments.LOOTING, "Loo"),
        new EnchantEntry(Enchantments.SWEEPING, "Swe"),
        new EnchantEntry(Enchantments.EFFICIENCY, "Eff"),
        new EnchantEntry(Enchantments.SILK_TOUCH, "Sil"),
        new EnchantEntry(Enchantments.FORTUNE, "For"),
        new EnchantEntry(Enchantments.FLAME, "Fla"),
        new EnchantEntry(Enchantments.LUCK_OF_THE_SEA, "Luc"),
        new EnchantEntry(Enchantments.LURE, "Lur"),
        new EnchantEntry(Enchantments.MENDING, "Men"),
        new EnchantEntry(Enchantments.VANISHING_CURSE, "Van"),
        new EnchantEntry(Enchantments.PUNCH, "Pun")};


    public static Vec3d getInterpolatedPos(final Entity entity, final float ticks) {
        return new Vec3d(entity.lastTickPosX, entity.lastTickPosY, entity.lastTickPosZ).add(getInterpolatedAmount(entity, ticks));
    }

    public static Vec3d getInterpolatedRenderPos(final Entity entity, final float ticks) {
        return getInterpolatedPos(entity, ticks).subtract(Minecraft.getMinecraft().getRenderManager().renderPosX, Minecraft.getMinecraft().getRenderManager().renderPosY, Minecraft.getMinecraft().getRenderManager().renderPosZ);
    }

    public static class EnchantEntry {
        private final Enchantment enchant;
        private final String name;

        public EnchantEntry(final Enchantment enchant, final String name) {
            this.enchant = enchant;
            this.name = name;
        }

        public Enchantment getEnchant() {
            return this.enchant;
        }

        public String getName() {
            return this.name;
        }
    }

    public String getShortGameType(final String gameType) {
        if (gameType.equalsIgnoreCase("survival")) {
            return ChatColor.GRAY + "S";
        }
        if (gameType.equalsIgnoreCase("creative")) {
            return ChatColor.AQUA + "C";
        }
        if (gameType.equalsIgnoreCase("adventure")) {
            return ChatColor.GRAY + "A";
        }
        if (gameType.equalsIgnoreCase("spectator")) {
            return ChatColor.RED + "SP";
        }
        return "";
    }

    private void renderItemDurability(ItemStack itemStack, int posX, int posY) {
        if (itemStack.getMaxDamage() == 0) return;
        float damagePercent = (itemStack.getMaxDamage() - itemStack.getItemDamage()) / (float) itemStack.getMaxDamage();

        float green = damagePercent;
        if (green > 1) green = 1;
        else if (green < 0) green = 0;

        float red = 1 - green;

        damagePercent = damagePercent * 100;
        int x = posX * 2;
        if (damagePercent < 10) {
            x = posX * 2 + 12;
        } else if (damagePercent < 100) {
            x = posX * 2 + 8;
        } else if (damagePercent >= 100) {
            x = posX * 2 + 5;
        }
        GlStateManager.scale(0.5, 0.5, 0.5);
        fontRendererSmall.drawString((int) (damagePercent) + "%", x, posY + 20, new Color((int) (red * 255), (int) (green * 255), 0).getRGB());
        GlStateManager.scale(2, 2, 2);
    }

    public boolean isMaxEnchants(ItemStack stack) {
        if (stack.getItem() instanceof ItemElytra && stack.getEnchantmentTagList().tagCount() > 1) return true;
        return stack.getEnchantmentTagList().tagCount() > 2;
    }


    public boolean isFakePlayer(EntityPlayer entityPlayer) {
        return (entityPlayer.getEntityId() == -9999);
    }

}
