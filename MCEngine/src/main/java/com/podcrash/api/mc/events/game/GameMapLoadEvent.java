package com.podcrash.api.mc.events.game;

import com.podcrash.api.db.pojos.map.BaseMap;
import com.podcrash.api.db.pojos.map.GameMap;
import com.podcrash.api.mc.game.Game;
import org.bukkit.World;
import org.bukkit.event.HandlerList;

/**
 * This will be called when the game is started
 */
public class GameMapLoadEvent extends GameEvent {
    private static HandlerList handlers = new HandlerList();
    private GameMap map;
    private World world;

    public GameMapLoadEvent(Game game, GameMap map, World world) {
        super(game, "loading the map " + map.getName());
        this.map = map;
        this.world = world;
    }

    public GameMap getMap() {
        return map;
    }

    public World getWorld() {
        return world;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
