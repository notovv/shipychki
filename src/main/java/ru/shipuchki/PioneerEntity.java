package ru.shipuchki;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.ProjectileAttackGoal;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class PioneerEntity extends FizzNpc implements RangedAttackMob {
	private static final int RING_SIZE = 4;
	private final List<ShipuchkaEntity> ring = new ArrayList<>();

	public PioneerEntity(EntityType<? extends ZombieEntity> type, World world) {
		super(type, world, Text.literal("[OWNER] ").formatted(Formatting.DARK_RED, Formatting.BOLD)
				.append(Text.literal("Пионер").formatted(Formatting.WHITE)));
	}

	@Override
	protected void initFightGoals() {
		this.goalSelector.add(1, new ProjectileAttackGoal(this, 1.1, 20, 14.0f));
		this.targetSelector.add(1, new ActiveTargetGoal<>(this, BroyaEntity.class, true));
	}

	/** Шипучки кружат над головой. */
	@Override
	public void tickMovement() {
		super.tickMovement();
		if (!(getWorld() instanceof ServerWorld sw) || !isAlive()) {
			return;
		}
		ring.removeIf(s -> !s.isAlive() || !s.isDecor());
		while (ring.size() < RING_SIZE) {
			ShipuchkaEntity s = new ShipuchkaEntity(sw, this, new ItemStack(ModItems.SHIPUCHKA));
			s.setDecor(true);
			s.setPosition(getPos());
			sw.spawnEntity(s);
			ring.add(s);
		}
		for (int i = 0; i < ring.size(); i++) {
			double ang = age * 0.12 + i * (Math.PI * 2 / RING_SIZE);
			ShipuchkaEntity s = ring.get(i);
			s.setPosition(getX() + Math.cos(ang) * 0.9,
					getY() + 2.15 + 0.15 * Math.sin(age * 0.2 + i),
					getZ() + Math.sin(ang) * 0.9);
			s.setVelocity(Vec3d.ZERO);
		}
	}

	@Override
	public void shootAt(LivingEntity target, float pullProgress) {
		if (!(getWorld() instanceof ServerWorld sw)) {
			return;
		}
		if (!ring.isEmpty()) {
			ring.remove(ring.size() - 1).launchAt(target, 1.3f);
		} else {
			ShipuchkaEntity.throwAt(sw, this, getEyePos(), target, 1.3f);
		}
	}

	@Override
	public void remove(Entity.RemovalReason reason) {
		for (ShipuchkaEntity s : ring) {
			s.discard();
		}
		ring.clear();
		super.remove(reason);
	}
}
