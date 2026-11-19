package ru.shipuchki;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

/**
 * Общая база: бродят по миру, дерутся друг с другом, игрок их не трогает и не может
 * ни ударить, ни толкнуть, ни использовать на них предметы.
 */
public abstract class FizzNpc extends ZombieEntity {
	protected FizzNpc(EntityType<? extends ZombieEntity> type, World world, Text name) {
		super(type, world);
		setCustomName(name);
		setCustomNameVisible(true);
		setPersistent();
	}

	public static DefaultAttributeContainer.Builder attributes(double hp, double dmg) {
		return ZombieEntity.createZombieAttributes()
				.add(EntityAttributes.MAX_HEALTH, hp)
				.add(EntityAttributes.ATTACK_DAMAGE, dmg)
				.add(EntityAttributes.MOVEMENT_SPEED, 0.28)
				.add(EntityAttributes.FOLLOW_RANGE, 48.0)
				.add(EntityAttributes.SPAWN_REINFORCEMENTS, 0.0);
	}

	/** Броя против Аквыча и Пионера. Свои своих не бьют. */
	public static boolean enemies(Entity a, Entity b) {
		return a instanceof FizzNpc && b instanceof FizzNpc && (a instanceof BroyaEntity) != (b instanceof BroyaEntity);
	}

	protected abstract void initFightGoals();

	@Override
	protected void initGoals() {
		this.goalSelector.add(0, new SwimGoal(this));
		initFightGoals();
		this.goalSelector.add(5, new WanderAroundFarGoal(this, 1.0));
		this.goalSelector.add(6, new LookAtEntityGoal(this, FizzNpc.class, 10.0f));
		this.goalSelector.add(7, new LookAroundGoal(this));
	}

	// ванильные цели зомби (охота на игроков, деревни и т.д.) не нужны
	@Override
	protected void initCustomGoals() {
	}

	@Override
	protected boolean burnsInDaylight() {
		return false;
	}

	@Override
	protected boolean canConvertInWater() {
		return false;
	}

	@Override
	public void tickMovement() {
		super.tickMovement();
		if (isBaby()) {
			setBaby(false);
		}
	}

	// --- игрок не может взаимодействовать ---
	@Override
	public boolean isAttackable() {
		return false;
	}

	@Override
	public boolean isPushable() {
		return false;
	}

	@Override
	protected ActionResult interactMob(PlayerEntity player, Hand hand) {
		return ActionResult.PASS;
	}

	/** Урон проходит только от других NPC (или /kill и пустота). Умереть они не могут. */
	@Override
	public boolean damage(ServerWorld world, DamageSource source, float amount) {
		boolean allowed = source.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY)
				|| source.getAttacker() instanceof FizzNpc;
		if (!allowed) {
			return false;
		}
		boolean r = super.damage(world, source, amount);
		if (isAlive() && getHealth() < 5.0f) {
			setHealth(getMaxHealth());
		}
		return r;
	}
}
