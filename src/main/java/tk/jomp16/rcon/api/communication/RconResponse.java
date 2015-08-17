package tk.jomp16.rcon.api.communication;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Class that represents a outgoing rcon response
 */

@RequiredArgsConstructor
@Getter
public final class RconResponse {
    /**
     * The response ID
     */
    private final int id;
    /**
     * The response type
     */
    private final int type;
    /**
     * The response body
     */
    @SuppressWarnings("MismatchedQueryAndUpdateOfStringBuilder")
    private final StringBuilder responseBody = new StringBuilder();

    /**
     * Appends a new string into the body
     *
     * @param s the content to be appended into the body
     * @return this instance of RconResponse
     */
    public RconResponse append(final String s) {
        this.responseBody.append(s);

        return this;
    }
}
