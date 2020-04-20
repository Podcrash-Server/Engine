package com.podcrash.api.mc.commands;

import com.podcrash.api.mc.game.GameManager;
import com.podcrash.api.mc.game.GameState;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class SpecCommand extends BukkitCommand {

    public SpecCommand() {
        super("spectate",
                "Spectate a game.",
                "/spectate",
                Collections.emptyList());
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        Player player = (Player) sender;
        if (GameManager.getGame().getGameState() == GameState.STARTED) {
            player.sendMessage(String.format("%sInvicta> %sYou may not switch teams mid-game!", ChatColor.BLUE, ChatColor.GRAY));
            return true;
        }
        GameManager.getGame().toggleSpec(player);
        return true;
    }
}
