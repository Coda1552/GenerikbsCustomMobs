package codyhuh.gcm.client;

import codyhuh.gcm.GenerikbsCustomMobs;
import codyhuh.gcm.client.geo.BoogerRenderer;
import codyhuh.gcm.client.geo.ModGeoRenderer;
import codyhuh.gcm.client.geo.TossedBoogerRenderer;
import codyhuh.gcm.registry.ModEntities;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

@Mod.EventBusSubscriber(modid = GenerikbsCustomMobs.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEvents {

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers e) {
        e.registerEntityRenderer(ModEntities.BOOGER_EATER.get(), (ctx) -> new ModGeoRenderer<>(ctx, () -> new DefaultedEntityGeoModel<>(new ResourceLocation(GenerikbsCustomMobs.MOD_ID, "booger_eater"), true)));
        e.registerEntityRenderer(ModEntities.TOSSED_BOOGER.get(), TossedBoogerRenderer::new);
        e.registerEntityRenderer(ModEntities.BOOGER.get(), BoogerRenderer::new);
        e.registerEntityRenderer(ModEntities.PECKER_FIGHTER.get(), (ctx) -> new ModGeoRenderer<>(ctx, () -> new DefaultedEntityGeoModel<>(new ResourceLocation(GenerikbsCustomMobs.MOD_ID, "pecker_fighter"), true)));
    }
}
