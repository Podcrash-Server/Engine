package com.podcrash.api.mc.listeners;

import com.podcrash.api.mc.damage.DamageApplier;
import com.podcrash.api.mc.events.EnableLobbyPVPEvent;
import com.podcrash.api.mc.game.GameManager;
import com.podcrash.api.mc.game.GameType;
import com.podcrash.api.mc.sound.SoundPlayer;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class GeneralLobbyListener extends ListenerBase {
    public GeneralLobbyListener(JavaPlugin plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void enableGeneralLobbyPVP(PlayerInteractEvent event) {
        //if (event.isCancelled()) return;
        Player player = event.getPlayer();
        String mode;

        if (GameManager.getGame() != null) {
            mode = GameManager.getGame().getMode();
        } else {
            mode = null;
        }

        //System.out.println("tests");
        boolean isActioning = (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK ||
                event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK);
        boolean isHoldingItem = (!player.getItemInHand().getType().equals(Material.AIR) &&
                player.getItemInHand().getItemMeta().hasDisplayName() && player.getItemInHand().getItemMeta().getDisplayName().contains("Enable Lobby PVP"));


        //System.out.println(isActioning + " " + isHoldingItem);
        if (isActioning && isHoldingItem) {
            SoundPlayer.sendSound(player, "random.pop", 1F, 63);
            DamageApplier.removeInvincibleEntity(player);

            EnableLobbyPVPEvent e = new EnableLobbyPVPEvent(player, mode);
            Bukkit.getPluginManager().callEvent(e);
        }
    }

    @EventHandler
    private void applyGeneralPVPGear(EnableLobbyPVPEvent event) {
        // Only apply this general PVP gear if there is no game currently running.
        if(event.getGameType() != null)
            return;

        ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta meta = sword.getItemMeta();
        meta.spigot().setUnbreakable(true);
        sword.setItemMeta(meta);
        event.getPlayer().setItemInHand(sword);

        Material[] armor = {Material.IRON_BOOTS, Material.IRON_LEGGINGS, Material.IRON_CHESTPLATE , Material.IRON_HELMET};
        ItemStack[] armors = new ItemStack[4];
        for(int i = 0; i < armor.length; i++){
            Material mat = armor[i];
            net.minecraft.server.v1_8_R3.ItemStack nmsStack = CraftItemStack.asNMSCopy(new ItemStack(mat));
            NBTTagCompound tag = new NBTTagCompound();
            tag.setBoolean("Unbreakable", true);
            nmsStack.setTag(tag);

            armors[i] = new ItemStack(CraftItemStack.asBukkitCopy(nmsStack));
        }
        event.getPlayer().getEquipment().setArmorContents(armors);
    }
}
