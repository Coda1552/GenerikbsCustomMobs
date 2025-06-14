package codyhuh.gcm.client.geo;

import codyhuh.gcm.GenerikbsCustomMobs;
import codyhuh.gcm.common.entities.Booger;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

import java.util.Map;

public class BoogerModel<T extends Booger> extends GeoModel<T> {

    @Override
    public ResourceLocation getAnimationResource(T animatable) {
        return new ResourceLocation(GenerikbsCustomMobs.MOD_ID, "animations/entity/booger/" + getSize(animatable, 0));
    }

    @Override
    public ResourceLocation getModelResource(T animatable) {
        return new ResourceLocation(GenerikbsCustomMobs.MOD_ID, "geo/entity/booger/head.geo.json"/* + getSize(animatable, 1)*/);
    }

    @Override
    public ResourceLocation getTextureResource(T animatable) {
        return animatable.getGameProfile()
                .map(this::getSkin)
                .orElseGet(() -> {
                    return new ResourceLocation(GenerikbsCustomMobs.MOD_ID, "textures/entity/booger_head.png");
                });
    }

    private ResourceLocation getSkin(GameProfile gameProfile) {
        if (!gameProfile.isComplete()) {
            return new ResourceLocation(GenerikbsCustomMobs.MOD_ID, "textures/entity/booger_head.png");
        } else {
            final Minecraft minecraft = Minecraft.getInstance();
            SkinManager skinManager = minecraft.getSkinManager();
            final Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> loadSkinFromCache = skinManager.getInsecureSkinInformation(gameProfile); // returned map may or may not be typed
            if (loadSkinFromCache.containsKey(MinecraftProfileTexture.Type.SKIN)) {
                return skinManager.registerTexture(loadSkinFromCache.get(MinecraftProfileTexture.Type.SKIN), MinecraftProfileTexture.Type.SKIN);
            } else {
                //Miniatures.LOG.error("STEVE: Returning default skin for " + gameProfile);
                return DefaultPlayerSkin.getDefaultSkin(gameProfile.getId());
            }
        }
    }

    @Override
    public RenderType getRenderType(T animatable, ResourceLocation texture) {
        return RenderType.entityTranslucent(texture);
    }

    private String getSize(T e, int i) {
        String s = "";
        if (i == 0) {
            s = ".animation.json";
        }
        if (i == 1) {
            s = ".geo.json";
        }
        if (i == 2) {
            s = ".png";
        }

        return switch (e.getSize()) {
            case 0 -> "0" + s;
            case 1 -> "1" + s;
            default -> "2" + s;
        };
    }
}
