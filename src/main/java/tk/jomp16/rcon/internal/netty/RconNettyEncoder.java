package tk.jomp16.rcon.internal.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import tk.jomp16.rcon.api.communication.RconResponse;

import java.nio.ByteOrder;

/**
 * Encodes a RconResponse into a raw rcon packet
 *
 * @see RconResponse
 */
public final class RconNettyEncoder extends MessageToByteEncoder<RconResponse> {
    private final boolean printPacket;

    public RconNettyEncoder(final boolean printPacket) {
        this.printPacket = printPacket;
    }

    @Override
    protected void encode(final ChannelHandlerContext ctx, final RconResponse msg, final ByteBuf out) throws Exception {
        if (this.printPacket) {
            System.out.println("RCON_ENCODER -> ID(" + msg.getId() + "), TYPE(" + msg.getType() + "), BODY(" + msg.getBody() + ")");
        }

        out.order(ByteOrder.LITTLE_ENDIAN)
                .writeInt(msg.getBody().length() + 12)
                .writeInt(msg.getId())
                .writeInt(msg.getType())
                .writeBytes(msg.getBody().getBytes())
                .writeInt(0);
    }
}
