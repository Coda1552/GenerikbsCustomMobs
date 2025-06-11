package codyhuh.gcm;

import codyhuh.gcm.common.entities.Booger;
import net.minecraft.world.level.block.entity.SkullBlockEntity;

import javax.annotation.Nullable;
import java.util.ArrayDeque;
import java.util.Queue;

public class ProfileUpdater {

    private static final Queue<Booger> entities = new ArrayDeque<>();
    @Nullable
    private static Thread thread;

    public static void updateProfile(Booger entity) {
        entities.add(entity);

        if (thread == null || thread.getState() == Thread.State.TERMINATED) {
            thread = new Thread(() -> {
                while (!entities.isEmpty()) {
                    Booger mob = entities.remove();
                    if (mob != null) {
                        SkullBlockEntity.updateGameprofile(mob.getProfile(), mob::setProfile);
                    }
                }
            });
            thread.start();
        }
    }

}