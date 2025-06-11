package codyhuh.gcm.registry;

import codyhuh.gcm.GenerikbsCustomMobs;
import codyhuh.gcm.common.entities.Booger;
import codyhuh.gcm.common.entities.BoogerEater;
import codyhuh.gcm.common.entities.PeckerFighter;
import codyhuh.gcm.common.entities.TossedBooger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
	public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, GenerikbsCustomMobs.MOD_ID);

	public static final RegistryObject<EntityType<BoogerEater>> BOOGER_EATER = ENTITIES.register("booger_eater", () -> EntityType.Builder.of(BoogerEater::new, MobCategory.MONSTER).sized(0.65f, 1.0F).build(new ResourceLocation(GenerikbsCustomMobs.MOD_ID, "booger_eater").toString()));
	public static final RegistryObject<EntityType<TossedBooger>> TOSSED_BOOGER = ENTITIES.register("tossed_booger", () -> EntityType.Builder.<TossedBooger>of(TossedBooger::new, MobCategory.MISC).sized(0.25f, 0.25F).build(new ResourceLocation(GenerikbsCustomMobs.MOD_ID, "tossed_booger").toString()));
	public static final RegistryObject<EntityType<Booger>> BOOGER = ENTITIES.register("booger", () -> EntityType.Builder.of(Booger::new, MobCategory.MONSTER).sized(2.5F, 2.5F).build(new ResourceLocation(GenerikbsCustomMobs.MOD_ID, "booger").toString()));
	public static final RegistryObject<EntityType<PeckerFighter>> PECKER_FIGHTER = ENTITIES.register("pecker_fighter", () -> EntityType.Builder.of(PeckerFighter::new, MobCategory.CREATURE).sized(0.85F, 0.95F).build(new ResourceLocation(GenerikbsCustomMobs.MOD_ID, "pecker_fighter").toString()));

}



