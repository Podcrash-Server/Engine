package com.podcrash.api.mc.commands;

import com.podcrash.api.db.TableOrganizer;
import com.podcrash.api.db.tables.DataTableType;
import com.podcrash.api.db.tables.RanksTable;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class AddRoleCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.isOp()) {
            sender.sendMessage(String.format("%sInvicta> %sYou have insufficient permissions to use that command.", ChatColor.BLUE, ChatColor.GRAY));
            return true;
        }

        if (args.length != 3) {
            sender.sendMessage("invalid command: try /addrole [ROLE NAME] [COLOR] [POSITION]");
            return true;
        }

        String roleName = args[0];


        String color = args[1];
        int position;
        try {
            color = ChatColor.valueOf(color.toUpperCase()).toString();
            position = Integer.parseInt(args[2]);
        } catch (IllegalArgumentException e) {
            sender.sendMessage("Invalid argument!");
            return true;
        }

        RanksTable table = TableOrganizer.getTable(DataTableType.PERMISSIONS);
        table.addRank(roleName, color, position);

        return true;

    }
}
