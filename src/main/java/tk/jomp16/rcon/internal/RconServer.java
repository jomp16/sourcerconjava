package tk.jomp16.rcon.internal;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import tk.jomp16.rcon.api.event.RconEvent;
import tk.jomp16.rcon.internal.netty.RconNettyDecoder;
import tk.jomp16.rcon.internal.netty.RconNettyEncoder;
import tk.jomp16.rcon.internal.netty.RconNettyHandler;

import java.io.Closeable;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public final class RconServer implements Closeable {
    private final String host;
    private final int port;
    private final String rconPassword;
    private boolean printPacket;
    private ServerBootstrap serverBootstrap;
    private List<RconEvent> rconEvents;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    /**
     * Creates a new instance of RconServer
     *
     * @param host         the IP that the server will bind on
     * @param port         the port that the server will bind on
     * @param rconPassword the password to authenticate the users
     * @param printPacket  if you want to see the incoming and outgoing packet
     */
    public RconServer(final String host, final int port, final String rconPassword, final boolean printPacket) {
        this.host = host;
        this.port = port;
        this.rconPassword = rconPassword;
        this.printPacket = printPacket;

        if (this.rconPassword == null || this.rconPassword.isEmpty()) {
            System.out.println("RCON password not set!");

            return;
        }

        this.rconEvents = new LinkedList<>();

        this.bossGroup = new NioEventLoopGroup();
        this.workerGroup = new NioEventLoopGroup();

        this.serverBootstrap = new ServerBootstrap();

        this.serverBootstrap.group(this.bossGroup, this.workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(final SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new RconNettyDecoder(RconServer.this.printPacket),
                                new RconNettyEncoder(RconServer.this.printPacket), new RconNettyHandler(RconServer.this));
                    }
                })
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
    }

    /**
     * Starts the RconServer instance
     */
    public void startServer() {
        final ChannelFuture channelFuture = this.serverBootstrap.bind(this.host, this.port);

        channelFuture.awaitUninterruptibly();

        if (channelFuture.isDone()) {
            if (!channelFuture.isSuccess()) {
                System.err.println("An error happened when trying to start RCON server!");

                this.bossGroup.shutdownGracefully();
                this.workerGroup.shutdownGracefully();
            }
        }
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

    /**
     * Get the rconPassword, used in AuthRconEvent
     *
     * @return the rconPassword
     */
    public String getRconPassword() {
        return this.rconPassword;
    }

    /**
     * Gets all rconEvents which handles an incoming rcon request
     *
     * @return the list of all RconEvents
     */
    public List<RconEvent> getRconEvents() {
        return this.rconEvents;
    }

    @Override
    public void close() throws IOException {
        this.bossGroup.shutdownGracefully();
        this.workerGroup.shutdownGracefully();
    }
}
