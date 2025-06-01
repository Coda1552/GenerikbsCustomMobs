package codyhuh.gcm.client.geo;

import codyhuh.gcm.client.geo.layers.BoogerHeadRenderLayer;
import codyhuh.gcm.common.entities.Booger;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BoogerRenderer<T extends Booger> extends ModGeoRenderer<T> {

    public BoogerRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new BoogerModel<>());
        addRenderLayer(new BoogerHeadRenderLayer<>(this));
    }

    @Override
    public void render(T animatable, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn) {
        poseStack.pushPose();
        float f = 1.0F;
        if (animatable.getSize() <= 1) {
            f = 0.45F;
        }
        if (animatable.getSize() >= 2) {
            f = animatable.getSize() * 0.6F;
        }
        poseStack.scale(f, f, f);

        super.render(animatable, entityYaw, partialTicks, poseStack, bufferIn, packedLightIn);
        poseStack.popPose();
    }
}
