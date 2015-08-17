package tk.jomp16.rcon.api.communication;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Class that represents a incoming rcon request
 */

@RequiredArgsConstructor
@Getter
public final class RconRequest {
    /**
     * The request ID
     */
    private final int id;
    /**
     * The request type
     *
     * @see tk.jomp16.rcon.internal.RconConstant for the rcon type
     */
    private final int type;
    /**
     * The body
     */
    private final String body;
}
