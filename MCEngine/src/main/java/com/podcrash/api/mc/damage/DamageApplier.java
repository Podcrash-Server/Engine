package com.podcrash.api.mc.damage;

import com.abstractpackets.packetwrapper.WrapperPlayServerEntityVelocity;
import com.podcrash.api.mc.util.PacketUtil;
import net.jafama.FastMath;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityLiving;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.util.Vector;
import org.spigotmc.SpigotConfig;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Main class that you will use to apply custom damages
 */
public final class DamageApplier {

    private static Set<LivingEntity> invincibleEntities = new HashSet<>();

    public static Set<LivingEntity> getInvincibleEntities() {
        return invincibleEntities;
    }

    public static void addInvincibleEntity(LivingEntity entity) {
       invincibleEntities.add(entity);
    }

    public static void removeInvincibleEntity(LivingEntity entity) {
        invincibleEntities.remove(entity);
    }

    /**
     * Deal knockback, but with no multipliers
     * @see this#nativeApplyKnockback(LivingEntity, LivingEntity)
     * @param epvictim
     * @param epdamager
     */
    public static void nativeApplyKnockback(LivingEntity epvictim, LivingEntity epdamager) {
        nativeApplyKnockback(epvictim, epdamager, new double[]{1D, 1D, 1D});
    }

    /**
     * Set the knockback in relative to the position of the players
     * @param epvictim - victim
     * @param epdamager - attacker
     * @param velocityModifiers - in the form of x,y,z. These are multiplied after the
     *                          initial calculations to determine how much kb one should take.
     */
    public static void nativeApplyKnockback(LivingEntity epvictim, LivingEntity epdamager, double[] velocityModifiers) {
        Entity livingVictim = ((CraftEntity) epvictim).getHandle();
        Entity livingDamager = ((CraftEntity) epdamager).getHandle();
        double d0 = livingVictim.motX;
        double d1 = livingVictim.motY;
        double d2 = livingVictim.motZ;
        a(livingVictim, livingDamager);

        double inRadian = Math.PI / 180.0D;
        double angle = livingDamager.yaw * inRadian;
        livingVictim.g((-FastMath.sin(angle) * SpigotConfig.knockbackExtraHorizontal),
                SpigotConfig.knockbackExtraVertical,
                (FastMath.cos(angle) * SpigotConfig.knockbackExtraHorizontal));

        livingVictim.motX *= velocityModifiers[0];
        livingVictim.motY *= velocityModifiers[1];
        livingVictim.motZ *= velocityModifiers[2];

        livingDamager.motX *= 0.6D;
        livingDamager.motZ *= 0.6D;

        sendVectorEvent(livingVictim, d0, d1, d2);
    }

    /**
     * Send the velocity event + packets to everybody
     * This uses the default bukkit way
     * @param livingVictim - victim
     * @param d0 - vectorX
     * @param d1 - vectorY
     * @param d2 - vectorZ
     */
    private static void sendVectorEvent(Entity livingVictim, double d0, double d1, double d2) {
        livingVictim.velocityChanged = true;

        if(livingVictim instanceof Player) {
            Player player = ((Player) livingVictim);
            Vector velocity = new Vector(d0, d1, d2);

            PlayerVelocityEvent event = new PlayerVelocityEvent(player, velocity.clone());
            Bukkit.getServer().getPluginManager().callEvent(event);

            if (!velocity.equals(event.getVelocity())) {
                event.setVelocity(velocity);
            }
            if (!event.isCancelled()) {
                WrapperPlayServerEntityVelocity entityVelocity = new WrapperPlayServerEntityVelocity();
                entityVelocity.setEntityID(livingVictim.getId());
                entityVelocity.setVelocityX(livingVictim.motX);
                entityVelocity.setVelocityY(livingVictim.motY);
                entityVelocity.setVelocityZ(livingVictim.motZ);
                PacketUtil.syncSend(entityVelocity, player.getWorld().getPlayers());
                livingVictim.velocityChanged = false;
                livingVictim.motX = d0;
                livingVictim.motY = d1;
                livingVictim.motZ = d2;
            }
        }
    }

    /**
     * Base method
     * @see Damage
     * @param victim
     * @param attacker
     * @param damage
     * @param arrow
     * @param source
     * @param cause
     * @param applyKb
     */
    public static void damage(LivingEntity victim, LivingEntity attacker, double damage, Arrow arrow, DamageSource source, Cause cause, boolean applyKb) {
        if(victim.isDead() || attacker.isDead() || invincibleEntities.contains(victim)) return; //prevent bs hits from dying
        //TODO: change to our own death system (ex: spectators)
        DamageQueue.getDamages().push(new Damage(victim, attacker, damage,
                attacker.getEquipment().getItemInHand(), cause, arrow, source, applyKb));
    }

    /**
     * The methods below simplify the above method
     *
     *
     */
    //For good-ol melee
    public static void damage(LivingEntity victim, LivingEntity attacker, double damage, boolean applyKb) {
        damage(victim, attacker, damage, null, null, Cause.MELEE, applyKb);
    }
    //For arrows
    public static void damage(LivingEntity victim, LivingEntity attacker, double damage, Arrow arrow, boolean applyKb) {
        damage(victim, attacker, damage, arrow, null, Cause.PROJECTILE, applyKb);
    }

    //For everything else
    public static void damage(LivingEntity victim, LivingEntity attacker, double damage, DamageSource source, boolean applyKb) {
        damage(victim, attacker, damage, null, source, Cause.CUSTOM, applyKb);
    }
    //Just deal damage for no reason (no kb)
    public static void damage(LivingEntity victim, LivingEntity attacker, double damage) {
        damage(victim, attacker, damage, null, null, Cause.NULL, false);
    }

    /**
     * built in a method for entityliving (has something to do with velocity)
     * @param entityLiving1 - victim being knocked
     * @param entity - attacker knocking victim. Important to figuring out angles at which the victim will be knockbed
     */
    private static void a(Entity entityLiving1, Entity entity) {
        EntityLiving entityLiving;
        if(entityLiving1 instanceof EntityLiving) {
            entityLiving = (EntityLiving) entityLiving1;
        }else throw new IllegalArgumentException("entityLiving1 must be instance of " + EntityLiving.class);
        double d0 = entity.locX - entityLiving.locX;
        double d1 = entity.locZ - entityLiving.locZ;

        entityLiving.aw = (float)(FastMath.atan2(d1, d0) * 180.0D / Math.PI - (double)entityLiving.yaw);
        //entityLiving.a(entity, 0F, d0, d1);
        double magnitude = FastMath.sqrt(d0 * d0 + d1 * d1);
        entityLiving.motX /= SpigotConfig.knockbackFriction;
        entityLiving.motY /= SpigotConfig.knockbackFriction;
        entityLiving.motZ /= SpigotConfig.knockbackFriction;

        entityLiving.motX -= d0 / magnitude * SpigotConfig.knockbackHorizontal;
        entityLiving.motY += SpigotConfig.knockbackVertical;
        entityLiving.motZ -= d1 / magnitude * SpigotConfig.knockbackHorizontal;
        if (entityLiving.motY > SpigotConfig.knockbackVerticalLimit) {
            entityLiving.motY = SpigotConfig.knockbackVerticalLimit;
        }
    }
}
