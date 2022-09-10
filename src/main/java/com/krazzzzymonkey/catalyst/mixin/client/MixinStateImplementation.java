package com.krazzzzymonkey.catalyst.mixin.client;


import com.krazzzzymonkey.catalyst.events.AddCollisionBoxToListEvent;
import com.krazzzzymonkey.catalyst.managers.ModuleManager;
import net.minecraft.block.Block;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.util.List;


@Mixin({BlockStateContainer.StateImplementation.class})
public class MixinStateImplementation {
    @Shadow
    @Final
    private Block block;
//todo remove the lag

/*    @Redirect(method = {"addCollisionBoxToList"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;addCollisionBoxToList(Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/AxisAlignedBB;Ljava/util/List;Lnet/minecraft/entity/Entity;Z)V"))
    public void addCollisionBoxToList(final Block b, final IBlockState state, final World worldIn, final BlockPos pos, final AxisAlignedBB entityBox, final List<AxisAlignedBB> collidingBoxes, @Nullable final Entity entityIn, final boolean isActualState) {
        try {
            Class[] params = {Block.class, IBlockState.class, World.class, BlockPos.class, AxisAlignedBB.class, List.class, Entity.class, boolean.class, Block.class};
            ModuleManager.getMixinProxyClass().getMethod("postAddCollisionBoxToListEvent", params).invoke(ModuleManager.getMixinProxyClass(), b, state, worldIn, pos, entityBox, collidingBoxes, entityIn, isActualState, block);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }

    }*/
}
