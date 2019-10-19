package com.podcrash.api.mc.listeners;

import com.abstractpackets.packetwrapper.WrapperPlayServerEntityStatus;
import me.raindance.champions.Main;
import me.raindance.champions.damage.DamageQueue;
import me.raindance.champions.events.ApplyKitEvent;
import me.raindance.champions.events.DamageApplyEvent;
import me.raindance.champions.listeners.ListenerBase;
import me.raindance.champions.listeners.PlayerJoinEventTest;
import com.podcrash.api.mc.sound.SoundPlayer;
import com.podcrash.api.mc.util.PacketUtil;
import com.podcrash.api.mc.world.WorldManager;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.List;

/**
 * Prevent dumb things from happening
 */
public class MapMaintainListener extends ListenerBase {
    public MapMaintainListener(JavaPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    // stops crops from being destroyed if players jump on it
    public void onPlayerInteract(PlayerInteractEvent e) {
        // Physical means jump on it
        if (e.getAction() == Action.PHYSICAL) {
            Block block = e.getClickedBlock();
            if (block == null) return;
            // If the block is farmland (soil)
            if (block.getType() == Material.SOIL) {
                e.setCancelled(true);
                //block.setTypeIdAndData(block.getType().getId(), block.getData(), true);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onWeather(WeatherChangeEvent event){
        if (plugin.getConfig().getList("worlds").contains(event.getWorld().getName()) || evaluate(event.getWorld())) {
            event.getWorld().setWeatherDuration(0);
            event.setCancelled(true);

        }
    }
    @EventHandler
    public void onSpawn(CreatureSpawnEvent event) {
        if (plugin.getConfig().getList("worlds").contains(event.getEntity().getWorld().getName()) || evaluate(event.getEntity().getWorld())) {
            if (event.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.CUSTOM)) {
                return;
            }
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBreak(BlockBreakEvent e) {
        if (plugin.getConfig().getList("worlds").contains(e.getBlock().getWorld().getName()) || evaluate(e.getBlock().getWorld())) {
            if (!e.getPlayer().hasPermission("champions.can.break.map")) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDrop(PlayerDropItemEvent e) {
        if (plugin.getConfig().getList("worlds").contains(e.getPlayer().getWorld().getName()) || evaluate(e.getPlayer().getWorld())) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onFood(FoodLevelChangeEvent e) {
        if(e.getEntity().getWorld().getName().equalsIgnoreCase("world")) {
            e.setFoodLevel(20);
        }else e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPickUp(PlayerPickupItemEvent event){
        String name = event.getItem().getCustomName();
        if(name != null && name.startsWith("RITB")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void despawn(ItemDespawnEvent e) {
        if(evaluate(e.getEntity().getWorld())) {
            e.setCancelled(true);
        }
    }

    /**
     * Remove the default damage tick which makes alters your velocity
     * Remove default death mechanics.
     * @param event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void statusDamage(EntityDamageEvent event) {
        //if the event was cancelled, or the entity isn't living, or if it's in the og world,
        //or if the cause is null or custom
        // then cancel it
        if((event.isCancelled() ||
                !(event.getEntity() instanceof LivingEntity)) ||
                (event.getEntity().getWorld().getName().equals("world")) ||
                (event.getCause() == null || event.getCause() == EntityDamageEvent.DamageCause.CUSTOM)) {
            event.setCancelled(true);
            return;
        }
        LivingEntity p = (LivingEntity) event.getEntity();
        double damage = event.getDamage();
        //if the damage is 0, don't go through with the event
        if(damage <= 0) {
            event.setCancelled(true);
            return;
        }
        double afterHealth = p.getHealth() - damage;

        List<EntityDamageEvent.DamageCause> poss = Arrays.asList(
                EntityDamageEvent.DamageCause.FIRE_TICK,
                EntityDamageEvent.DamageCause.FALL,
                EntityDamageEvent.DamageCause.POISON,
                EntityDamageEvent.DamageCause.WITHER
        );
        //if the damage is one of the causes above
        //cancel it and set our own damage.
        //This is done because the types of damage above affects velocity
        if(poss.contains(event.getCause())) {
            event.setCancelled(true);
            damage(p, damage);
        }

        //if the player is about to die, cancel it
        //Then call our own death event.
        if(p instanceof Player && afterHealth <= 0D) {
            DamageQueue.artificialDie((Player) event.getEntity());
            event.setCancelled(true);
        }
    }

    /**
     * Send animation packets as well as sound
     * (For some reason, the animation packet is also supposed to send sound as well
     * but it doesn't?)
     * @param p
     * @param damage
     */
    private void damage(LivingEntity p, double damage) {
        double health = p.getHealth() - damage;
        p.setHealth((health < 0) ? 0 : health);

        WrapperPlayServerEntityStatus packet = new WrapperPlayServerEntityStatus();
        packet.setEntityId(p.getEntityId());
        packet.setEntityStatus(WrapperPlayServerEntityStatus.Status.ENTITY_HURT);

        PacketUtil.syncSend(packet, p.getWorld().getPlayers());
        SoundPlayer.sendSound(p.getLocation(), "game.player.hurt", 1, 63);

    }

    @EventHandler(priority = EventPriority.HIGH)
    public void die(PlayerDeathEvent event) {
        event.setDeathMessage(null);
        event.getDrops().clear();
        Main.getInstance().getLogger().info("from MapMaintainListener#92: If you ever see this message, it's a bug");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void blockTransform(BlockFromToEvent e) {
        Block block = e.getBlock();
        Block to = e.getToBlock();
        boolean cancel = false;
        if(evaluate(block.getWorld()) && block.getType() == Material.ICE && to.getType() == Material.WATER)
            cancel = true;
        else if(block.getType() == Material.AIR && to.getType() == Material.VINE)
            cancel = true;
        if(cancel) e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void damage(DamageApplyEvent event) {
        if(event.getVictim() instanceof Player && event.getVictim().getWorld().getName().equals("world"))
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void preventCraft(InventoryClickEvent event) {
        if(event.getWhoClicked().getWorld().getName().equals("world")) return;
        Inventory clicked = event.getClickedInventory();
        if(clicked != null && clicked.getType() == InventoryType.CRAFTING)
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void kit(ApplyKitEvent e) {
        if(e.getChampionsPlayer().getPlayer().getWorld().getName().equals("world")) {
            e.setKeepInventory(true);
            e.getChampionsPlayer().getInventory().setItem(35, PlayerJoinEventTest.beacon);
        }
    }
    private boolean evaluate(World world) {
        return WorldManager.getInstance().getWorlds().contains(world.getName());
    }
}
