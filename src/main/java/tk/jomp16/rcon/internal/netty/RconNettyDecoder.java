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

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.log4j.Log4j2;
import tk.jomp16.rcon.api.communication.RconRequest;

import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.List;

/**
 * Decodes the raw rcon packet and wrap into a RconRequest class
 *
 * @see RconRequest
 */

@Log4j2
public final class RconNettyDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(final ChannelHandlerContext ctx, final ByteBuf in, final List<Object> out) throws Exception {
        if (in.readableBytes() < 8) {
            return;
        }

        final ByteBuf byteBuf = in.order(ByteOrder.LITTLE_ENDIAN);

        final int readerIndex = in.readerIndex();
        final int packetLength = byteBuf.readInt();

        if (packetLength < 4 || packetLength > 4096) {
            return;
        }

        if (byteBuf.readableBytes() < packetLength) {
            byteBuf.readerIndex(readerIndex);

            return;
        }

        final int id = byteBuf.readInt();
        final int type = byteBuf.readInt();

        final String body = byteBuf.readBytes(byteBuf.readableBytes() - 4).toString(Charset.forName("UTF-8"));

        final String ip = ctx.channel().remoteAddress().toString().replaceFirst("/", "");

        log.trace("(" + ip + ") - GOT --> [" + id + "][" + type + "] -- " + body.replaceAll("[\\r\\n]+", "(newline)"));

//       log.debug("packetLength=" + packetLength + " id=" + id + " type=" + type + " body=" + body);

        out.add(new RconRequest(id, type, body));
    }
}
