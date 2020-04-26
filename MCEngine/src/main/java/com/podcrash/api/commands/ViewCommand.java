package com.podcrash.api.commands;

import com.podcrash.api.game.GameManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;

import java.util.Collections;

public class ViewCommand extends BukkitCommand {

    public ViewCommand() {
        super("view",
                "View information about a game.",
                "/view",
                Collections.emptyList());
    }

    //TODO: change the Game.toString() so that it is applicable for games w/ more than two teams

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (GameManager.getGame() != null)
            sender.sendMessage(GameManager.getGame().toString());
        else
            sender.sendMessage(String.format("%sInvicta> %sA game has not been created yet.", ChatColor.BLUE, ChatColor.GRAY));
        return true;
    }
}