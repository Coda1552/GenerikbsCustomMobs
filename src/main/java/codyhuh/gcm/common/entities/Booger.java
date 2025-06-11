package codyhuh.gcm.common.entities;

import codyhuh.gcm.NameManager;
import codyhuh.gcm.PlayerName;
import codyhuh.gcm.ProfileUpdater;
import codyhuh.gcm.registry.ModAnimations;
import codyhuh.gcm.registry.ModEntities;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringUtil;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;

import java.util.Objects;
import java.util.Optional;

import static com.mojang.authlib.minecraft.MinecraftProfileTexture.Type.ELYTRA;

public class Booger extends Slime implements GeoEntity {

    public Booger(EntityType<? extends Slime> p_33588_, Level p_33589_) {
        super(p_33588_, p_33589_);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 10.0D).add(Attributes.FOLLOW_RANGE, 32.0D).add(Attributes.ATTACK_DAMAGE, 1.0D).add(Attributes.MOVEMENT_SPEED, 0.25D);
    }


    private static final EntityDataAccessor<String> NAME = SynchedEntityData.defineId(Booger.class, EntityDataSerializers.STRING);
    @Nullable
    private GameProfile profile;
    @Nullable
    private ResourceLocation skin;
    private boolean skinAvailable;

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        getEntityData().define(NAME, "");
    }

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor p_33601_, DifficultyInstance p_33602_, MobSpawnType p_33603_, @org.jetbrains.annotations.Nullable SpawnGroupData p_33604_, @org.jetbrains.annotations.Nullable CompoundTag p_33605_) {

        if (!hasUsername())
            setUsername(NameManager.INSTANCE.getRandomName());

        return super.finalizeSpawn(p_33601_, p_33602_, p_33603_, p_33604_, p_33605_);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if (getCustomName() != null && getCustomName().getString().isEmpty())
            compound.remove("CustomName");

        String username = getUsername().getCombinedNames();
        if (!StringUtil.isNullOrEmpty(username))
            compound.putString("Username", username);

        if (profile != null && profile.isComplete())
            compound.put("Profile", NbtUtils.writeGameProfile(new CompoundTag(), profile));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        String username = compound.getString("Username");
        if (!StringUtil.isNullOrEmpty(username)) {
            setUsername(username);
        } else {
            setUsername(NameManager.INSTANCE.getRandomName());
        }

        if (compound.contains("Profile", Tag.TAG_COMPOUND)) {
            profile = NbtUtils.readGameProfile(compound.getCompound("Profile"));
        }
    }

    @Override
    public Component getCustomName() {
        Component customName = super.getCustomName();
        String displayName = getUsername().getDisplayName();
        return customName != null && !customName.getString().isEmpty() ? customName : !StringUtil.isNullOrEmpty(displayName) ? Component.literal(displayName) : null;
    }

    @Override
    public boolean hasCustomName() {
        return super.hasCustomName() || !StringUtil.isNullOrEmpty(getUsername().getDisplayName());
    }


    @Nullable
    public GameProfile getProfile() {
        if (profile == null && hasUsername()) {
            profile = new GameProfile(null, getUsername().getSkinName());
            ProfileUpdater.updateProfile(this);
        }
        return profile;
    }

    public void setProfile(@Nullable GameProfile profile) {
        this.profile = profile;
    }

    public boolean hasUsername() {
        return !StringUtil.isNullOrEmpty(getEntityData().get(NAME));
    }

    public PlayerName getUsername() {
        if (!hasUsername() && !level().isClientSide()) {
            setUsername(NameManager.INSTANCE.getRandomName());
        }
        return new PlayerName(getEntityData().get(NAME));
    }

    public void setUsername(String username) {
        PlayerName playerName = new PlayerName(username);
        if (playerName.noDisplayName()) {
            Optional<PlayerName> name = NameManager.INSTANCE.findName(username);
            if (name.isPresent())
                playerName = name.get();
        }
        NameManager.INSTANCE.useName(playerName);
        setUsername(playerName);
    }

    public void setUsername(PlayerName name) {
        PlayerName oldName = hasUsername() ? getUsername(): null;
        getEntityData().set(NAME, name.getCombinedNames());

        if (!Objects.equals(oldName, name)) {
            setProfile(null);
            getProfile();
        }
    }

    @OnlyIn(Dist.CLIENT)
    public SkinManager.SkinTextureCallback getSkinCallback() {
        return (type, location, profileTexture) -> {
            switch (type) {
                case SKIN -> {
                    skin = location;
                    skinAvailable = true;
                }
            }
        };
    }

    @OnlyIn(Dist.CLIENT)
    public boolean isTextureAvailable(MinecraftProfileTexture.Type type) {
        return skinAvailable;
    }

    @SuppressWarnings("ConstantConditions")
    @OnlyIn(Dist.CLIENT)
    public ResourceLocation getTexture(MinecraftProfileTexture.Type type) {
        return skin;
    }


    ///// VVV /////

    @Override
    public void travel(Vec3 p_21280_) {
        if (jumping) {
            setZza(1.0F);
        }
        super.travel(p_21280_);
    }

    @Override
    public EntityType<? extends Slime> getType() {
        return ModEntities.BOOGER.get();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<GeoEntity>(this, "controller", 2, this::predicate));

    }

    private <E extends GeoEntity> PlayState predicate(AnimationState<E> event) {
        if (!isAddedToWorld()) {
            return PlayState.STOP;
        }

        if (event.isMoving()) {
            event.setAnimation(ModAnimations.JUMP);
        }
        else {
            event.setAnimation(ModAnimations.IDLE);
        }

        return PlayState.CONTINUE;
    }

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
