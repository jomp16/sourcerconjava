/*
 * Copyright (C) 2015 jomp16
 *
 * This file is part of Source RCON - Java.
 *
 * Source RCON - Java is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Source RCON - Java is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Source RCON - Java. If not, see <http://www.gnu.org/licenses/>.
 */

package tk.jomp16.rcon.internal.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.log4j.Log4j2;
import tk.jomp16.rcon.api.communication.RconResponse;

import java.nio.ByteOrder;

/**
 * Encodes a RconResponse into a raw rcon packet
 *
 * @see RconResponse
 */

@Log4j2
public final class RconNettyEncoder extends MessageToByteEncoder<RconResponse> {
    @Override
    protected void encode(final ChannelHandlerContext ctx, final RconResponse msg, final ByteBuf out) throws Exception {
        final String ip = ctx.channel().remoteAddress().toString().replaceFirst("/", "");

        log.trace("(" + ip + ") - SENT --> [" + msg.getId() + "][" + msg.getType() + "] -- " + msg.getResponseBody().toString().replaceAll("[\\r\\n]+", "(newline)"));

        out.order(ByteOrder.LITTLE_ENDIAN)
                .writeInt(msg.getResponseBody().toString().length() + 12)
                .writeInt(msg.getId())
                .writeInt(msg.getType())
                .writeBytes(msg.getResponseBody().toString().getBytes())
                .writeInt(0);
    }
}
