package codyhuh.gcm.common.entities.goals;

import codyhuh.gcm.common.entities.Booger;
import codyhuh.gcm.common.entities.BoogerEater;
import codyhuh.gcm.registry.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.monster.Vex;

import javax.annotation.Nullable;

public class SummonBoogerGoal extends Goal {
    protected BoogerEater mob;
    protected int attackWarmupDelay;
    protected int nextAttackTickCount;

    protected SummonBoogerGoal(BoogerEater mob) {
        this.mob = mob;
    }

    public boolean canUse() {
        LivingEntity livingentity = mob.getTarget();
        if (livingentity != null && livingentity.isAlive()) {
            if (mob.isAggressive()) {
                return false;
            }
            else {
                int i = mob.level().getNearbyEntities(Vex.class, TargetingConditions.DEFAULT, mob, mob.getBoundingBox().inflate(16.0D)).size();

                return mob.getRandom().nextInt(8) + 1 > i && mob.tickCount >= this.nextAttackTickCount;
            }
        } else {
            return false;
        }
    }

    public boolean canContinueToUse() {
        LivingEntity livingentity = mob.getTarget();
        return livingentity != null && livingentity.isAlive() && this.attackWarmupDelay > 0;
    }

    public void start() {
        this.attackWarmupDelay = this.adjustedTickDelay(this.getCastWarmupTime());
        mob.spellCastingTickCount = this.getCastingTime();
        this.nextAttackTickCount = mob.tickCount + this.getCastingInterval();
        SoundEvent soundevent = this.getSpellPrepareSound();
        if (soundevent != null) {
            mob.playSound(soundevent, 1.0F, 1.0F);
        }

        mob.setIsCastingSpell(true);
    }

    public void tick() {
        --this.attackWarmupDelay;
        if (this.attackWarmupDelay == 0) {
            this.performSpellCasting();
            mob.playSound(SoundEvents.EVOKER_PREPARE_SUMMON, 1.0F, 1.0F);
        }

    }

    protected void performSpellCasting() {
        ServerLevel serverlevel = (ServerLevel) mob.level();

        for(int i = 0; i < 2; ++i) {
            BlockPos blockpos = mob.blockPosition().offset(-2 + mob.getRandom().nextInt(5), 1, -2 + mob.getRandom().nextInt(5));
            Booger slime = ModEntities.BOOGER.get().create(mob.level());
            if (slime != null) {
                slime.setSize(1, false);
                slime.moveTo(blockpos, 0.0F, 0.0F);
                slime.finalizeSpawn(serverlevel, mob.level().getCurrentDifficultyAt(blockpos), MobSpawnType.MOB_SUMMONED, null, null);
                serverlevel.addFreshEntityWithPassengers(slime);
            }
        }
    }

    protected int getCastWarmupTime() {
        return 20;
    }

    protected int getCastingTime() {
        return 100;
    }

    protected int getCastingInterval() {
        return 340;
    }

    @Nullable
    protected SoundEvent getSpellPrepareSound() {
        return SoundEvents.EVOKER_PREPARE_SUMMON;
    }
}