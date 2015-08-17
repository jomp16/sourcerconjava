package tk.jomp16.rcon.api.event;

import io.netty.channel.Channel;
import tk.jomp16.rcon.api.communication.RconRequest;
import tk.jomp16.rcon.internal.RconServer;

/**
 * The interface that classes which will handle commands must implement
 */
public interface RconEvent {
    void handle(final RconServer rconServer, final Channel channel, final RconRequest rconRequest) throws Exception;
}
