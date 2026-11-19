package ru.shipuchki;

import net.fabricmc.api.ModInitializer;

public class ShipuchkiMod implements ModInitializer {
	public static final String MOD_ID = "shipuchki";

	@Override
	public void onInitialize() {
		ModEntities.register();
		ModItems.register();
		Spawner.register();
	}
}
