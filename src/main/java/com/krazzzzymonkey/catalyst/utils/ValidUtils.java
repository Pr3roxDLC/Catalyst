package com.krazzzzymonkey.catalyst.utils;

import com.krazzzzymonkey.catalyst.managers.EnemyManager;
import com.krazzzzymonkey.catalyst.managers.FriendManager;
import com.krazzzzymonkey.catalyst.managers.ModuleManager;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.utils.system.Wrapper;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

public class ValidUtils {

	public static boolean isValidEntityTracers(EntityLivingBase e) {
		Modules targets = ModuleManager.getModule("Tracers");
		if(targets.isToggled()) {
			if(targets.isToggledValue("Players") && e instanceof EntityPlayer) {
				return false;
			}
			else
                return !targets.isToggledValue("Mobs") || !(e instanceof EntityLiving);
		}
		return true;
	}



}
