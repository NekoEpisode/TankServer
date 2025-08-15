package net.nekoepisode.tankserver.network.event.events;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.nekoepisode.tankserver.network.event.INetworkEvent;
import net.nekoepisode.tankserver.network.utils.NetworkUtils;

public class EventPlaySound implements INetworkEvent {
    private String sound;
    private float pitch;
    private float volume;

    public EventPlaySound() {}

    public EventPlaySound(String sound, float pitch, float volume) {
        this.sound = sound;
        this.pitch = pitch;
        this.volume = volume;
    }

    @Override
    public void write(ByteBuf b) {
        NetworkUtils.writeString(b, sound);
        b.writeFloat(pitch);
        b.writeFloat(volume);
    }

    @Override
    public void read(ByteBuf b) {
        sound = NetworkUtils.readString(b);
        pitch = b.readFloat();
        volume = b.readFloat();
    }

    @Override
    public void handle(ChannelHandlerContext ctx) {}
}
