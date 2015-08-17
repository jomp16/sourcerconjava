/*
 * Copyright (C) 2015 jomp16
 *
 * This file is part of Source RCON - Java.
 *
 * Source RCON - Java is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Source RCON - Java is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Source RCON - Java. If not, see <http://www.gnu.org/licenses/>.
 */

package tk.jomp16.rcon.internal.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.AttributeKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import tk.jomp16.rcon.api.communication.RconRequest;
import tk.jomp16.rcon.api.communication.RconResponse;
import tk.jomp16.rcon.internal.RconConstant;
import tk.jomp16.rcon.internal.RconServer;

@RequiredArgsConstructor
@Log4j2
public final class RconNettyHandler extends ChannelInboundHandlerAdapter {
    private final RconServer rconServer;
    private final AttributeKey<Boolean> authenticatedAttribute = AttributeKey.valueOf("AUTHENTICATED");

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

        this.rconServer.getRconEvents().parallelStream().forEach(rconEvent -> {
            try {
                rconEvent.handle(this.rconServer, ctx.channel(), rconRequest);
            } catch (final Exception e) {
                log.error("An error happened while handling Source RCON packet!", e);
            }
        });
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) throws Exception {
        log.error("An error happened while handling Source RCON packet!", cause);
    }
}
