package com.krazzzzymonkey.catalyst.command;

import com.krazzzzymonkey.catalyst.utils.system.Wrapper;
import com.krazzzzymonkey.catalyst.utils.visual.ChatUtils;
import net.minecraft.network.play.client.CPacketChatMessage;

public class Say extends Command {
	public Say() {
		super("say");
	}

	@Override
	public void runCommand(String s, String[] args) {
		try {
			StringBuilder content = new StringBuilder();
			for (String arg : args) {
				content.append(" ").append(arg);
			}
			Wrapper.INSTANCE.sendPacket(new CPacketChatMessage(content.toString()));
		} catch (Exception e) {
			ChatUtils.error("Usage: " + getSyntax());
		}
	}

	@Override
	public String getDescription() {
		return "Send message to chat.";
	}

	@Override
	public String getSyntax() {
		return "say <message>";
	}
}