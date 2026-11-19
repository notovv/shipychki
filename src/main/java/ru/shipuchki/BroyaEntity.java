package ru.shipuchki;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.ProjectileAttackGoal;
import net.minecraft.entity.mob.RangedAttackMob;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

public class BroyaEntity extends FizzNpc implements RangedAttackMob {
	public BroyaEntity(EntityType<? extends ZombieEntity> type, World world) {
		super(type, world, Text.literal("Броя").formatted(Formatting.AQUA));
	}

	@Override
	protected void initFightGoals() {
		this.goalSelector.add(1, new ProjectileAttackGoal(this, 1.0, 25, 14.0f));
		this.targetSelector.add(1, new ActiveTargetGoal<>(this, AkvychEntity.class, true));
		this.targetSelector.add(2, new ActiveTargetGoal<>(this, PioneerEntity.class, true));
	}

	@Override
	public void shootAt(LivingEntity target, float pullProgress) {
		if (getWorld() instanceof ServerWorld sw) {
			ShipuchkaEntity.throwAt(sw, this, getEyePos(), target, 1.3f);
		}
	}
}
