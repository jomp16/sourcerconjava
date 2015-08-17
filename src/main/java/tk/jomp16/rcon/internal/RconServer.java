package tk.jomp16.rcon.internal;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import tk.jomp16.rcon.api.event.RconEvent;
import tk.jomp16.rcon.internal.netty.RconNettyDecoder;
import tk.jomp16.rcon.internal.netty.RconNettyEncoder;
import tk.jomp16.rcon.internal.netty.RconNettyHandler;

import java.io.Closeable;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

@Log4j2
public final class RconServer implements Closeable {
    private final String host;
    private final int port;
    @Getter
    private final String rconPassword;
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    @Getter
    private List<RconEvent> rconEvents;
    private ServerBootstrap serverBootstrap;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private boolean canStartServer;

    /**
     * Creates a new instance of RconServer
     *
     * @param host         the IP that the server will bind on
     * @param port         the port that the server will bind on
     * @param rconPassword the password to authenticate the users
     * @param epoll        if Netty server will run on epoll
     */
    public RconServer(final String host, final int port, final String rconPassword, final boolean epoll) {
        this.host = host;
        this.port = port;
        this.rconPassword = rconPassword;

        if (this.rconPassword == null || this.rconPassword.isEmpty()) {
            log.error("Source RCON password not set!");

            return;
        }

        this.rconEvents = new LinkedList<>();

        this.bossGroup = epoll ? new EpollEventLoopGroup() : new NioEventLoopGroup();
        this.workerGroup = epoll ? new EpollEventLoopGroup() : new NioEventLoopGroup();

        this.serverBootstrap = new ServerBootstrap();

        this.serverBootstrap.group(this.bossGroup, this.workerGroup)
                .channel(epoll ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(final SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new RconNettyDecoder(), new RconNettyEncoder(), new RconNettyHandler(RconServer.this));
                    }
                })
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);

        this.canStartServer = true;
    }

    /**
     * Starts the RconServer instance
     */
    public void startServer() {
        if (!this.canStartServer) {
            log.error("Cannot start Source RCON server!");

            return;
        }

        new Thread(() -> {
            log.info("Starting Source RCON server...");
            final ChannelFuture channelFuture = this.serverBootstrap.bind(this.host, this.port);

            channelFuture.awaitUninterruptibly();

            if (channelFuture.isDone()) {
                if (channelFuture.isSuccess()) {
                    log.info("Source RCON server started on ip " + this.host + " port " + this.port + "!");

                    channelFuture.channel().closeFuture().awaitUninterruptibly();
                } else {
                    log.error("Error starting Source RCON server!", channelFuture.cause());

                    this.bossGroup.shutdownGracefully();
                    this.workerGroup.shutdownGracefully();
                }
            }
        }).start();

        log.info("Source RCON server started!");
    }

    /**
     * Add a command which will call the rconEvent.
     *
     * @param rconEvent the rconEvent that handle the request, except for AUTH
     */
    public RconServer addRconEvent(final RconEvent rconEvent) {
        this.rconEvents.add(rconEvent);

        return this;
    }

    @Override
    public void close() throws IOException {
        log.info("Closing Source RCON server...");

        this.bossGroup.shutdownGracefully();
        this.workerGroup.shutdownGracefully();

        log.info("Close of Source RCON server done!");
    }
}
