package com.krazzzzymonkey.catalyst.module.modules.misc;

import com.krazzzzymonkey.catalyst.events.ClientTickEvent;
import com.krazzzzymonkey.catalyst.events.PacketEvent;
import com.krazzzzymonkey.catalyst.managers.ModuleManager;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.module.modules.combat.Criticals;
import com.krazzzzymonkey.catalyst.utils.CrystalUtils;
import com.krazzzzymonkey.catalyst.utils.Timer;
import com.krazzzzymonkey.catalyst.value.Mode;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import com.krazzzzymonkey.catalyst.value.types.ModeValue;
import com.mojang.authlib.GameProfile;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.CombatRules;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;


public class FakePlayer extends Modules {

    public ModeValue armorMode = new ModeValue("ArmorMode", new Mode("Player", true), new Mode("Diamond", false), new Mode("None", false));
    public BooleanValue totems = new BooleanValue("Totems", true, "Gives the Fake player an infinite amount of Totems");
    public BooleanValue shouldDie = new BooleanValue("ShouldDie", false, "Allows the fakeplayer to die if totems are disabled");
    public static EntityOtherPlayerMP clonedPlayer = null;

    public static double x, y, z, timeSinceLastHit = 0;

    public FakePlayer() {
        super("FakePlayer", ModuleCategory.MISC, "Spawns in a Fake player (Client Side)");
        this.addValue(armorMode, totems, shouldDie);
    }

    final private ItemStack[] armor = new ItemStack[]{
        new ItemStack(Items.DIAMOND_BOOTS),
        new ItemStack(Items.DIAMOND_LEGGINGS),
        new ItemStack(Items.DIAMOND_CHESTPLATE),
        new ItemStack(Items.DIAMOND_HELMET)
    };

    @Override
    public void onEnable() {
        super.onEnable();
        if (mc.world == null) {
            this.toggle();
            return;
        }
        clonedPlayer = new EntityOtherPlayerMP(mc.world, new GameProfile(UUID.fromString("3a54cd18-783e-4b6c-9f5f-70c23fd9dca9"), "CatalystClient"));
        File file = new File(System.getProperty("user.home") + File.separator + "Catalyst" + File.separator + "assets" + File.separator + "fakeplayer" + File.separator + "skin.png");

        {
            try {
                DefaultPlayerSkin.TEXTURE_ALEX = Minecraft.getMinecraft().getRenderManager().renderEngine.getDynamicTextureLocation(file.getName(), new DynamicTexture(ImageIO.read(file)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        clonedPlayer.copyLocationAndAnglesFrom(mc.player);
        clonedPlayer.rotationYawHead = mc.player.rotationYawHead;
        clonedPlayer.rotationYaw = mc.player.rotationYaw;
        clonedPlayer.rotationPitch = mc.player.rotationPitch;
        clonedPlayer.setGameType(GameType.SURVIVAL);
        clonedPlayer.setHealth(20);
        mc.world.addEntityToWorld(-9999, clonedPlayer);
        if (armorMode.getMode("Player").isToggled()) {
            clonedPlayer.inventory = mc.player.inventory;
        } else if (armorMode.getMode("Diamond").isToggled()) {

            for (int i = 0; i < 4; i++) {
                ItemStack item = armor[i];
                item.addEnchantment(Enchantments.PROTECTION, 4);
                clonedPlayer.inventory.armorInventory.set(i, item);
            }
        }
        clonedPlayer.onLivingUpdate();
    }

    @EventHandler
    private final EventListener<PacketEvent> onPacket = new EventListener<>(e -> {
        if(e.getSide() == PacketEvent.Side.OUT) {

            if (e.getPacket() instanceof CPacketUseEntity) {
                CPacketUseEntity cPacketUseEntity = (CPacketUseEntity) e.getPacket();
                if (cPacketUseEntity.getAction() == CPacketUseEntity.Action.ATTACK) {
                    if (cPacketUseEntity.entityId == -9999) {

                        if (clonedPlayer.hurtResistantTime > 0) return;

                        clonedPlayer.performHurtAnimation();
                        double attackSpeed = 1;
                        double damageAmount = 1;
                        long lastHitTime = mc.player.lastAttackedEntityTime;
                        for (AttributeModifier modifier : mc.player.getHeldItemMainhand().getAttributeModifiers(EntityEquipmentSlot.MAINHAND).get(SharedMonsterAttributes.ATTACK_SPEED.getName())) {
                            attackSpeed = modifier.getAmount();
                        }
                        for (AttributeModifier modifier : mc.player.getHeldItemMainhand().getAttributeModifiers(EntityEquipmentSlot.MAINHAND).get(SharedMonsterAttributes.ATTACK_DAMAGE.getName())) {
                            damageAmount = modifier.getAmount();
                        }


//                    double attackMultiplier = 20/attackSpeed;
//
//                    double actualDamageAmount = MathUtils.clamp((float) (0.2 + Math.pow(((timeSinceLastHit + 0.5) * attackMultiplier),2) * 0.8), 0.2f, 1) * damageAmount;
//                    damageEntity((float) actualDamageAmount);
//
//                    ChatUtils.normalMessage(Math.pow(((timeSinceLastHit + 0.5) * attackMultiplier),2) + "");
//                    ChatUtils.normalMessage((0.2 + Math.pow(((timeSinceLastHit + 0.5) * attackMultiplier),2) * 0.8)+"");
//                    ChatUtils.normalMessage(MathUtils.clamp((float) (0.2 + Math.pow(((timeSinceLastHit + 0.5) * attackMultiplier),2) * 0.8), 0.2f, 1) + "");
//
//                    //ChatUtils.normalMessage("Attacking FakePlayer with:" + damageAmount + " DamageAmmount, " + attackSpeed + " Attack Speed. Last hit was at: " + lastHitTime + " time passed since is: " + timeSinceLastHit + " resulting in a hit with actualDamage: " + actualDamageAmount);
//                    timeSinceLastHit = 0;

//                      ChatUtils.normalMessage("Dealing " + (damageAmount * mc.player.getCooledAttackStrength(0.5f) + " Damage" + damageAmount  + " " + mc.player.getCooledAttackStrength(0.5f) ));
                        damageEntity((float) (damageAmount * mc.player.getCooledAttackStrength(0.5f)));
                        clonedPlayer.hurtResistantTime = clonedPlayer.maxHurtResistantTime;
                    }
                }
            }
        }
    });


    //Crystal Damage
    @EventHandler
    private final EventListener<PacketEvent> onPacketReceive = new EventListener<>(event -> {
        if(event.getSide() == PacketEvent.Side.IN) {

            if (clonedPlayer == null) return;
            if (event.getPacket() instanceof SPacketExplosion) {
                SPacketExplosion sPacketExplosion = (SPacketExplosion) event.getPacket();
                float damage = CrystalUtils.calculateDamage(new BlockPos(sPacketExplosion.posX, sPacketExplosion.posY, sPacketExplosion.posZ), clonedPlayer, false);
                damageEntity(damage);
            }
        }
    });

    @EventHandler
    private final EventListener<ClientTickEvent> onClientTick = new EventListener<>(e -> {
        if (mc.world == null) {
            this.setToggled(false);
            return;
        }

        if (clonedPlayer.isDead) {
            x = clonedPlayer.posX;
            y = clonedPlayer.posY;
            z = clonedPlayer.posZ;
            onEnable();
            clonedPlayer.setPosition(x, y, z);
        }
        if (clonedPlayer.isPotionActive(MobEffects.REGENERATION)) {
            PotionEffect potionEffect = clonedPlayer.activePotionsMap.get(MobEffects.REGENERATION);
            if (potionEffect.getPotion().isReady(potionEffect.getDuration(), potionEffect.getAmplifier())) {
                potionEffect.performEffect(clonedPlayer);
            }

        }

        clonedPlayer.hurtResistantTime--;

    });

    @Override
    public void onDisable() {
        super.onDisable();
        if (mc.world != null) {
            mc.world.removeEntityFromWorld(-9999);
            DefaultPlayerSkin.TEXTURE_ALEX = new ResourceLocation("textures/entity/alex.png");
        }
    }

    Timer popTimer = new Timer().reset();


    protected void damageEntity(float damageAmount) {

        ItemStack hitItem = mc.player.getHeldItemMainhand();

        if (EnchantmentHelper.getEnchantments(hitItem).containsKey(Enchantments.SHARPNESS)) {
            float extraDamage = 0.5f * EnchantmentHelper.getEnchantments(hitItem).get(Enchantments.SHARPNESS) + 0.5f;
            damageAmount += extraDamage;
        }


        if (armorMode.getMode("Player").isToggled()) {
            damageAmount = CombatRules.getDamageAfterAbsorb(damageAmount, mc.player.getTotalArmorValue(), (float) mc.player.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());
        } else if (armorMode.getMode("None").isToggled()) {
            damageAmount = CombatRules.getDamageAfterAbsorb(damageAmount, 0, (float) mc.player.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());
        } else if (armorMode.getMode("Diamond").isToggled()) {
            damageAmount = CombatRules.getDamageAfterAbsorb(damageAmount, 20, (float) mc.player.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());
        }

        boolean IS_CRIT_HIT = (mc.player.getCooledAttackStrength(0.5f) > 0.9f) && mc.player.fallDistance > 0.0F && !mc.player.onGround && !mc.player.isOnLadder() && !mc.player.isInWater() && !mc.player.isPotionActive(MobEffects.BLINDNESS) && !mc.player.isRiding();

        if (ModuleManager.getModule("Criticals").isToggled() && ((Criticals) ModuleManager.getModule("Criticals")).mode.getMode("Packet").isToggled()) {
            IS_CRIT_HIT = true;
        }

        if (IS_CRIT_HIT) {
            damageAmount *= 1.5f;
        }


        if (damageAmount != 0.0F) {
            if (damageAmount > clonedPlayer.getHealth() && totems.getValue()) {
                //Totem Stuff
                if (popTimer.passedMs(500)) {
                    mc.effectRenderer.emitParticleAtEntity(clonedPlayer, EnumParticleTypes.TOTEM, 30);
                    mc.world.playSound(clonedPlayer.posX, clonedPlayer.posY, clonedPlayer.posZ, SoundEvents.ITEM_TOTEM_USE, clonedPlayer.getSoundCategory(), 1.0F, 1.0F, false);
                    clonedPlayer.setHealth(2);
                    clonedPlayer.addPotionEffect(new PotionEffect(Objects.requireNonNull(Potion.getPotionById(10)), 45, 2));

                    popTimer.reset();
                }
            } else {
                if(shouldDie.getValue()){
                    float f1 = clonedPlayer.getHealth();
                    clonedPlayer.getCombatTracker().trackDamage(DamageSource.GENERIC, f1, damageAmount);
                    clonedPlayer.setHealth(f1 - damageAmount); // Forge: moved to fix MC-121048
                    clonedPlayer.setAbsorptionAmount(clonedPlayer.getAbsorptionAmount() - damageAmount);
                }else {
                    clonedPlayer.setHealth(2);
                    clonedPlayer.addPotionEffect(new PotionEffect(Objects.requireNonNull(Potion.getPotionById(10)), 45, 2));
                }

            }
        }

    }

}
