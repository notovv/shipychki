package ru.shipuchki;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

/** Служебный предмет: нужен только чтобы рисовать летящую шипучку. В креативе/крафте/лоте его нет. */
public class ModItems {
	public static Item SHIPUCHKA;

	public static void register() {
		RegistryKey<Item> k = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(ShipuchkiMod.MOD_ID, "shipuchka"));
		SHIPUCHKA = Registry.register(Registries.ITEM, k, new Item(new Item.Settings().registryKey(k)));
	}
}
