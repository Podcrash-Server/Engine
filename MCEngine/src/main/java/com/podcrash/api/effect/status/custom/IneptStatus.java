package com.podcrash.api.effect.status.custom;

import com.podcrash.api.effect.status.Status;
import com.podcrash.api.game.Game;
import com.podcrash.api.game.GameManager;
import org.bukkit.entity.Player;

/**
 * Part of the respawn for games
 */
public class IneptStatus extends CustomStatus {
    private final Game game;
    public IneptStatus(Player player) {
        super(player, Status.INEPTITUDE);
        game = GameManager.getGame();
    }

    @Override
    protected void doWhileAffected() {
        for(Player player : game.getBukkitPlayers()){
            if (player != getPlayer() && player.canSee(getPlayer())) player.hidePlayer(getPlayer());
        }
    }

    @Override
    protected boolean isInflicted() {
        return getApplier().isInept();
    }

    @Override
    protected void removeEffect() {
        getApplier().removeInept();
        for(Player player : game.getBukkitPlayers()){
            if (player != getPlayer()) player.showPlayer(getPlayer());
        }
    }
}