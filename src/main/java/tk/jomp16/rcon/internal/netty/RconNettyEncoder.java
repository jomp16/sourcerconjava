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
