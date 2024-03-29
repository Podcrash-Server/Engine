package com.podcrash.api.mc.callback.helpers;

import com.abstractpackets.packetwrapper.WrapperPlayServerWorldParticles;
import com.comphenix.net.sf.cglib.asm.$ClassWriter;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.podcrash.api.mc.callback.sources.AwaitTime;
import com.podcrash.api.mc.callback.sources.HitGround;
import com.podcrash.api.mc.effect.particle.ParticleGenerator;
import com.podcrash.api.mc.events.TrapPrimeEvent;
import com.podcrash.api.mc.util.PacketUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Item;

import java.util.HashSet;
import java.util.Set;

/**
 * Use this class to spawn items, await until they touch the ground, and prime them!
 */
public final class TrapSetter {
    private final static Set<Integer> trapIds = new HashSet<>();

    /**
     *
     * @param item the item being used to make the trap
     * @param elapsed how long the trap should remain until it is primed (in milliseconds)
     */
    public static void spawnTrap(Item item, long elapsed) {
        HitGround ground = new HitGround(item);
        AwaitTime time = new AwaitTime(elapsed);
        trapIds.add(item.getEntityId());
        item.setPickupDelay(100000);
        time.then(() -> {
            if(!trapIds.contains(item.getEntityId())) return;
            item.setPickupDelay(0);
            TrapPrimeEvent primeEvent = new TrapPrimeEvent(item);
            Bukkit.getPluginManager().callEvent(primeEvent);
        });

        ground.then(() -> time.runAsync(1, 0)).runAsync(1, 0);
    }

    /**
     * The method is supposed to test if the item is actually a trap spawned from TrapSetter#spawnTrap
     * @param item the supposed trap of the item
     * @return whether or not the remove was successful (doubles as a contains)
     */
    public static boolean deleteTrap(Item item) {
        if(item == null) return false;
        return trapIds.remove(item.getEntityId());
    }

    public static boolean destroyTrap(Item item) {
        boolean delete = deleteTrap(item);
        item.remove();
        return delete;
    }
}
