package codyhuh.gcm.common.entities;

import codyhuh.gcm.common.entities.goals.SummonBoogerGoal;
import codyhuh.gcm.registry.ModAnimations;
import codyhuh.gcm.registry.ModSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;


public class BoogerEater extends Monster implements GeoEntity, RangedAttackMob {
    // 1- Normal, 2- Tosser, 3- Summoner
    private static final EntityDataAccessor<Boolean> DATA_SPELL_CASTING = SynchedEntityData.defineId(BoogerEater.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_TOSSING = SynchedEntityData.defineId(BoogerEater.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_TYPE = SynchedEntityData.defineId(BoogerEater.class, EntityDataSerializers.INT);
    public int spellCastingTickCount;

    public BoogerEater(EntityType<? extends Monster> p_33002_, Level p_33003_) {
        super(p_33002_, p_33003_);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 12.0D).add(Attributes.FOLLOW_RANGE, 64.0D).add(Attributes.ATTACK_DAMAGE, 3.0D).add(Attributes.MOVEMENT_SPEED, 0.25D);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, false) {
            @Override
            public boolean canUse() {
                return ((BoogerEater)mob).getBoogerType() == 0 && super.canUse();
            }
        });
        this.goalSelector.addGoal(2, new RangedAttackGoal(this, 1.0D, 35, 35, 27.0F) {
            @Override
            public boolean canUse() {
                return ((BoogerEater)mob).getBoogerType() == 1 && super.canUse();
            }

            @Override
            public void tick() {
                super.tick();
                Minecraft.getInstance().getChatListener().handleSystemMessage(Component.literal("Attack time: " + attackTime), true);

                if (attackTime == 35) {
                    setTossing(true);
                }

                if (attackTime == 0) {
                    stop();
                }
            }

            @Override
            public void stop() {
                super.stop();
                setTossing(false);
            }
        });
        this.goalSelector.addGoal(2, new SummonBoogerGoal(this) {
            @Override
            public boolean canUse() {
                return mob.getBoogerType() == 2 && super.canUse();
            }
        });
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(0, new AvoidEntityGoal<>(this, Player.class, 6.0F, 1.35, 1.6) {
            @Override
            public boolean canUse() {
                return ((BoogerEater)mob).getBoogerType() > 0 && super.canUse();
            }
        });
        this.goalSelector.addGoal(2, new RandomStrollGoal(this, 0.8D, 15));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 6.0F));

        this.targetSelector.addGoal(0, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    public void setIsCastingSpell(boolean p_33728_) {
        this.entityData.set(DATA_SPELL_CASTING, p_33728_);
    }

    @Override
    public void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_TYPE, 0);
        this.entityData.define(DATA_SPELL_CASTING, false);
        this.entityData.define(DATA_TOSSING, false);
    }

    public boolean isTossing() {
        return this.entityData.get(DATA_TOSSING);
    }

    private void setTossing(boolean tossing) {
        this.entityData.set(DATA_TOSSING, tossing);
    }

    public int getBoogerType() {
        return this.entityData.get(DATA_TYPE);
    }

    private void setType(int type) {
        this.entityData.set(DATA_TYPE, type);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("Type", getBoogerType());
        compound.putInt("SpellTicks", this.spellCastingTickCount);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        setType(compound.getInt("Type"));
        this.spellCastingTickCount = compound.getInt("SpellTicks");
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @javax.annotation.Nullable SpawnGroupData spawnDataIn, @javax.annotation.Nullable CompoundTag dataTag) {
        spawnDataIn = super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
        if (dataTag == null) {
            setType(random.nextInt(3));
        }
        else {
            if (dataTag.contains("Type", 3)){
                this.setType(dataTag.getInt("Type"));
            }
        }

        return spawnDataIn;
    }

    @Override
    public void tick() {
        super.tick();

/*        if (!isCustomNameVisible()) setCustomNameVisible(true);

        if (tickCount == 1 && !level().isClientSide()) {
            if (getBoogerType() == 0) {
                setCustomName(Component.literal("Normal"));
            } else if (getBoogerType() == 1) {
                setCustomName(Component.literal("Tosser"));
            } else if (getBoogerType() == 2) {
                setCustomName(Component.literal("Summoner"));
            }
        }*/
    }

    protected SoundEvent getDeathSound() {
        return ModSounds.BOOGER_EATER_DEATH.get();
    }

    protected SoundEvent getHurtSound(DamageSource p_28281_) {
        return ModSounds.BOOGER_EATER_HURT.get();
    }

    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        return ModSounds.BOOGER_EATER_IDLE.get();
    }

    @Override
    protected float getSoundVolume() {
        return 0.6F;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<GeoEntity>(this, "controller", 2, this::predicate));
        controllerRegistrar.add(new AnimationController<GeoEntity>(this, "controller1", 2, this::toss));
    }

    private <E extends GeoEntity> PlayState toss(AnimationState<E> event) {
        if (!isAddedToWorld()) {
            return PlayState.STOP;
        }

        if (isTossing()) {
            event.setAnimation(RawAnimation.begin().thenLoop("throw"));
            return PlayState.CONTINUE;
        }
        return PlayState.STOP;
    }

    private <E extends GeoEntity> PlayState predicate(AnimationState<E> event) {
        if (!isAddedToWorld()) {
            return PlayState.STOP;
        }

        if (event.isMoving()) {
            event.setAnimation(ModAnimations.WALK);
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

    @Override
    public void performRangedAttack(LivingEntity target, float f) {
        TossedBooger boogie = new TossedBooger(level(), this.getX(), this.getY() + 0.8D, this.getZ());

        double d0 = target.getX() - this.getX();
        double d1 = target.getY(0.3333333333333333) - boogie.getY();
        double d2 = target.getZ() - this.getZ();
        double d3 = Math.sqrt(d0 * d0 + d2 * d2);

        boogie.shoot(d0, d1 + d3 * 0.20000000298023224, d2, 1.6F, (float)(14 - this.level().getDifficulty().getId() * 4));

        this.playSound(SoundEvents.SLIME_SQUISH_SMALL, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        this.level().addFreshEntity(boogie);

    }
}
