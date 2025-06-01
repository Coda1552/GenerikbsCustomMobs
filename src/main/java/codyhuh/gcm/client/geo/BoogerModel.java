package codyhuh.gcm.client.geo;

import codyhuh.gcm.GenerikbsCustomMobs;
import codyhuh.gcm.common.entities.Booger;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class BoogerModel<T extends Booger> extends GeoModel<T> {

    @Override
    public ResourceLocation getAnimationResource(T animatable) {
        return new ResourceLocation(GenerikbsCustomMobs.MOD_ID, "animations/entity/booger/" + getSize(animatable, 0));
    }

    @Override
    public ResourceLocation getModelResource(T animatable) {
        return new ResourceLocation(GenerikbsCustomMobs.MOD_ID, "geo/entity/booger/" + getSize(animatable, 1));
    }

    @Override
    public ResourceLocation getTextureResource(T animatable) {
        return new ResourceLocation(GenerikbsCustomMobs.MOD_ID, "textures/entity/booger_head.png"/* + getSize(animatable, 2)*/);
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
