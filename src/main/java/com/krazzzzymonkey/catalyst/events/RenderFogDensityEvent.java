package com.krazzzzymonkey.catalyst.events;

import dev.tigr.simpleevents.event.Event;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.Entity;

public class RenderFogDensityEvent extends ClientEvent {
    private final EntityRenderer renderer;
    private final Entity entity;
    private final IBlockState state;
    private final double renderPartialTicks;

    public RenderFogDensityEvent(EntityRenderer renderer, Entity entity, IBlockState state, double renderPartialTicks) {
        this.renderer = renderer;
        this.entity = entity;
        this.state = state;
        this.renderPartialTicks = renderPartialTicks;
        setName("RenderFogDensityEvent");
    }

    public Entity getEntity() {
        return entity;
    }

    public IBlockState getState() {
        return state;
    }

    public EntityRenderer getRenderer() {
        return renderer;
    }

    public double getRenderPartialTicks() {
        return renderPartialTicks;
    }
}
