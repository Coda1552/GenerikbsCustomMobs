package codyhuh.gcm;

import codyhuh.gcm.common.entities.Booger;
import codyhuh.gcm.registry.ModEntities;
import codyhuh.gcm.registry.ModItems;
import codyhuh.gcm.registry.ModSerializers;
import codyhuh.gcm.registry.ModSounds;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.players.GameProfileCache;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
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
        ModSerializers.SERIALIZERS.register(bus);

        MinecraftForge.EVENT_BUS.addListener(this::onServerAboutToStart);

        forgeBus.register(this);
    }


    public void onServerAboutToStart(ServerAboutToStartEvent event) {
        MinecraftServer server = event.getServer();
        Booger.setProfileCache(server.getProfileCache());
        Booger.setSessionService(server.getSessionService());
        GameProfileCache.setUsesAuthentication(server.usesAuthentication());
    }
}

