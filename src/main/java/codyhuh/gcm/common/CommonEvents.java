package codyhuh.gcm.common;

import codyhuh.gcm.GenerikbsCustomMobs;
import codyhuh.gcm.common.entities.Booger;
import codyhuh.gcm.common.entities.BoogerEater;
import codyhuh.gcm.common.entities.PeckerFighter;
import codyhuh.gcm.registry.ModEntities;
import codyhuh.gcm.registry.ModItems;
import codyhuh.gcm.registry.ModSerializers;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = GenerikbsCustomMobs.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CommonEvents {

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.BOOGER_EATER.get(), BoogerEater.createAttributes().build());
        event.put(ModEntities.BOOGER.get(), Booger.createAttributes().build());
        event.put(ModEntities.PECKER_FIGHTER.get(), PeckerFighter.createAttributes().build());
    }

    @SubscribeEvent
    public static void populateTabs(BuildCreativeModeTabContentsEvent e) {
        if (e.getTabKey().equals(CreativeModeTabs.SPAWN_EGGS)) {
            for (RegistryObject<Item> item : ModItems.ITEMS.getEntries()) {
                e.accept(item.get());
            }
        }
    }
}

