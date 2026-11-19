package ru.shipuchki;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.entity.mob.ZombieEntity;

public class ShipuchkiClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		EntityRendererRegistry.register(ModEntities.SHIPUCHKA, FlyingItemEntityRenderer::new);
		EntityRendererRegistry.<ZombieEntity>register(ModEntities.BROYA, ctx -> new NpcRenderer(ctx, "broya"));
		EntityRendererRegistry.<ZombieEntity>register(ModEntities.AKVYCH, ctx -> new NpcRenderer(ctx, "akvych"));
		EntityRendererRegistry.<ZombieEntity>register(ModEntities.PIONEER, ctx -> new NpcRenderer(ctx, "pioneer"));
	}
}
