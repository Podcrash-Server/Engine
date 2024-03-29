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

public class WrapperPlayServerMap extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.MAP;

    public WrapperPlayServerMap() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }

    public WrapperPlayServerMap(PacketContainer packet) {
        super(packet, TYPE);
    }

    /**
     * Retrieve Item Damage.
     * <p>
     * Notes: the damage value of the map being modified
     * @return The current Item Damage
     */
    public int getItemDamage() {
        return handle.getIntegers().read(0);
    }

    /**
     * Set Item Damage.
     * @param value - new value.
     */
    public void setItemDamage(int value) {
        handle.getIntegers().write(0, value);
    }

    /**
     * Retrieve Scale.
     * @return The current Scale
     */
    public byte getScale() {
        return handle.getBytes().read(0);
    }

    /**
     * Set Scale.
     * @param value - new value.
     */
    public void setScale(byte value) {
        handle.getBytes().write(0, value);
    }

    public Object[] getMapIcons() {
        return (Object[]) handle.getModifier().read(2);
    }

    public void setMapIcons(Object[] value) {
        handle.getModifier().write(2, value);
    }

    public int getColumns() {
        return handle.getIntegers().read(1);
    }

    public void setColumns(int value) {
        handle.getIntegers().write(1, value);
    }

    public int getRows() {
        return handle.getIntegers().read(2);
    }

    public void setRows(int value) {
        handle.getIntegers().write(2, value);
    }

    public int getX() {
        return handle.getIntegers().read(3);
    }

    public void setX(int value) {
        handle.getIntegers().write(3, value);
    }

    public int getZ() {
        return handle.getIntegers().read(4);
    }

    public void setZ(int value) {
        handle.getIntegers().write(4, value);
    }

    public byte[] getData() {
        return handle.getByteArrays().read(0);
    }

    public void setData(byte[] value) {
        handle.getByteArrays().write(0, value);
    }
}
