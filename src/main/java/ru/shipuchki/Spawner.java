package ru.shipuchki;

import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;

/** Следит, чтобы рядом с игроками в мире бродили Броя, Аквыч и Пионер. */
public class Spawner {
	private static final int CHECK_RADIUS = 128;

	public static void register() {
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			if (server.getTicks() % 200 != 0) {
				return;
			}
			ServerWorld w = server.getOverworld();
			for (ServerPlayerEntity p : w.getPlayers()) {
				ensureNear(w, p, false);
			}
		});

		// /shipuchki spawn — принудительно призвать всю троицу рядом (только для операторов)
		CommandRegistrationCallback.EVENT.register((dispatcher, access, env) -> dispatcher.register(
				CommandManager.literal("shipuchki")
						.requires(s -> s.hasPermissionLevel(2))
						.then(CommandManager.literal("spawn").executes(Spawner::cmdSpawn))));
	}

	private static int cmdSpawn(CommandContext<ServerCommandSource> ctx) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
		ServerPlayerEntity p = ctx.getSource().getPlayerOrThrow();
		ensureNear(p.getServerWorld(), p, true);
		return 1;
	}

	private static void ensureNear(ServerWorld w, ServerPlayerEntity p, boolean force) {
		boolean broya = force || !has(w, p, BroyaEntity.class);
		boolean akvych = force || !has(w, p, AkvychEntity.class);
		boolean pioneer = force || !has(w, p, PioneerEntity.class);
		if (!broya && !akvych && !pioneer) {
			return;
		}
		double a = w.random.nextDouble() * Math.PI * 2;
		double dist = force ? 12 : 35;
		double cx = p.getX() + Math.cos(a) * dist;
		double cz = p.getZ() + Math.sin(a) * dist;
		if (broya) spawnAt(ModEntities.BROYA, w, cx + 6, cz);
		if (akvych) spawnAt(ModEntities.AKVYCH, w, cx - 6, cz + 3);
		if (pioneer) spawnAt(ModEntities.PIONEER, w, cx - 6, cz - 3);
	}

	private static boolean has(ServerWorld w, ServerPlayerEntity p, Class<? extends Entity> cls) {
		return !w.getEntitiesByClass(cls, p.getBoundingBox().expand(CHECK_RADIUS), Entity::isAlive).isEmpty();
	}

	private static <T extends MobEntity> void spawnAt(EntityType<T> type, ServerWorld w, double x, double z) {
		int y = w.getTopY(Heightmap.Type.MOTION_BLOCKING, (int) x, (int) z);
		type.spawn(w, new BlockPos((int) x, y, (int) z), SpawnReason.EVENT);
	}
}
