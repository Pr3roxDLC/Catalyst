package com.krazzzzymonkey.catalyst.utils.system;

import net.minecraft.client.Minecraft;

public class Mapping {

	public static String getPlayerInfo = isNotObfuscated() ? "getPlayerInfo" : "func_175155_b";
	public static String session = isNotObfuscated() ? "session" : "field_71449_j";
	public static String yaw = isNotObfuscated() ? "yaw" : "field_149476_e";
	public static String playerTextures = isNotObfuscated() ? "playerTextures" : "field_187107_a";
	public static String pitch = isNotObfuscated() ? "pitch" : "field_149473_f";
	public static String currentGameType = isNotObfuscated() ? "currentGameType" : "field_78779_k";
	public static String connection = isNotObfuscated() ? "connection" : "field_78774_b";
	public static String blockHitDelay = isNotObfuscated() ? "blockHitDelay" : "field_78781_i";
	public static String message = isNotObfuscated() ? "message" : "field_149440_a";
	public static String curBlockDamageMP = isNotObfuscated() ? "curBlockDamageMP" : "field_78770_f";
	public static String isHittingBlock = isNotObfuscated() ? "isHittingBlock" : "field_78778_j";
	public static String onUpdateWalkingPlayer = isNotObfuscated() ? "onUpdateWalkingPlayer" : "func_175161_p";

    public static boolean isNotObfuscated() {
        try {
        	return Minecraft.class.getDeclaredField("instance") != null;
        } catch (Exception ex) {
            return false;
        }
    }
}
