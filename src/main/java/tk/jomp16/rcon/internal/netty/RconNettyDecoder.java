package tk.jomp16.rcon.internal.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import tk.jomp16.rcon.api.communication.RconRequest;

import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.List;

/**
 * Decodes the raw rcon packet and wrap into a RconRequest class
 *
 * @see RconRequest
 */
public final class RconNettyDecoder extends ByteToMessageDecoder {
    private final boolean printPacket;

    public RconNettyDecoder(final boolean printPacket) {
        this.printPacket = printPacket;
    }

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

        if (this.printPacket) {
            System.out.println("RCON_DECODER -> ID(" + id + "), TYPE(" + type + "), BODY(" + body + ")");
        }

//        System.out.println("packetLength=" + packetLength + " id=" + id + " type=" + type + " body=" + body);

        out.add(new RconRequest(id, type, body));
    }
}
