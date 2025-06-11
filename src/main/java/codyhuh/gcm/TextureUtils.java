package codyhuh.gcm;

import codyhuh.gcm.common.entities.Booger;
import com.google.common.hash.Hashing;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

import javax.annotation.Nullable;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Some code adapted from https://github.com/GoryMoon/PlayerMobs

public class TextureUtils {

    private static final Map<String, ResourceLocation> SKIN_RESOURCE_CACHE = new Object2ObjectOpenHashMap<>();
    public static final ResourceLocation HIDE_FEATURE = new ResourceLocation(GenerikbsCustomMobs.MOD_ID, "hide_feature");

    public static ResourceLocation getPlayerSkin(Booger entity) {
        String lowerName = entity.getUsername().getSkinName().toLowerCase(Locale.ROOT);

        // Check for custom overridden skins
        ResourceLocation location = SKIN_RESOURCE_CACHE.get(lowerName);
        if (location != null) {
            return location;
        }

        return getTexture(entity).orElse(DefaultPlayerSkin.getDefaultSkin());
    }

    private static Optional<ResourceLocation> getTexture(Booger entity) {
        if (entity.isTextureAvailable(MinecraftProfileTexture.Type.SKIN)) {
            return Optional.of(entity.getTexture(MinecraftProfileTexture.Type.SKIN));
        }

        GameProfile profile = entity.getProfile();
        if (profile != null && !profile.isComplete()) {
            return getDefault(profile, MinecraftProfileTexture.Type.SKIN);
        }

        if (profile != null && profile.getName() != null) {
            Minecraft mc = Minecraft.getInstance();
            Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map = mc.getSkinManager().getInsecureSkinInformation(profile);
            if (map.containsKey(MinecraftProfileTexture.Type.SKIN)) {
                MinecraftProfileTexture profileTexture = map.get(MinecraftProfileTexture.Type.SKIN);
                // Use sha1 as that is how minecraft hashes and stores the skins
                String s = Hashing.sha1().hashUnencodedChars(profileTexture.getHash()).toString();
                ResourceLocation location = SkinManager.getTextureLocation(MinecraftProfileTexture.Type.SKIN, s);
                if (mc.textureManager.getTexture(location, MissingTextureAtlasSprite.getTexture()) != MissingTextureAtlasSprite.getTexture()) {
                    return Optional.of(location);
                } else {
                    RenderSystem.recordRenderCall(() -> mc.getSkinManager().registerTexture(profileTexture, MinecraftProfileTexture.Type.SKIN, entity.getSkinCallback()));
                }
            }

        }
        return getDefault(profile, MinecraftProfileTexture.Type.SKIN);
    }

    private static Optional<ResourceLocation> getDefault(@Nullable GameProfile profile, MinecraftProfileTexture.Type type) {
        if (type == MinecraftProfileTexture.Type.CAPE || type == MinecraftProfileTexture.Type.ELYTRA) {
            return Optional.empty();
        } else {
            return Optional.of(profile != null && profile.isComplete() ? DefaultPlayerSkin.getDefaultSkin(profile.getId()) : DefaultPlayerSkin.getDefaultSkin());
        }
    }

    public static void onResourceManagerReload(ResourceManager resourceManager) {
        SKIN_RESOURCE_CACHE.clear();

        var skins = resourceManager.listResources("skins", resourceLocation -> resourceLocation.getNamespace().equals(GenerikbsCustomMobs.MOD_ID) && resourceLocation.getPath().endsWith(".png"));
        Pattern skinPattern = Pattern.compile("skins/([a-z0-9_.-]*).png");

        for (ResourceLocation location : skins.keySet()) {
            Matcher matcher = skinPattern.matcher(location.getPath());
            if (matcher.find()) {
                String name = matcher.group(1);
                SKIN_RESOURCE_CACHE.put(name, location);
            }
        }
    }

    private static void parseHideableTexture(ResourceManager resourceManager, String type, Map<String, ResourceLocation> cache) {
        cache.clear();
        Pattern pattern = Pattern.compile(type + "/([a-z0-9_.-]*).(png|txt)");

        var resources = resourceManager.listResources(type, resourceLocation -> resourceLocation.getNamespace().equals(GenerikbsCustomMobs.MOD_ID) &&
                (resourceLocation.getPath().endsWith(".png") || resourceLocation.getPath().endsWith(".txt")));

        for (ResourceLocation location : resources.keySet()) {
            Matcher matcher = pattern.matcher(location.getPath());
            if (matcher.find()) {
                String name = matcher.group(1);
                boolean ignore = Objects.equals(matcher.group(2), "txt");
                cache.put(name, ignore ? HIDE_FEATURE : location);
            }
        }
    }

    public static ResourceManagerReloadListener resourceManagerReloadListener() {
        return TextureUtils::onResourceManagerReload;
    }
}