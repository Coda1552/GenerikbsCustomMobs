package codyhuh.gcm;

import codyhuh.gcm.common.entities.Booger;
import codyhuh.gcm.registry.ModEntities;
import codyhuh.gcm.registry.ModItems;
import codyhuh.gcm.registry.ModSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.server.Services;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(GenerikbsCustomMobs.MOD_ID)
public class GenerikbsCustomMobs {
    public static final String MOD_ID = "gcm";

    public GenerikbsCustomMobs() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forgeBus = MinecraftForge.EVENT_BUS;

        ModItems.ITEMS.register(bus);
        ModEntities.ENTITIES.register(bus);
        ModSounds.SOUND_EVENTS.register(bus);

        forgeBus.register(this);

        forgeBus.addListener(this::levelUnloadEvent);
        forgeBus.addListener(this::levelLoadEvent);
    }

    private void levelLoadEvent(LevelEvent.Load e) {
        Services services = Services.create(Minecraft.getInstance().authenticationService, Minecraft.getInstance().gameDirectory);
        Booger.setup(services, Minecraft.getInstance());
    }

    private void levelUnloadEvent(LevelEvent.Unload e) {
        if (!Minecraft.getInstance().isLocalServer) {
            Services services = Services.create(Minecraft.getInstance().authenticationService, Minecraft.getInstance().gameDirectory);
            Booger.setup(services, Minecraft.getInstance());
        }
    }
}
