package codyhuh.gcm.common.entities;

import codyhuh.gcm.GenerikbsCustomMobs;
import codyhuh.gcm.registry.ModAnimations;
import codyhuh.gcm.registry.ModEntities;
import codyhuh.gcm.registry.ModSerializers;
import com.google.common.collect.Iterables;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.Property;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.util.Mth;
import net.minecraft.util.StringUtil;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;

import java.util.Optional;
import java.util.UUID;

public class Booger extends Slime implements GeoEntity {
    private static final EntityDataAccessor<Optional<GameProfile>> GAMEPROFILE = SynchedEntityData.defineId(Booger.class, ModSerializers.OPTIONAL_GAME_PROFILE.get());
    private static final EntityDataAccessor<Integer> ID_SIZE = SynchedEntityData.defineId(Booger.class, EntityDataSerializers.INT);;

    private static GameProfileCache profileCache;
    private static MinecraftSessionService sessionService;

    private String owner;
    private UUID ownerId;

    public Booger(EntityType<? extends Slime> p_33588_, Level p_33589_) {
        super(p_33588_, p_33589_);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 10.0D).add(Attributes.FOLLOW_RANGE, 32.0D).add(Attributes.ATTACK_DAMAGE, 1.0D).add(Attributes.MOVEMENT_SPEED, 0.25D);
    }

    public void remove(Entity.RemovalReason p_149847_) {
        int i = this.getSize();
        if (!this.level().isClientSide && i > 1 && this.isDeadOrDying()) {
            Component component = this.getCustomName();
            boolean flag = this.isNoAi();
            float f = (float)i / 4.0F;
            int j = i / 2;
            int k = 2 + this.random.nextInt(3);

            for(int l = 0; l < k; ++l) {
                float f1 = ((float)(l % 2) - 0.5F) * f;
                float f2 = ((float)(l / 2) - 0.5F) * f;
                Booger booger = (Booger)this.getType().create(this.level());
                if (booger != null) {
                    if (this.isPersistenceRequired()) {
                        booger.setPersistenceRequired();
                    }

                    booger.setCustomName(component);
                    booger.setNoAi(flag);
                    booger.setGameProfile(owner);
                    booger.setInvulnerable(this.isInvulnerable());
                    booger.setSize(j, true);
                    booger.moveTo(this.getX() + (double)f1, this.getY() + 0.5, this.getZ() + (double)f2, this.random.nextFloat() * 360.0F, 0.0F);
                    this.level().addFreshEntity(booger);
                }
            }
        }

        this.brain.clearMemories();
        this.setRemoved(p_149847_);
        this.invalidateCaps();
    }

    @Nullable
    public static GameProfile updateGameProfile(@Nullable GameProfile input) {
        if (input != null && !StringUtil.isNullOrEmpty(input.getName())) {
            //if (NullProfileCache.isCachedNull(input.getName(), null)) {
            //    return input;
            //}

            if (input.isComplete() && input.getProperties().containsKey("textures")) {
                return input;
            } else if (profileCache != null && sessionService != null) {
                Optional<GameProfile> gameprofile = profileCache.get(input.getName());
                if (!gameprofile.isPresent()) {
                    //NullProfileCache.cacheNull(input.getName(), input.getId());
                    return input;
                } else {
                    GameProfile profile = gameprofile.get();
                    Property property = Iterables.getFirst(profile.getProperties().get("textures"), null);
                    if (property == null) {
                        //Miniatures.LOG.info("Refilling cache for gameprofile: " + profile);
                        profile = sessionService.fillProfileProperties(profile, true);
                    }

                    //if (!profile.isComplete()) {
                    //    NullProfileCache.cacheNull(profile.getName(), profile.getId());
                    //}

                    return profile;
                }
            } else {
                return input;
            }
        } else {
            return input;
        }
    }

    public static void setProfileCache(GameProfileCache profileCache) {
        Booger.profileCache = profileCache;
    }

    public static void setSessionService(MinecraftSessionService sessionService) {
        Booger.sessionService = sessionService;
    }

    public void setSize(int p_33594_, boolean p_33595_) {
        int i = Mth.clamp(p_33594_, 1, 127);
        this.entityData.set(ID_SIZE, i);
        this.reapplyPosition();
        this.refreshDimensions();
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue((double)(i * i));
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue((double)(0.2F + 0.1F * (float)i));
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue((double)i);
        if (p_33595_) {
            this.setHealth(this.getMaxHealth());
        }

        this.xpReward = i;
    }

    public int getSize() {
        return this.entityData.get(ID_SIZE);
    }

    public EntityDimensions getDimensions(Pose p_33597_) {
        return super.getDimensions(p_33597_).scale(0.5F * (float)this.getSize());
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(GAMEPROFILE, Optional.empty());
        this.entityData.define(ID_SIZE, 1);
    }

    public Optional<GameProfile> getGameProfile() {
        return entityData.get(GAMEPROFILE);
    }

    public void setGameProfile(String name) {
        setGameProfile(new GameProfile(null, name));
    }

    public void setGameProfile(UUID id) {
        setGameProfile(new GameProfile(id, null));
    }

    public void setGameProfile(GameProfile playerProfile) {
        //if (NullProfileCache.isCachedNull(playerProfile.getName(), playerProfile.getId())) {
        //    return;
        //}

        GameProfile profile = updateGameProfile(playerProfile);
        if (profile != null) {
            setGameProfileInternal(profile);
        } else {
            setGameProfileInternal(null);
            //NullProfileCache.cacheNull(playerProfile.getName(), playerProfile.getId());
        }
    }

    protected void setGameProfileInternal(GameProfile playerProfile) {
        if (playerProfile != null) {
            owner = playerProfile.getName();
            ownerId = playerProfile.getId();
            entityData.set(GAMEPROFILE, Optional.of(playerProfile));
            setCustomName(Component.literal(playerProfile.getName()));
        } else {
            entityData.set(GAMEPROFILE, Optional.empty());
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);

        if (owner != null) {
            compound.putString("owner", owner);
        }
        if (ownerId != null) {
            compound.putUUID("OwnerUUID", ownerId);
        }

        getGameProfile().ifPresent(profile -> {
            compound.put("gameProfile", NbtUtils.writeGameProfile(new CompoundTag(), profile));

            if (owner == null) {
                if (profile.getName() != null) {
                    compound.putString("owner", profile.getName());
                }
            }
            if (ownerId == null) {
                if (profile.getId() != null) {
                    compound.putUUID("OwnerUUID", profile.getId());
                } else {
                    //GenerikbsCustomMobs.LOG.warning("GameProfile has no UUID: " + profile);
                }
            }
        });

        compound.putInt("Size", this.getSize() - 1);
    }

    public void readAdditionalSaveData(CompoundTag p_33607_) {
        this.setSize(p_33607_.getInt("Size") + 1, false);
        super.readAdditionalSaveData(p_33607_);
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);

        GameProfile incomingProfile = null;
        String incomingOwner = null;
        UUID incomingUuid = null;

        if (compound.contains("gameProfile")) {
            incomingProfile = NbtUtils.readGameProfile(compound.getCompound("gameProfile"));
        }

        if (incomingProfile == null) {
            if (compound.contains("owner", Tag.TAG_STRING)) {
                incomingOwner = compound.getString("owner");
                incomingProfile = new GameProfile(null, incomingOwner);
            } else if (compound.hasUUID("OwnerUUID")) {
                incomingUuid = compound.getUUID("OwnerUUID");
                incomingProfile = new GameProfile(incomingUuid, null);
            }
        }

        GameProfile currentProfile = getGameProfile().orElse(null);

        if (incomingProfile != null && currentProfile != null && ((incomingProfile.getId() != null && !incomingProfile.getId().equals(currentProfile.getId())) || (incomingProfile.getName() != null && !incomingProfile.getName().equals(currentProfile.getName())))) {
            setGameProfile(incomingProfile);
        } else if (incomingProfile != null && currentProfile == null) {
            setGameProfile(incomingProfile);
        }
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        if (GAMEPROFILE.equals(key) && this.level().isClientSide()) {
            this.getGameProfile().ifPresent(gameprofile -> {
                if (gameprofile.isComplete()) {
                    Minecraft.getInstance().getSkinManager().registerSkins(gameprofile, (textureType, textureLocation, profileTexture) -> {}, true);
                }
            });
        }

        if (ID_SIZE.equals(key)) {
            this.refreshDimensions();
            this.setYRot(this.yHeadRot);
            this.yBodyRot = this.yHeadRot;
            if (this.isInWater() && this.random.nextInt(20) == 0) {
                this.doWaterSplashEffect();
            }
        }
    }

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
