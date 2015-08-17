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
