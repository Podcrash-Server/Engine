package com.podcrash.api.game.scoreboard;

import com.podcrash.api.db.redis.Communicator;
import com.podcrash.api.game.Game;
import com.podcrash.api.game.GameState;
import com.podcrash.api.scoreboard.CustomScoreboard;
import com.podcrash.api.time.TimeHandler;
import com.podcrash.api.time.resources.TimeResource;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GameLobbyScoreboard extends CustomScoreboard {

    private final Game game;
    private boolean running;

    public GameLobbyScoreboard(Game game) {
        super(15);
        this.game = game;
        running = false;
    }

    public boolean isRunning() {
        return running;
    }

    public void run() {
        running = true;
        TimeHandler.repeatedTime(5, 0, new TimeResource() {
            @Override
            public void task() {
                updateLobbyScoreboard();
                for (Player player : game.getBukkitPlayers()){
                    player.setScoreboard(getBoard());
                }
            }

            @Override
            public boolean cancel() {
                return game.getGameState() == GameState.STARTED;
            }

            @Override
            public void cleanup() {
                for (Player player : game.getBukkitPlayers()){
                    player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
                }
                running = false;
            }
        });
    }

    private void updateLobbyScoreboard() {
        List<String> lines = new ArrayList<>();

        lines.add("&c&lGame");
        lines.add(game.getMode());
        lines.add("");
        lines.add("&b&lServer");
        lines.add("&f" + Communicator.getCode());
        lines.add("");
        lines.add("&e&lPlayers");
        lines.add("&f" + game.size() + "/" + game.getMaxPlayers());
        lines.add("");
        lines.add("&d&lMap");
        lines.add(game.getMapName());
        lines.add("");
        lines.add("&a&lStatus");
        lines.add(game.getTimer().getStatus());

        setLines(lines);
    }
}
