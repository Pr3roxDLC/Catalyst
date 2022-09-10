package com.krazzzzymonkey.catalyst.module.modules.other;

import com.krazzzzymonkey.catalyst.command.Command;
import com.krazzzzymonkey.catalyst.events.*;
import com.krazzzzymonkey.catalyst.gui.chest.CustomGuiChest;
import com.krazzzzymonkey.catalyst.managers.ChatMentionManager;
import com.krazzzzymonkey.catalyst.managers.CommandManager;
import com.krazzzzymonkey.catalyst.managers.FriendManager;
import com.krazzzzymonkey.catalyst.managers.LuaManager;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.module.modules.chat.ChatMention;
import com.krazzzzymonkey.catalyst.module.modules.chat.CustomChat;
import com.krazzzzymonkey.catalyst.module.modules.misc.NoEntityTrace;
import com.krazzzzymonkey.catalyst.module.modules.misc.Sounds;
import com.krazzzzymonkey.catalyst.module.modules.render.*;
import com.krazzzzymonkey.catalyst.module.modules.world.Scaffold;
import com.krazzzzymonkey.catalyst.utils.visual.ColorUtils;
import com.krazzzzymonkey.catalyst.utils.visual.RenderUtils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiTextField;
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
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.krazzzzymonkey.catalyst.managers.ModuleManager.EVENT_MANAGER;
import static com.krazzzzymonkey.catalyst.module.modules.render.CustomMainMenu.SHADER_DIR;

public class MixinProxy extends Modules {
    public MixinProxy() throws Exception {
        super("MixinProxy", ModuleCategory.MISC, "If you can see this, something went horribly wrong, this is not supposed to be seen by anyone");
        throw new InstantiationException("MixinProxy Class should not be instantiated");
   }

    // this is mixin shit

    public static String getCommandPrefix() {
        return CommandManager.prefix;
    }

    public static String getFormatting() {
        return ChatMention.formatting;
    }

    public static void drawStringWithShadow(String text, double x, double y, int color) {
        CustomChat.fontRenderer.drawStringWithShadow(text, x, y, color);
    }

    public static ArrayList<String> getFriendList() {
        return FriendManager.friendsList;
    }

    public static String getStringColor() {
        return TabFriends.color;
    }


    public static ArrayList<String> getMentionList() {
        return ChatMentionManager.mentionList;
    }


    public static ArrayList<Command> getCommands() {
        return CommandManager.getInstance().getCommands();
    }

    public static Color getRainbow() {
        return ColorUtils.rainbow();
    }

    public static void initFakeInventory(IInventory inventory) {
        Minecraft.getMinecraft().displayGuiScreen(new CustomGuiChest(Minecraft.getMinecraft().player.inventory, inventory));
    }

    static String predictedCommand;

    public static void guiChatInit(int width, int height, GuiTextField inputField) {
        CommandEvent.inputField = inputField.getText();
        if (inputField.getText().isEmpty()) {
            Minecraft.getMinecraft().fontRenderer.drawStringWithShadow("Use " + CommandManager.prefix + "help for Catalyst commands.", 4, height - 12, -1);
        }
        if (inputField.getText().startsWith(CommandManager.prefix)) {
            RenderUtils.drawBorderedRect(2, height - 14, width - 2, height - 2, 2, ColorUtils.rainbow().getRGB(), new Color(0, 0, 0, 0).getRGB());
            if (inputField.getText().length() > 1) {

                for (Command command : CommandManager.getInstance().getCommands()) {
                    if ((CommandManager.prefix + command.getCommand()).contains(inputField.getText())) {
                        Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(CommandManager.prefix + command.getSyntax(), 4, height - 12, -1);
                        predictedCommand = command.getCommand();
                        break;
                    }
                }

                if (Keyboard.isKeyDown(Keyboard.KEY_TAB) && !inputField.getText().contains(" ")) {
                    inputField.setText(CommandManager.prefix + predictedCommand);

                }
            }
        }
    }

    public static File getShaderDir(){
        return SHADER_DIR;
    }


    public static void postRenderItemEvent(EnumHandSide hand, float y) {
        RenderItemEvent event = new RenderItemEvent(0.56F, -0.52F + y * -0.6F, -0.72F, -0.56F, -0.52F + y * -0.6F, -0.72F,
            0.0, 0.0, 1.0, 0.0,
            0.0, 0.0, 1.0, 0.0,
            1.0, 1.0, 1.0,
            1.0, 1.0, 1.0
        );
        EVENT_MANAGER.post(event);
        if (hand == EnumHandSide.RIGHT) {
            GlStateManager.translate(event.getMainX(), event.getMainY(), event.getMainZ());
            GlStateManager.scale(event.getMainHandScaleX(), event.getMainHandScaleY(), event.getMainHandScaleZ());
            GlStateManager.rotate((float) event.getMainRAngel(), (float) event.getMainRx(), (float) event.getMainRy(), (float) event.getMainRz());
        } else {
            GlStateManager.translate(event.getOffX(), event.getOffY(), event.getOffZ());
            GlStateManager.scale(event.getOffHandScaleX(), event.getOffHandScaleY(), event.getOffHandScaleZ());
            GlStateManager.rotate((float) event.getOffRAngel(), (float) event.getOffRx(), (float) event.getOffRy(), (float) event.getOffRz());
        }
    }

    public static void postReachEvent(CallbackInfoReturnable<Float> cir) {
        final ReachEvent e = new ReachEvent(cir.getReturnValue());
        EVENT_MANAGER.post(e);
        cir.setReturnValue(e.distance);
    }

    public static void postKeyDownEvent(int i) {
        KeyDownEvent e = new KeyDownEvent(i);
        EVENT_MANAGER.post(e);
    }

    public static void postDamageBlockEvent(BlockPos blockPos, EnumFacing enumFacing, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        final DamageBlockEvent damageBlockEvent = new DamageBlockEvent(blockPos, enumFacing);
        EVENT_MANAGER.post(damageBlockEvent);
        if (damageBlockEvent.isCancelled()) {
            callbackInfoReturnable.setReturnValue(false);
        }
    }

    public static void postMotionEvent(String event) {

        if (event.equals("PRE")) {
            EVENT_MANAGER.post(new MotionEvent.PRE());
        } else if (event.equals("PREWALK")) {
            EVENT_MANAGER.post(new MotionEvent.PREWALK());
        } else if (event.equals("POST")) {
            EVENT_MANAGER.post(new MotionEvent.POST());
        }
    }

    public static void postStopUsingItemEvent(EntityPlayer playerIn, CallbackInfo ci) {

        StopUsingItemEvent event = new StopUsingItemEvent();
        EVENT_MANAGER.post(event);
        if (event.isCancelled()) {
            if (event.isPacket()) {
                mc.playerController.syncCurrentPlayItem();
                playerIn.stopActiveHand();
            }
            ci.cancel();
        }

    }


    public static void postAddCollisionBoxToListEvent(final Block block, final IBlockState state, final World worldIn, final BlockPos pos, final AxisAlignedBB entityBox, final List<AxisAlignedBB> collidingBoxes, final Entity entityIn, final boolean isActualState, Block b) {
        final AddCollisionBoxToListEvent event = new AddCollisionBoxToListEvent(block, state, worldIn, pos, entityBox, collidingBoxes, entityIn, isActualState);
        EVENT_MANAGER.post(event);
        if (!event.isCancelled()) {
            b.addCollisionBoxToList(state, worldIn, pos, entityBox, collidingBoxes, entityIn, isActualState);
        }
    }

    public static void postRenderModelEntityLivingEvent(ModelBase modelBase, Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        final RenderModelEntityLivingEvent event = new RenderModelEntityLivingEvent((EntityLivingBase) entityIn, modelBase, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        EVENT_MANAGER.post(event);
        if (event.isCancelled()) {
            return;
        }
        event.getModelBase().render(entityIn, event.getLimbSwing(), event.getLimbSwingAmount(), event.getAgeInTicks(), event.getNetHeadYaw(), event.getHeadPitch(), event.getScaleFactor());
    }


    public static boolean isInList(final Block block) {
        return XRay.blocks.contains(block);
    }

    public static boolean getCancelSneak() {
        return Scaffold.cancelSneak;
    }

    public static boolean shouldEnable() {
        return NoEntityTrace.shouldEnable;
    }

    public static void updateFields(int x, int y, int left, int top) {
        ShulkerPreview.mouseX = x;
        ShulkerPreview.mouseY = y;
        ShulkerPreview.guiLeft = left;
        ShulkerPreview.guiTop = top;
    }

    public static void updateFields(NBTTagCompound nbt, ItemStack itemStack, boolean active, boolean pinned) {
        ShulkerPreview.nbt = nbt;
        ShulkerPreview.itemStack = itemStack;
        ShulkerPreview.active = active;
        ShulkerPreview.pinned = pinned;
    }

    public static void updateFields(int x, int y) {
        ShulkerPreview.drawX = x;
        ShulkerPreview.drawY = y;
    }

    public static void project(float oldFovY, float oldAspectRatio, float oldZNear, float oldZFar, boolean fromHands) {
        AspectRatio.project(oldFovY, oldAspectRatio, oldZNear, oldZFar, fromHands);
    }

    public static void renderOne(final float lineWidth) {
        ESP.renderOne(lineWidth);
    }

    public static void renderTwo() {
        ESP.renderTwo();
    }

    public static void renderThree() {
        ESP.renderThree();
    }

    public static void renderFour(final Color color) {
        ESP.renderFour(color);
    }

    public static void renderFive() {
        ESP.renderFive();
    }

    public static void setColor(final Color color) {
        ESP.setColor(color);
    }

    public static void onExplosion() {
        Sounds.onExplosion();
    }

    public static void onPlayerUpdate() {
        EVENT_MANAGER.post(new PlayerUpdateEvent());
    }

    public static Object[] onPlayerMove(MoverType type, double x, double y, double z){
        PlayerMoveEvent event = new PlayerMoveEvent(type, x, y ,z);
        EVENT_MANAGER.post(event);
        return new Object[]{event.type, event.x, event.y, event.z};
    }

    public static String remapMethod(String clazz, String methodName){
        return LuaManager.isInDevEnv ? methodName : LuaManager.MAPPER.unmapMethod(clazz,methodName);
    }
    public static String remapField(String clazz, String fieldName){
        return LuaManager.isInDevEnv ? fieldName : LuaManager.MAPPER.unmapMethod(clazz,fieldName);
    }


}
