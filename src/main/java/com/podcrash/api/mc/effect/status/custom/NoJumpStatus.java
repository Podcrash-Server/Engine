package com.podcrash.api.mc.effect.status.custom;

import com.podcrash.api.mc.effect.status.Status;
import com.podcrash.api.mc.time.resources.TimeResource;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class NoJumpStatus extends CustomStatus {
    private final PotionEffect jump = new PotionEffect(PotionEffectType.JUMP, 5, 128, true);
    public NoJumpStatus(Player player) {
        super(player, Status.NOJUMP);

    }

    @Override
    protected void doWhileAffected() {
        new TimeResource() {
            @Override
            public void task() {
                if (!getPlayer().hasPotionEffect(PotionEffectType.JUMP)) getPlayer().addPotionEffect(jump);
            }

            @Override
            public boolean cancel() {
                return true;
            }

            @Override
            public void cleanup() {

            }
        }.delaySync(1);
    }

    @Override
    protected boolean isInflicted() {
        return getApplier().isNoJump();
    }

    @Override
    protected void removeEffect() {
        getApplier().removeNoJump();
    }
}
