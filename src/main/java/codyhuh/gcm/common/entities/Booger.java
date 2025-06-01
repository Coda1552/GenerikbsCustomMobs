package codyhuh.gcm.common.entities;

import codyhuh.gcm.registry.ModAnimations;
import codyhuh.gcm.registry.ModEntities;
import com.google.common.collect.Iterables;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.Property;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.Services;
import net.minecraft.server.players.GameProfileCache;
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
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

public class Booger extends Slime implements GeoEntity {
    @Nullable
    private static GameProfileCache profileCache;
    @Nullable
    private static MinecraftSessionService sessionService;
    @Nullable
    private static Executor mainThreadExecutor;
    @Nullable
    private GameProfile owner;

    public Booger(EntityType<? extends Slime> p_33588_, Level p_33589_) {
        super(p_33588_, p_33589_);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 10.0D).add(Attributes.FOLLOW_RANGE, 32.0D).add(Attributes.ATTACK_DAMAGE, 1.0D).add(Attributes.MOVEMENT_SPEED, 0.25D);
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

    public static void setup(Services p_222886_, Executor p_222887_) {
        profileCache = p_222886_.profileCache();
        sessionService = p_222886_.sessionService();
        mainThreadExecutor = p_222887_;
    }

    public static void clear() {
        profileCache = null;
        sessionService = null;
        mainThreadExecutor = null;
    }

    @Nullable
    public GameProfile getOwnerProfile() {
        return this.owner;
    }

    @Override
    public void readAdditionalSaveData(CompoundTag p_155745_) {
        super.readAdditionalSaveData(p_155745_);
        if (p_155745_.contains("SkullOwner", 10)) {
            this.setOwner(NbtUtils.readGameProfile(p_155745_.getCompound("SkullOwner")));
        }
        else if (p_155745_.contains("ExtraType", 8)) {
            String $$1 = p_155745_.getString("ExtraType");
            if (!StringUtil.isNullOrEmpty($$1)) {
                this.setOwner(new GameProfile(null, $$1));
            }
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag p_33619_) {
        super.addAdditionalSaveData(p_33619_);
        if (this.owner != null) {
            CompoundTag $$1 = new CompoundTag();
            NbtUtils.writeGameProfile($$1, this.owner);
            p_33619_.put("SkullOwner", $$1);
        }
    }

    public void setOwner(@Nullable GameProfile p_59770_) {
        synchronized(this) {
            this.owner = p_59770_;
        }

        this.updateOwnerProfile();
    }

    private void updateOwnerProfile() {
        updateGameprofile(this.owner, (p_155747_) -> {
            this.owner = p_155747_;
        });
    }

    public static void updateGameprofile(@Nullable GameProfile p_155739_, Consumer<GameProfile> p_155740_) {
        if (p_155739_ != null && !StringUtil.isNullOrEmpty(p_155739_.getName()) && (!p_155739_.isComplete() || !p_155739_.getProperties().containsKey("textures")) && profileCache != null && sessionService != null) {
            profileCache.getAsync(p_155739_.getName(), (p_182470_) -> {
                Util.backgroundExecutor().execute(() -> {
                    Util.ifElse(p_182470_, (p_276255_) -> {
                        Property $$2 = (Property) Iterables.getFirst(p_276255_.getProperties().get("textures"), (Object)null);
                        if ($$2 == null) {
                            MinecraftSessionService $$3 = sessionService;
                            if ($$3 == null) {
                                return;
                            }

                            p_276255_ = $$3.fillProfileProperties(p_276255_, true);
                        }

                        GameProfile $$4 = p_276255_;
                        Executor $$5 = mainThreadExecutor;
                        if ($$5 != null) {
                            $$5.execute(() -> {
                                GameProfileCache $$6 = profileCache;
                                if ($$6 != null) {
                                    $$6.add($$4);
                                    p_155740_.accept($$4);
                                }

                            });
                        }

                    }, () -> {
                        Executor $$2 = mainThreadExecutor;
                        if ($$2 != null) {
                            $$2.execute(() -> {
                                p_155740_.accept(p_155739_);
                            });
                        }

                    });
                });
            });
        } else {
            p_155740_.accept(p_155739_);
        }
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
