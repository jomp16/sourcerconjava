package tk.jomp16.rcon.api.communication;

import tk.jomp16.rcon.internal.RconConstant;

/**
 * Class that represents a outgoing rcon response
 */
public final class RconResponse {
    /**
     * The response ID
     */
    private int id;
    /**
     * The response type
     */
    private int type;
    /**
     * The response body
     */
    private String body;

    /**
     * Creates a new RconResponse with just an ID, and the SERVERDATA_RESPONSE_VALUE type
     *
     * @param id the response ID
     */
    public RconResponse(final int id) {
        this.init(id, RconConstant.SERVERDATA_RESPONSE_VALUE);
    }

    /**
     * Creates a new RconResponse with an ID and a type
     *
     * @param id   the response ID
     * @param type the response type
     */
    public RconResponse(final int id, final int type) {
        this.init(id, type);
    }

    /**
     * Initializes a RconResponse with an ID and a type
     *
     * @param id   the response ID
     * @param type the response type
     */
    private void init(final int id, final int type) {
        this.id = id;
        this.type = type;
        this.body = "";
    }

    /**
     * Appends a new string into the body
     *
     * @param s the content to be appended into the body
     * @return this instance of RconResponse
     */
    public RconResponse append(final String s) {
        this.body += s;

        return this;
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
