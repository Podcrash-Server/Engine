/**
 * This file is part of PacketWrapper.
 * Copyright (C) 2012-2015 Kristian S. Strangeland
 * Copyright (C) 2015 dmulloy2
 *
 * PacketWrapper is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PacketWrapper is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with PacketWrapper.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.abstractpackets.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;

public class WrapperStatusServerOutPing extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Status.Server.PONG;
    
    public WrapperStatusServerOutPing() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperStatusServerOutPing(PacketContainer packet) {
        super(packet, TYPE);
    }
    
    /**
     * Retrieve Time.
     * <p>
     * Notes: should be the same as sent by the client
     * @return The current Time
     */
    public long getTime() {
        return handle.getLongs().read(0);
    }
    
    /**
     * Set Time.
     * @param value - new value.
     */
    public void setTime(long value) {
        handle.getLongs().write(0, value);
    }
    
}

