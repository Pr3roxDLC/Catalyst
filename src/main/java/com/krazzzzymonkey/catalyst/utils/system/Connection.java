package com.krazzzzymonkey.catalyst.utils.system;

import com.krazzzzymonkey.catalyst.events.PacketEvent;
import com.krazzzzymonkey.catalyst.managers.ModuleManager;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import net.minecraft.network.Packet;

public class Connection extends ChannelDuplexHandler {


    public Connection() {

        try {
            ChannelPipeline pipeline = Wrapper.INSTANCE.mc().getConnection().getNetworkManager().channel.pipeline();
            pipeline.addBefore("packet_handler", "PacketHandler", this);
        } catch (Exception exception) {

            exception.printStackTrace();
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object packet) throws Exception {
        PacketEvent event = new PacketEvent((Packet<?>) packet, PacketEvent.Side.IN);
        ModuleManager.EVENT_MANAGER.post(event);

        if (event.isCancelled()){
            return;
        }

        super.channelRead(ctx, packet);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object packet, ChannelPromise promise) throws Exception {
        PacketEvent event = new PacketEvent((Packet<?>) packet, PacketEvent.Side.OUT);
        ModuleManager.EVENT_MANAGER.post(event);

        if (event.isCancelled()){
            return;
        }

        super.write(ctx, packet, promise);
    }

    public enum Side {
        IN,
        OUT
    }
}
