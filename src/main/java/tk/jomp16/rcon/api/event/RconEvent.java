/*
 * Copyright (C) 2015 jomp16
 *
 * This file is part of Source RCON - Java.
 *
 * Source RCON - Java is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Source RCON - Java is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Source RCON - Java. If not, see <http://www.gnu.org/licenses/>.
 */

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
