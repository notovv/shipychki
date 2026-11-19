package ru.shipuchki;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ShipuchkaEntity extends ThrownItemEntity {
	/** true = шипучка просто висит/кружит над головой Пионера и ничего не делает. */
	private boolean decor = false;

	public ShipuchkaEntity(EntityType<? extends ShipuchkaEntity> type, World world) {
		super(type, world);
	}

	public ShipuchkaEntity(World world, LivingEntity owner, ItemStack stack) {
		super(ModEntities.SHIPUCHKA, owner, world, stack);
	}

	public boolean isDecor() {
		return decor;
	}

	public void setDecor(boolean d) {
		this.decor = d;
		setNoGravity(d);
	}

	@Override
	protected Item getDefaultItem() {
		return ModItems.SHIPUCHKA;
	}

	@Override
	public boolean shouldSave() {
		return !decor;
	}

	/** Запустить кружащую шипучку в цель. */
	public void launchAt(LivingEntity target, float speed) {
		setDecor(false);
		aim(getPos(), target, speed);
	}

	private void aim(Vec3d from, LivingEntity target, float speed) {
		double dx = target.getX() - from.x;
		double dy = target.getBodyY(0.33) - from.y;
		double dz = target.getZ() - from.z;
		double h = Math.sqrt(dx * dx + dz * dz);
		setVelocity(dx, dy + h * 0.2, dz, speed, 3.0f);
	}

	/** Новая шипучка летит в цель (Броя, а у Пионера — если кольцо пустое). */
	public static void throwAt(ServerWorld w, LivingEntity owner, Vec3d from, LivingEntity target, float speed) {
		ShipuchkaEntity e = new ShipuchkaEntity(w, owner, new ItemStack(ModItems.SHIPUCHKA));
		e.setPosition(from);
		e.aim(from, target, speed);
		w.spawnEntity(e);
		w.playSound(null, from.x, from.y, from.z, SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.HOSTILE, 0.7f, 1.0f);
	}

	// Шипучка не сталкивается с сущностями, а взрывается сама рядом с врагом (см. tick). Игроков не задевает.
	@Override
	protected boolean canHit(Entity entity) {
		return false;
	}

	@Override
	public void tick() {
		super.tick();
		if (decor || !(getWorld() instanceof ServerWorld sw) || !isAlive()) {
			return;
		}
		if (age > 200) {
			discard();
			return;
		}
		Entity owner = getOwner();
		if (owner != null && !sw.getEntitiesByClass(FizzNpc.class, getBoundingBox().expand(0.7),
				n -> FizzNpc.enemies(owner, n)).isEmpty()) {
			boom(sw);
			discard();
		}
	}

	@Override
	protected void onCollision(HitResult hit) {
		if (decor) {
			return;
		}
		super.onCollision(hit);
		if (getWorld() instanceof ServerWorld sw) {
			boom(sw);
			discard();
		}
	}

	private void boom(ServerWorld sw) {
		Entity owner = getOwner();
		Box box = getBoundingBox().expand(2.5);
		for (FizzNpc n : sw.getEntitiesByClass(FizzNpc.class, box, e -> FizzNpc.enemies(owner, e))) {
			n.damage(sw, sw.getDamageSources().thrown(this, owner), 6.0f);
			n.addStatusEffect(new StatusEffectInstance(StatusEffects.LEVITATION, 20, 0));
		}
		sw.spawnParticles(ParticleTypes.FIREWORK, getX(), getY(), getZ(), 30, 0.5, 0.5, 0.5, 0.15);
		sw.spawnParticles(ParticleTypes.BUBBLE_POP, getX(), getY(), getZ(), 25, 0.6, 0.6, 0.6, 0.05);
		sw.playSound(null, getX(), getY(), getZ(), SoundEvents.ENTITY_FIREWORK_ROCKET_BLAST,
				SoundCategory.NEUTRAL, 1.0f, 1.4f);
	}
}
