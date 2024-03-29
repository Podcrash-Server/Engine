package com.podcrash.api.mc.commands;

import com.podcrash.api.mc.commands.CommandBase;
import com.podcrash.api.mc.game.GameManager;
import com.podcrash.api.mc.game.GameState;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpecCommand extends CommandBase {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(sender instanceof Player) {
            Player player = (Player) sender;
            if(GameManager.getGame().getGameState() == GameState.STARTED) {
                player.sendMessage(
                        String.format(
                                "%sInvicta> %sYou may not switch teams mid-game!",
                                ChatColor.BLUE,
                                ChatColor.GRAY));
                return true;
            }
            GameManager.getGame().toggleSpec(player);
            return true;
        }
        return false;
    }
}
