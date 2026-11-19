package ru.shipuchki;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class ModEntities {
	public static EntityType<ShipuchkaEntity> SHIPUCHKA;
	public static EntityType<BroyaEntity> BROYA;
	public static EntityType<AkvychEntity> AKVYCH;
	public static EntityType<PioneerEntity> PIONEER;

	private static RegistryKey<EntityType<?>> key(String name) {
		return RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(ShipuchkiMod.MOD_ID, name));
	}

	public static void register() {
		RegistryKey<EntityType<?>> k = key("shipuchka");
		SHIPUCHKA = Registry.register(Registries.ENTITY_TYPE, k,
				EntityType.Builder.<ShipuchkaEntity>create(ShipuchkaEntity::new, SpawnGroup.MISC)
						.dimensions(0.25f, 0.25f).maxTrackingRange(8).trackingTickInterval(1).build(k));

		k = key("broya");
		BROYA = Registry.register(Registries.ENTITY_TYPE, k,
				EntityType.Builder.<BroyaEntity>create(BroyaEntity::new, SpawnGroup.MONSTER)
						.dimensions(0.6f, 1.95f).maxTrackingRange(10).build(k));

		k = key("akvych");
		AKVYCH = Registry.register(Registries.ENTITY_TYPE, k,
				EntityType.Builder.<AkvychEntity>create(AkvychEntity::new, SpawnGroup.MONSTER)
						.dimensions(0.6f, 1.95f).maxTrackingRange(10).build(k));

		k = key("pioneer");
		PIONEER = Registry.register(Registries.ENTITY_TYPE, k,
				EntityType.Builder.<PioneerEntity>create(PioneerEntity::new, SpawnGroup.MONSTER)
						.dimensions(0.6f, 1.95f).maxTrackingRange(10).build(k));

		FabricDefaultAttributeRegistry.register(BROYA, FizzNpc.attributes(150, 5));
		FabricDefaultAttributeRegistry.register(AKVYCH, FizzNpc.attributes(250, 8));
		FabricDefaultAttributeRegistry.register(PIONEER, FizzNpc.attributes(200, 4));
	}
}
