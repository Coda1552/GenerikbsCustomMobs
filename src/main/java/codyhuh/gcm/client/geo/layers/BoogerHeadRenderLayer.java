package codyhuh.gcm.client.geo.layers;

import codyhuh.gcm.GenerikbsCustomMobs;
import codyhuh.gcm.common.entities.Booger;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.core.UUIDUtil;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

import java.util.Map;

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

    private ResourceLocation getSkin(T e) {
        GameProfile gp = e.getOwnerProfile();
        if (e.hasCustomName() && gp != null) {
            Minecraft minecraft = Minecraft.getInstance();
            Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map = minecraft.getSkinManager().getInsecureSkinInformation(gp);
            return map.containsKey(MinecraftProfileTexture.Type.SKIN) ? minecraft.getSkinManager().registerTexture(map.get(MinecraftProfileTexture.Type.SKIN), MinecraftProfileTexture.Type.SKIN) : DefaultPlayerSkin.getDefaultSkin(UUIDUtil.getOrCreatePlayerUUID(gp));
        }
        else {
            return new ResourceLocation(GenerikbsCustomMobs.MOD_ID,"textures/entity/booger_head.png");
        }
    }
}
