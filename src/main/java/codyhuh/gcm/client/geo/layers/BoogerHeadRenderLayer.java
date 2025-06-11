package codyhuh.gcm.client.geo.layers;

import codyhuh.gcm.GenerikbsCustomMobs;
import codyhuh.gcm.common.entities.Booger;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class BoogerHeadRenderLayer<T extends Booger & GeoAnimatable> extends GeoRenderLayer<T> {

    public BoogerHeadRenderLayer(GeoRenderer<T> entityRendererIn) {
        super(entityRendererIn);
    }

    @Override
    public void render(PoseStack poseStack, T animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        int size = 0;

        for (int i = 0; i <= 2; i++) {
            size = i;
        }

        ResourceLocation modelLoc = new ResourceLocation(GenerikbsCustomMobs.MOD_ID, "geo/entity/booger/" + size + ".geo.json");
        RenderType boogerRenderType = RenderType.entityTranslucent(new ResourceLocation(GenerikbsCustomMobs.MOD_ID, "textures/entity/booger/" + size + ".png"));

        VertexConsumer vertexConsumer = bufferSource.getBuffer(boogerRenderType);

        this.getRenderer().reRender(this.getGeoModel().getBakedModel(modelLoc), poseStack, bufferSource, animatable, boogerRenderType, vertexConsumer, partialTick, packedLight, packedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
    }
}
