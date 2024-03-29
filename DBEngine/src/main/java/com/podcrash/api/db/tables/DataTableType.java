package com.podcrash.api.db.tables;

import com.podcrash.api.db.ITable;
import org.bukkit.event.world.WorldLoadEvent;

public enum DataTableType {
    KITS(ChampionsKitTable.class),
    PLAYERS(PlayerTable.class),
    PERMISSIONS(RanksTable.class),
    MAPS(MapTable.class),
    WORLDS(WorldLoader.class),
    ECONOMY(EconomyTable.class)
    ;

    private Class<? extends ITable> table;
    DataTableType(Class<? extends ITable> table) {
        this.table = table;
    }

    public String getName(){
        return name().toLowerCase();
    }

    public Class<? extends ITable> getTable() {
        return table;
    }
}
