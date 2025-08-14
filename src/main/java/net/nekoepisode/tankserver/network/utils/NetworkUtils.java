package net.nekoepisode.tankserver.network.utils;

import io.netty.buffer.ByteBuf;
import net.nekoepisode.tankserver.game.color.Color;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class NetworkUtils {
    public static final Charset charset = StandardCharsets.UTF_8;

    public static String readString(ByteBuf b) {
        int l = b.readInt();

        if (l < 0)
            return null;

        return b.readCharSequence(l, charset).toString();
    }

    public static void writeString(ByteBuf b, String s) {
        int extra = 0;

        if (s == null) {
            b.writeInt(-1);
            return;
        }

        for (int i = 0; i < s.length(); i++)
            if (s.charAt(i) == '§')
                extra++;

        b.writeInt(s.length() + extra);
        b.writeCharSequence(s, charset);
    }

    public static Color readColor(ByteBuf b) {
        Color color = new Color(0,0,0);
        color.red = b.readDouble();
        color.green = b.readDouble();
        color.blue = b.readDouble();
        return color;
    }

    public static void writeColor(ByteBuf b, Color c) {
        if (c == null) return;
        b.writeDouble(c.red);
        b.writeDouble(c.green);
        b.writeDouble(c.blue);
    }
}
