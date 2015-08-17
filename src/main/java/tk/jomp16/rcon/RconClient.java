package tk.jomp16.rcon;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;

import static java.nio.ByteOrder.LITTLE_ENDIAN;

public class RconClient {

    /*
     * The constants are the packet types.
     */
    static final int EXECUTE_COMMAND_PACKET = 2;
    static final int AUTHORIZATION_PACKET = 3;
    static final int AUTHORIZATION_RESPONSE = 2;
    static final SocketAddress serverAddress = new InetSocketAddress("127.0.0.1", 27015);
    static final String rconPassword = "lolwut";
    static InputStream inputStream;
    static OutputStream outputStream;

    public static void start() throws IOException {
        /*
		 * Prepare the socket and retrieve the streams.
		 */
        final Socket socket = new Socket();
//        socket.bind(new InetSocketAddress(InetAddress.getLocalHost(), 27015));
        socket.connect(serverAddress);
        inputStream = socket.getInputStream();
        outputStream = socket.getOutputStream();

		/*
		 * Authorize the user and then send commands. Multiple commands can be sent, but the user may need to be
		 * reauthorized after some time.
		 */
        if (!sendAuthorizationPacket()) {
            System.out.println("Error when trying to authenticate!");

            socket.close();

            return;
        }

//        sendCommand("say Hello, world!");

        sendCommand("version");

        /*
		 * Close the socket once its done being used.
		 */
        socket.close();
    }

    /**
     * Sends a command to the server. The user must be authorized for the server to execute the commands.
     *
     * @param command the command
     * @throws IOException
     */
    static void sendCommand(final String command) throws IOException {
        final byte[] packet = createPacket(1000, EXECUTE_COMMAND_PACKET, command);
        outputStream.write(packet);

		/*
		 * The server responds to command execution packets as well, but those responses are not very important.
		 * However, it may be worthwhile to check if the server actually responded with text. If a response contains no
		 * textual message, that usually means that the user needs to be reauthorized with the server. Another way to
		 * check if the user needs to be reauthorized is simply by joining the server and seeing if the commands sent
		 * have any effect.
		 */
    }

    /**
     * Sends an authorization packet to the server.
     *
     * @return whether or not the user was authorized by the server
     * @throws IOException
     */
    static boolean sendAuthorizationPacket() throws IOException {
		/*
		 * Create the authorization packet and send it.
		 */
        final byte[] packet = createPacket(1000, AUTHORIZATION_PACKET, rconPassword);
        outputStream.write(packet);

		/*
		 * Read the response (the first packet is a junk one) and check if the server authorized the user. The user is
		 * authorized if the server responds with the same packet ID as the one sent, which is 1000 in this case.
		 */
        parsePacket();

        final ByteBuffer response = parsePacket();
        return (response.getInt(8) == AUTHORIZATION_RESPONSE) && (response.getInt(4) == 1000);
    }

    /**
     * Creates an RCON packet.
     *
     * @param id      the packet ID (not an opcode)
     * @param type    the type
     * @param command the command
     * @return the bytes representing the packet
     */
    static byte[] createPacket(final int id, final int type, final String command) {
        final ByteBuffer packet = ByteBuffer.allocate(command.length() + 16);
        packet.order(LITTLE_ENDIAN);
        packet.putInt(command.length() + 12).putInt(id).putInt(type).put(command.getBytes()).putInt(0);
        return packet.array();
    }

    /**
     * Parses the next packet from the socket's input stream.
     *
     * @return the next packet
     * @throws IOException
     */
    static ByteBuffer parsePacket() throws IOException {
		/*
		 * Read the length of the packet.
		 */
        final byte[] length = new byte[4];

        inputStream.read(length);

        /*
		 * Create a buffer to contain the packet's payload.
		 */
        final ByteBuffer packet = ByteBuffer.allocate(4120);
        packet.order(LITTLE_ENDIAN);
        packet.put(length);

		/*
		 * Read the payload.
		 */

        final int size = packet.getInt(0);

        for (int i = 0; i < size; i++) {
            packet.put((byte) inputStream.read());
        }

        return packet;
    }
}
