package net.neko;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import net.neko.game.player.Player;
import net.neko.network.PlayerManager;
import net.neko.network.decoder.NetworkEventDecoder;
import net.neko.network.encoder.NetworkEventEncoder;
import net.neko.network.event.events.EventKick;
import net.neko.network.handlers.ServerHandler;

public class Server {
    private final int port;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    public Server(int port) {
        this.port = port;
    }

    public void run() throws Exception {
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast(
                                    new NetworkEventDecoder(),
                                    new NetworkEventEncoder(),
                                    new ServerHandler()
                            );
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture f = b.bind(port).sync();
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public void close() {
        for (Player player : PlayerManager.getInstance().getPlayers()) {
            player.sendEvent(new EventKick("Server is shutting down."));
            player.getPlayerConnection().ctx().close();
        }

        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
    }
}