package tk.jomp16.rcon.internal.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.AttributeKey;
import tk.jomp16.rcon.api.communication.RconRequest;
import tk.jomp16.rcon.api.communication.RconResponse;
import tk.jomp16.rcon.internal.RconConstant;
import tk.jomp16.rcon.internal.RconServer;

public final class RconNettyHandler extends ChannelInboundHandlerAdapter {
    private final RconServer rconServer;
    private final AttributeKey<Boolean> authenticatedAttribute = AttributeKey.valueOf("AUTHENTICATED");

    /**
     * Creates a new instance of RconNettyHandler, necessary to handle the rcon requests, and if type is a SERVERDATA_EXECCOMMAND
     * check the rconServer.getRconEvents() map if the command is present and call it
     *
     * @param rconServer the RconServer that contains the rconPassword and rconEvents
     */
    public RconNettyHandler(final RconServer rconServer) {
        this.rconServer = rconServer;
    }

    /**
     * This method just register and set the authenticated attribute to false
     *
     * @param ctx the client ChannelHandlerContext
     */
    @Override
    public void channelRegistered(final ChannelHandlerContext ctx) {
        ctx.attr(this.authenticatedAttribute).set(false);
    }

    /**
     * This method just set the authenticated attribute to false
     *
     * @param ctx the client ChannelHandlerContext
     */
    @Override
    public void channelUnregistered(final ChannelHandlerContext ctx) {
        ctx.attr(this.authenticatedAttribute).set(false);
    }

    /**
     * Reads, and do a action depending of the type of request
     *
     * @param ctx the client ChannelHandlerContext
     * @param msg the RconRequest packet
     * @see RconRequest
     */
    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) {
        final RconRequest rconRequest = (RconRequest) msg;

        if (rconRequest.getType() == RconConstant.SERVERDATA_AUTH) {
            if (ctx.attr(this.authenticatedAttribute).get() || rconRequest.getBody().isEmpty()) {
                ctx.disconnect();

                return;
            }

            ctx.writeAndFlush(new RconResponse(rconRequest.getId(), RconConstant.SERVERDATA_RESPONSE_VALUE));
            ctx.writeAndFlush(new RconResponse(this.rconServer.getRconPassword().equals(rconRequest.getBody()) ? rconRequest.getId() : -1, RconConstant.SERVERDATA_AUTH_RESPONSE));

            ctx.attr(this.authenticatedAttribute).set(true);

            return;
        }

        this.rconServer.getRconEvents().parallelStream().forEach(rconEvent -> rconEvent.handle(this.rconServer, ctx.channel(), rconRequest));
    }
}
