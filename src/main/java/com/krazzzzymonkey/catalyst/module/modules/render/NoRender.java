package com.krazzzzymonkey.catalyst.module.modules.render;

import com.google.common.collect.Sets;
import com.krazzzzymonkey.catalyst.events.ClientTickEvent;
import com.krazzzzymonkey.catalyst.events.PacketEvent;
import com.krazzzzymonkey.catalyst.events.RenderFogDensityEvent;
import com.krazzzzymonkey.catalyst.events.RenderGameOverlayEvent;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.utils.system.Wrapper;
import com.krazzzzymonkey.catalyst.value.sliders.DoubleValue;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import com.krazzzzymonkey.catalyst.value.types.SubMenu;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.server.*;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Set;

public class NoRender extends Modules {

    //No Particle
    public BooleanValue totems = new BooleanValue("Totems", false, "Prevents totem particles from being rendered");                                                                     // done
    public BooleanValue explosions = new BooleanValue("Explosions", false, "Prevents explosions from being rendered");                                                                  // done
    public BooleanValue criticals = new BooleanValue("Criticals", false, "Prevents criticals from being rendered");                                                                     // done
    public BooleanValue fireworks = new BooleanValue("Fireworks", false, "Prevents firewors from being rendered");                                                                      // done
    public BooleanValue allParticles = new BooleanValue("AllParticles", false, "Prevents all particles from being rendered");                                                           // done

    // No Armor
    public BooleanValue helmet = new BooleanValue("Helmet", false, "Prevents players helmets from being rendered");                                                                     // done
    public BooleanValue chestplate = new BooleanValue("Chestplate", false, "Prevents players chestplates from being rendered");                                                         // done
    public BooleanValue leggings = new BooleanValue("Leggings", false, "Prevents players leggings from being rendered");                                                                // done
    public BooleanValue boots = new BooleanValue("Boots", false, "Prevents players boots from being rendered");                                                                         // done

    // No Overlay
    public BooleanValue water = new BooleanValue("Water", false, "Prevents the water overlay from being rendered");                                                                     // done
    public BooleanValue lava = new BooleanValue("Lava", false, "Prevents the lava overlay from being rendered");                                                                        // done
    public BooleanValue blocks = new BooleanValue("InsideBlock", false, "Prevents the block overlay from being rendered when in a block");                                              // done
    public BooleanValue portal = new BooleanValue("Portal", false, "Prevents the portal effect when in a nether portal");                                                               // done
    public BooleanValue fire = new BooleanValue("Fire", false, "Prevents the fire overlay from being rendered");                                                                        // done

    // No Weather
    public BooleanValue noLightning = new BooleanValue("NoLightning", false, "Prevents lightning from being rendered");                                                                 // done
    public BooleanValue noWeather = new BooleanValue("NoWeather", false, "Prevents all weather from being rendered");                                                                   // done

    //No Cluster
    public static BooleanValue enabled = new BooleanValue("Enabled", false, "Renders Players close to you with decreasing opacity");                                                    // done
    public static DoubleValue startDistance = new DoubleValue("StartingDistance", 3, 1, 10, "At what distance should the opacity changes start");                              // done
    public static DoubleValue minOpacity = new DoubleValue("MinimumOpacity", 0, 0, 1, "The Minimum Opacity that will be used to render the players closest to you");           // done
    public static BooleanValue friendsOnly = new BooleanValue("FriendsOnly", false, "Only hide friends close to you");                                                                  // done

    // Tile Entities
    public BooleanValue banners = new BooleanValue("Banners", false, "Prevents banners from being rendered");                                                                           // done
    public BooleanValue beacons = new BooleanValue("Beacons", false, "Prevents beacons from being rendered");                                                                           // done
    public BooleanValue beds = new BooleanValue("Beds", false, "Prevents beds from being rendered");                                                                                    // done
    public BooleanValue chests = new BooleanValue("Chests", false, "Prevents chests from being rendered");                                                                              // done
    public BooleanValue enchantmentTables = new BooleanValue("EnchantTables", false, "Prevents enchantment tables from being rendered");                                                // done
    public BooleanValue pistons = new BooleanValue("Pistons", false, "Prevents pistons from being rendered");                                                                           // done
    public BooleanValue signs = new BooleanValue("Signs", false, "Prevents signs from being rendered");                                                                                 // done
    public BooleanValue enderChests = new BooleanValue("EnderChests", false, "Prevents ender chests from being rendered");                                                              // done
    public BooleanValue shulkerBoxes = new BooleanValue("ShulkerBoxes", false, "Prevents shulker boxes from being rendered");                                                           // done

    // Entities
    public BooleanValue noBats = new BooleanValue("NoBats", true, "Prevents bats from being rendered");                                                                                 // done
    public BooleanValue exp = new BooleanValue("Exp", true, "Prevents exp from being rendered");                                                                                        // done
    public BooleanValue mobs = new BooleanValue("Mobs", false, "Prevents mobs from being rendered");                                                                                    // done
    public BooleanValue items = new BooleanValue("Items", false, "Prevents items from being rendered");                                                                                 // done

    // Other shit
    public BooleanValue noHurtCam = new BooleanValue("NoHurtCam", true, "Prevents the player from flinching when taking damage");                                                       // done
    public BooleanValue noBossBar = new BooleanValue("NoBossbar", false, "Prevents boss bars from being rendered");                                                                     // done
   // public BooleanValue noFog = new BooleanValue("NoFog", false, "Prevents fog from being rendered");                                                                                                           //

    public NoRender() {
        super("NoRender", ModuleCategory.RENDER, "Prevents rendering specific things ingame");

        SubMenu noParticleSubmenu = new SubMenu("NoParticle", totems, explosions, criticals, fireworks, allParticles);
        SubMenu noArmorSubmenu = new SubMenu("NoArmor", helmet, chestplate, leggings, boots);
        SubMenu noOverlaySubmenu = new SubMenu("NoOverlay", water, lava, blocks, portal, fire);
        SubMenu noWeatherSubmenu = new SubMenu("NoWeather", noLightning, noWeather);
        SubMenu noClusterSubmenu = new SubMenu("NoCluster", enabled, startDistance, minOpacity, friendsOnly);
        SubMenu tileEntitiesSubmenu = new SubMenu("TileEntities", enchantmentTables, beacons, beds, signs, enderChests, chests,  banners, pistons, shulkerBoxes);
        SubMenu entitiesSubmenu = new SubMenu("Entities", noBats, exp, mobs, items);

        this.addValue(noParticleSubmenu, noArmorSubmenu, noOverlaySubmenu, noWeatherSubmenu, noClusterSubmenu, tileEntitiesSubmenu, entitiesSubmenu, noHurtCam, noBossBar/*, noFog*/);

    }

    // No Overlay todo refactor to new Event handler
    @SubscribeEvent
    public void onRenderBlockOverlay(RenderBlockOverlayEvent event) {
        if (fire.getValue() && event.getOverlayType() == RenderBlockOverlayEvent.OverlayType.FIRE)
            event.setCanceled(true);
        if (water.getValue() && event.getOverlayType() == RenderBlockOverlayEvent.OverlayType.WATER)
            event.setCanceled(true);
        if (blocks.getValue() && event.getOverlayType() == RenderBlockOverlayEvent.OverlayType.BLOCK)
            event.setCanceled(true);
    }

    // No Overlay
    @EventHandler
    private final EventListener<ClientTickEvent> onClientTick = new EventListener<>(e -> {
        if (Minecraft.getMinecraft().world == null || Minecraft.getMinecraft().player == null) return;
        if (portal.getValue()) Minecraft.getMinecraft().player.inPortal = false;
    });


    // NoBossbar && NoWeather
    @EventHandler
    private final EventListener<RenderGameOverlayEvent.Pre> onPreRenderGameOverlay = new EventListener<>(e -> {
        if (noBossBar.getValue()) {
            if (e.getType() == net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType.BOSSINFO)
                e.setCancelled(true);
        }
        if(noWeather.getValue()){
            Wrapper.INSTANCE.world().setRainStrength(0.0f);
            Wrapper.INSTANCE.world().setThunderStrength(0.0f);
        }
    });

    @EventHandler
    private final EventListener<RenderFogDensityEvent> onRenderFogDensity = new EventListener<>(e -> {
        if (lava.getValue() && e.getState().getMaterial().equals(Material.LAVA)) {
            e.setCancelled(true);
        }
        /*if(noFog.getValue()){
            e.setCancelled(true);
        }*/
    });

    // NoLightning
    @EventHandler
    private final EventListener<PacketEvent> onPacket = new EventListener<>(event -> {
        if (event.getPacket() instanceof SPacketSpawnGlobalEntity && noLightning.getValue()) {
            if (((SPacketSpawnGlobalEntity) event.getPacket()).getType() == 1) {
                event.setCancelled(true);
            }
        }
        if ((event.getPacket() instanceof SPacketSpawnMob && mobs.getValue()) ||
       //     (event.getPacket() instanceof SPacketSpawnObject && object.getValue()) ||
            (event.getPacket() instanceof SPacketSpawnExperienceOrb && exp.getValue()) ||
            (event.getPacket() instanceof SPacketExplosion && explosions.getValue()) ||
            (event.getPacket() instanceof SPacketSpawnObject && items.getValue() && ((SPacketSpawnObject) event.getPacket()).getType() == 2) ||
            (event.getPacket() instanceof SPacketSpawnObject && fireworks.getValue() && ((SPacketSpawnObject) event.getPacket()).getType() == 76))
            event.setCancelled(true);

        if (noBats.getValue() && (event.getPacket() instanceof SPacketSpawnMob && ((SPacketSpawnMob) event.getPacket()).getEntityType() == 65 ||
            event.getPacket() instanceof SPacketSoundEffect && BAT_SOUNDS.contains(((SPacketSoundEffect) event.getPacket()).getSound()))) {
            event.setCancelled(true);
        }

    });


    private static final Set<SoundEvent> BAT_SOUNDS = Sets.newHashSet(
        SoundEvents.ENTITY_BAT_AMBIENT,
        SoundEvents.ENTITY_BAT_DEATH,
        SoundEvents.ENTITY_BAT_HURT,
        SoundEvents.ENTITY_BAT_LOOP,
        SoundEvents.ENTITY_BAT_TAKEOFF
    );

}
