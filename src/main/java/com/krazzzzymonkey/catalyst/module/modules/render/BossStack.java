package com.krazzzzymonkey.catalyst.module.modules.render;

import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.utils.PairUtil;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.client.gui.BossInfoClient;
import net.minecraft.client.gui.GuiBossOverlay;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BossStack extends Modules {
    public BossStack() {
        super("BossStack", ModuleCategory.RENDER, "Stacks boss bars like withers");
    }

    @EventHandler
    private final EventListener<com.krazzzzymonkey.catalyst.events.RenderGameOverlayEvent.Pre> onPreRenderGameOverlay = new EventListener<>(e -> {
        if (e.getType() == RenderGameOverlayEvent.ElementType.BOSSINFO)
            e.setCancelled(true);
    });

    @EventHandler
    private final EventListener<com.krazzzzymonkey.catalyst.events.RenderGameOverlayEvent.Post> onPostRenderGameOverlay = new EventListener<>(e -> {
        Map<UUID, BossInfoClient> map = (mc.ingameGUI.getBossOverlay()).mapBossInfos;
        HashMap<String, PairUtil<BossInfoClient, Integer>> to = new HashMap<>();
        for (Map.Entry<UUID, BossInfoClient> entry : map.entrySet()) {
            String s = entry.getValue().getName().getFormattedText();
            if (to.containsKey(s)) {
                PairUtil<BossInfoClient, Integer> pair = to.get(s);
                pair = new PairUtil<>(pair.getKey(), Integer.valueOf(pair.getValue().intValue() + 1));
                to.put(s, pair);
                continue;
            }
            PairUtil<BossInfoClient, Integer> p = new PairUtil<>(entry.getValue(), Integer.valueOf(1));
            to.put(s, p);
        }
        ScaledResolution scaledresolution = new ScaledResolution(mc);
        int i = scaledresolution.getScaledWidth();
        int j = 12;
        for (Map.Entry<String, PairUtil<BossInfoClient, Integer>> entry : to.entrySet()) {
            String text = entry.getKey();
            BossInfoClient info = (entry.getValue()).getKey();
            int a = entry.getValue().getValue();
            text = text + " (" + a + "x)";
            int k = (int) (i / /*this.scale.getValue().floatValue()*/ 1 / 2.0F - 91.0F);
            GL11.glScaled(/*this.scale.getValue().floatValue()*/1, /*this.scale.getValue().floatValue()*/1, 1.0D);
            if (!e.isCancelled()) {
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                mc.getTextureManager().bindTexture(GuiBossOverlay.GUI_BARS_TEXTURES);
                mc.ingameGUI.getBossOverlay().render(k, j, info);
                mc.fontRenderer.drawStringWithShadow(text, i / /*this.scale.getValue().floatValue()*/1 / 2.0F - (mc.fontRenderer.getStringWidth(text) / 2), (j - 9), 16777215);
            }
            GL11.glScaled(1.0D / /*this.scale.getValue().floatValue()*/1, 1.0D / /*this.scale.getValue().floatValue()*/1, 1.0D);
            j += 10 + mc.fontRenderer.FONT_HEIGHT;
            mc.getTextureManager().bindTexture(GuiBossOverlay.ICONS);
        }
    });
}

