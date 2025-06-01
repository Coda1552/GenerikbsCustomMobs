package codyhuh.gcm.registry;

import codyhuh.gcm.GenerikbsCustomMobs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, GenerikbsCustomMobs.MOD_ID);

    public static final RegistryObject<SoundEvent> BOOGER_EATER_HURT = createSoundEvent("booger_eater.hurt");
    public static final RegistryObject<SoundEvent> BOOGER_EATER_DEATH = createSoundEvent("booger_eater.death");
    public static final RegistryObject<SoundEvent> BOOGER_EATER_IDLE = createSoundEvent("booger_eater.idle");

    private static RegistryObject<SoundEvent> createSoundEvent(final String name) {
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(GenerikbsCustomMobs.MOD_ID, name)));
    }

}
