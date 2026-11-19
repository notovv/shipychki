package ru.shipuchki;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.ZombieAttackGoal;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

public class AkvychEntity extends FizzNpc {
	public AkvychEntity(EntityType<? extends ZombieEntity> type, World world) {
		super(type, world, Text.literal("[ADMIN] ").formatted(Formatting.RED, Formatting.BOLD)
				.append(Text.literal("Аквыч").formatted(Formatting.WHITE)));
	}

	@Override
	protected void initFightGoals() {
		this.goalSelector.add(2, new ZombieAttackGoal(this, 1.25, false));
		this.targetSelector.add(1, new ActiveTargetGoal<>(this, BroyaEntity.class, true));
	}

	/** Админская способность: если Броя далеко — телепортируется к нему. */
	@Override
	public void tickMovement() {
		super.tickMovement();
		if (age % 40 != 0) {
			return;
		}
		if (getWorld() instanceof ServerWorld sw) {
			LivingEntity t = getTarget();
			if (t != null && squaredDistanceTo(t) > 400) {
				sw.spawnParticles(ParticleTypes.PORTAL, getX(), getY() + 1, getZ(), 30, 0.3, 0.6, 0.3, 0.1);
				requestTeleport(t.getX() + 2, t.getY(), t.getZ() + 2);
				sw.spawnParticles(ParticleTypes.PORTAL, getX(), getY() + 1, getZ(), 30, 0.3, 0.6, 0.3, 0.1);
			}
		}
	}
}
