package com.podcrash.api.commands;

import com.podcrash.api.db.TableOrganizer;
import com.podcrash.api.db.tables.DataTableType;
import com.podcrash.api.db.tables.PlayerTable;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/** TODO send request instead of directly adding
 *  TODO give more chat feedback when a user does something wrong, eg adding duplicate friends or removing ones that don't exist
 *  TODO change from "/f raXin remove" to "/f remove raXin"
 *  TODO gui alternative at some point?
 *  TODO use updateresult instead of contains(name) to save up on queries
 */

public class FriendCommand extends BukkitCommand {
    public FriendCommand() {
        super("f",
                "Add or remove friends.",
                "/f <remove> (Player Name)",
                Collections.emptyList());
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if(strings.length <= 2 && commandSender instanceof Player) {
            Player player = (Player) commandSender;
            PlayerTable table = TableOrganizer.getTable(DataTableType.PLAYERS);
            if(strings.length == 0) {
                table.getFriendsAsync(player.getUniqueId()).thenApply((friends) -> {
                    if(friends != null && friends.size() > 0) player.sendMessage(getNiceFriendList(UUIDSetToPlayers(friends)));
                    else player.sendMessage(ChatColor.RED + "No friends added.");
                    return true;
                });
            } else if (strings.length == 1) {
                OfflinePlayer target = Bukkit.getOfflinePlayer(strings[0]);
                if(target == null || target.getName().equals(player.getName())) return false;

                table.addFriend(target.getUniqueId(), player.getUniqueId());
                commandSender.sendMessage(String.format("%sFriends> %s%s %sis now your friend!"
                        , ChatColor.BLUE, ChatColor.YELLOW, strings[0], ChatColor.GRAY));
            } else {
                OfflinePlayer target = Bukkit.getOfflinePlayer(strings[0]);
                if(target == null || target.getName().equals(player.getName())) return false;

                table.removeFriend(target.getUniqueId(), player.getUniqueId());
                commandSender.sendMessage(String.format("%sFriends> %s%s %sis no longer your friend!"
                        , ChatColor.BLUE, ChatColor.YELLOW, strings[0], ChatColor.GRAY));
            }
            return true;
        } else {
            commandSender.sendMessage(String.format("%sFriends> %sYou entered too many arguments! Use \"/f\" to list your current friends.\n" +
                            "         Try doing \"/f (Player Name)\" to add a friend, or \"/f (Player Name) remove\" to remove a friend."
                    , ChatColor.BLUE, ChatColor.GRAY));
            return true;
        }
    }

    private Set<OfflinePlayer> UUIDSetToPlayers(Set<UUID> uuids) {
        Set<OfflinePlayer> result = new HashSet<>();
        for(UUID uuid : uuids) {
            result.add(Bukkit.getOfflinePlayer(uuid));
        }
        return result;
    }

    private String getNiceFriendList(Set<OfflinePlayer> friends) {
        StringBuilder builder = new StringBuilder();
        builder.append(ChatColor.GRAY);
        for(OfflinePlayer player : friends) {
            builder.append(player.getName()).append(" ");
        }
        return builder.toString();
    }
}
