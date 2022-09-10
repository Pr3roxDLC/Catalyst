package com.krazzzzymonkey.catalyst.utils;

import java.lang.reflect.Field;

import com.krazzzzymonkey.catalyst.utils.system.Mapping;
import com.krazzzzymonkey.catalyst.utils.system.Wrapper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockObsidian;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.GameType;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class PlayerControllerUtils {
	
	public static void setReach(Entity entity, double range) {
		class RangePlayerController extends PlayerControllerMP {
			private float range = (float) Wrapper.INSTANCE.player().getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue();
			public RangePlayerController(Minecraft mcIn, NetHandlerPlayClient netHandler) {
				super(mcIn, netHandler);
			}
			@Override
			public float getBlockReachDistance() {
				return range;
			}
			public void setBlockReachDistance(float range) {
				this.range = range;
			}
		}
		Minecraft mc = Wrapper.INSTANCE.mc();
		EntityPlayer player = Wrapper.INSTANCE.player();
		if (player == entity){
			if (!(mc.playerController instanceof RangePlayerController)){
				GameType gameType = ReflectionHelper.getPrivateValue(PlayerControllerMP.class, mc.playerController, Mapping.currentGameType);
	            NetHandlerPlayClient netClient = ReflectionHelper.getPrivateValue(PlayerControllerMP.class, mc.playerController, Mapping.connection);
	            RangePlayerController controller = new RangePlayerController(mc, netClient);
	            boolean isFlying = player.capabilities.isFlying;
	            boolean allowFlying = player.capabilities.allowFlying;
	            controller.setGameType(gameType);
	            player.capabilities.isFlying = isFlying;
	            player.capabilities.allowFlying = allowFlying;
	            mc.playerController = controller;
			}
			((RangePlayerController) mc.playerController).setBlockReachDistance((float) range);
		}
	}
	
	public static void setIsHittingBlock(boolean isHittingBlock) {
    	try {
    		Field field = PlayerControllerMP.class.getDeclaredField(Mapping.isHittingBlock);
    		field.setAccessible(true);
    		field.setBoolean(Wrapper.INSTANCE.mc().playerController, isHittingBlock);
    	} catch (Exception ex) {}
	}
	
	public static void setBlockHitDelay(final int blockHitDelay) {
    	try {
    		Field field = PlayerControllerMP.class.getDeclaredField(Mapping.blockHitDelay);
        	field.setAccessible(true);
        	field.setInt(Wrapper.INSTANCE.mc().playerController, blockHitDelay);
    	} catch (Exception ex) {}
    }
	
	public static float getCurBlockDamageMP() {
		float getCurBlockDamageMP = 0;
		try {
			Field field = PlayerControllerMP.class.getDeclaredField(Mapping.curBlockDamageMP);
			field.setAccessible(true);
			getCurBlockDamageMP =  field.getFloat(Wrapper.INSTANCE.mc().playerController);	
		} catch (Exception ex) {}
		return getCurBlockDamageMP;
	}

	public static int findObiInHotbar() {
		for (int i = 0; i < 9; ++i) {
			final ItemStack stack = Minecraft.getMinecraft().player.inventory.getStackInSlot(i);
			if (stack != ItemStack.EMPTY && stack.getItem() instanceof ItemBlock) {
				final Block block = ((ItemBlock) stack.getItem()).getBlock();
				if (block instanceof BlockEnderChest)
					return i;
				else if (block instanceof BlockObsidian)
					return i;
			}
		}
		return -1;
	}
	
	
}
