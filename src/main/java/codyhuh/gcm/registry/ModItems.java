package codyhuh.gcm.registry;

import codyhuh.gcm.GenerikbsCustomMobs;
import net.minecraft.world.item.*;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModItems {
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, GenerikbsCustomMobs.MOD_ID);

	public static final RegistryObject<ForgeSpawnEggItem> BOOGER_EATER_SPAWN_EGG = ITEMS.register("booger_eater_spawn_egg", () -> new ForgeSpawnEggItem(ModEntities.BOOGER_EATER, 0x9ef442, 0xfffcf7, new Item.Properties()));
	public static final RegistryObject<ForgeSpawnEggItem> BOOGER_SPAWN_EGG = ITEMS.register("booger_spawn_egg", () -> new ForgeSpawnEggItem(ModEntities.BOOGER, 0x9ef442, 0x1e252a, new Item.Properties()));
}
