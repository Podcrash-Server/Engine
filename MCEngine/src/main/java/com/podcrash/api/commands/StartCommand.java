package com.podcrash.api.commands;

import com.podcrash.api.game.Game;
import com.podcrash.api.game.lobby.GameLobbyTimer;
import com.podcrash.api.game.GameManager;
import com.podcrash.api.game.GameState;
import com.podcrash.api.plugin.PodcrashSpigot;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

import java.util.Collections;

public class StartCommand extends BukkitCommand {

    public StartCommand() {
        super("startgame",
                "Start a game.",
                "/startgame",
                Collections.singletonList("start"));
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player) || !sender.hasPermission("invicta.host")) {
            sender.sendMessage(String.format("%sInvicta> %sYou have insufficient permissions to use that command.", ChatColor.BLUE, ChatColor.GRAY));
            return true;
        }
        if (args.length == 1 && args[0].equalsIgnoreCase("fast")) {
            GameManager.startGame();
            return true;
        }
        Player player = (Player) sender;
        if (GameManager.hasPlayer(player)) {
            Game game = GameManager.getGame();
            if (game == null || game.getGameState() == GameState.STARTED) {
                sender.sendMessage("Game has started already!");
                return true;
            }
            GameLobbyTimer timer = game.getTimer();
            if (timer.isRunning()) {
                timer.stop(true);
                player.sendMessage("Timer paused");
                return true;
            }
            log(game.toString());
            if (game.hasChosenMap()) {
                timer.start();
            } else {
                player.sendMessage("A map has not been set for Game #" + game.getId());
            }
        } else {
            player.sendMessage("You are currently not in a game");
        }
        return true;
    }

    private void log(String msg){
        PodcrashSpigot.getInstance().getLogger().info(String.format("%s %s", this.getClass().getSimpleName(), msg));
    }

}