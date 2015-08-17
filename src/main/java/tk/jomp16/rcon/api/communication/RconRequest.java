package tk.jomp16.rcon.api.communication;

/**
 * Class that represents a incoming rcon request
 */
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

    /**
     * Initializes this RconRequest with an ID, a type and a body
     *
     * @param id   the request id
     * @param type the request type
     * @param body the request body
     */
    public RconRequest(final int id, final int type, final String body) {
        this.id = id;
        this.type = type;
        this.body = body;
    }

    public int getId() {
        return this.id;
    }

    public int getType() {
        return this.type;
    }

    public String getBody() {
        return this.body;
    }
}
