package it.menzani.bts;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.time.Duration;

public class PotionEffects {
    public static PotionEffect newPotionEffect(PotionEffectType type, Duration duration, int level, ParticleType particleType) {
        if (level < 1) throw new IllegalArgumentException("level must be greater than or equal to 1.");
        return new PotionEffect(type, (int) TickDuration.convert(duration, "duration"), --level,
                particleType.ambient, particleType.particles);
    }

    public enum ParticleType {
        NORMAL(false, true),
        AMBIENT(true, true),
        OFF(false, false);

        private final boolean ambient, particles;

        ParticleType(boolean ambient, boolean particles) {
            this.ambient = ambient;
            this.particles = particles;
        }
    }
}
