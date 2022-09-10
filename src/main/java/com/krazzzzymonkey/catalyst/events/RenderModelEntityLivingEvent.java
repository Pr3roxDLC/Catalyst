package com.krazzzzymonkey.catalyst.events;

import dev.tigr.simpleevents.event.Event;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.EntityLivingBase;


public class RenderModelEntityLivingEvent extends ClientEvent {
    private final EntityLivingBase entityLivingBase;
    private ModelBase modelBase;

    private float limbSwing;
    private float limbSwingAmount;
    private float ageInTicks;
    private float netHeadYaw;
    private float headPitch;
    private float scaleFactor;

    public RenderModelEntityLivingEvent(EntityLivingBase entityLivingBase, ModelBase modelBase, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {
        this.entityLivingBase = entityLivingBase;
        this.modelBase = modelBase;
        this.limbSwing = limbSwing;
        this.limbSwingAmount = limbSwingAmount;
        this.ageInTicks = ageInTicks;
        this.netHeadYaw = netHeadYaw;
        this.headPitch = headPitch;
        this.scaleFactor = scaleFactor;
        setName("RenderModelEntityLivingEvent");
    }

    public EntityLivingBase getEntityLivingBase() {
        return entityLivingBase;
    }

    public ModelBase getModelBase() {
        return modelBase;
    }

    public void setModelBase(ModelBase modelBase) {
        this.modelBase = modelBase;
    }

    public void setLimbSwing(float limbSwing) {
        this.limbSwing = limbSwing;
    }

    public float getLimbSwing() {
        return limbSwing;
    }

    public void setLimbSwingAmount(float limbSwingAmount) {
        this.limbSwingAmount = limbSwingAmount;
    }

    public float getLimbSwingAmount() {
        return limbSwingAmount;
    }

    public void setAgeInTicks(float ageInTicks) {
        this.ageInTicks = ageInTicks;
    }

    public float getAgeInTicks() {
        return ageInTicks;
    }

    public void setNetHeadYaw(float netHeadYaw) {
        this.netHeadYaw = netHeadYaw;
    }

    public float getNetHeadYaw() {
        return netHeadYaw;
    }

    public void setHeadPitch(float headPitch) {
        this.headPitch = headPitch;
    }

    public float getHeadPitch() {
        return headPitch;
    }

    public void setScaleFactor(float scaleFactor) {
        this.scaleFactor = scaleFactor;
    }

    public float getScaleFactor() {
        return scaleFactor;
    }
}
