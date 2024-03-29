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
import com.comphenix.protocol.wrappers.BlockPosition;
import org.bukkit.Location;
import org.bukkit.util.Vector;

public class WrapperPlayServerWorldEvent extends AbstractPacket implements ILocationPacket {
    public static final PacketType TYPE = PacketType.Play.Server.WORLD_EVENT;
    
    public WrapperPlayServerWorldEvent() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperPlayServerWorldEvent(PacketContainer packet) {
        super(packet, TYPE);
    }
    
    /**
     * Retrieve Effect ID.
     * <p>
     * Notes: the ID of the effect, see below.
     * @return The current Effect ID
     */
    public int getEffectId() {
        return handle.getIntegers().read(0);
    }
    
    /**
     * Set Effect ID.
     * @param value - new value.
     */
    public void setEffectId(int value) {
        handle.getIntegers().write(0, value);
    }
    
    /**
     * Retrieve Location.
     * <p>
     * Notes: the location of the effect
     * @return The current Location
     */
    public BlockPosition getLocation() {
        return handle.getBlockPositionModifier().read(0);
    }
    
    /**
     * Set Location.
     * @param value - new value.
     */
    public void setLocation(BlockPosition value) {
        handle.getBlockPositionModifier().write(0, value);
    }

    @Override
    public void setLocation(Location location) {
        setLocation(location.toVector());
    }

    @Override
    public void setLocation(Vector vector) {
        setLocation(new BlockPosition(vector));
    }

    /**
     * Please don't use this.
     * @param x
     * @param y
     * @param z
     */

    @Override
    public void setLocation(float x, float y, float z) {
        setLocation(new BlockPosition((int) x, (int) y, (int) z));
    }

    /**
     * Retrieve Data.
     * <p>
     * Notes: extra data for certain effects, see below.
     * @return The current Data
     */
    public int getData() {
        return handle.getIntegers().read(1);
    }
    
    /**
     * Set Data.
     * @param value - new value.
     */
    public void setData(int value) {
        handle.getIntegers().write(1, value);
    }
    
    /**
     * Retrieve Disable relative volume.
     * <p>
     * Notes: see above
     * @return The current Disable relative volume
     */
    public boolean getDisableRelativeVolume() {
        return handle.getSpecificModifier(boolean.class).read(0);
    }
    
    /**
     * Set Disable relative volume.
     * @param value - new value.
     */
    public void setDisableRelativeVolume(boolean value) {
        handle.getSpecificModifier(boolean.class).write(0, value);
    }
    
}

