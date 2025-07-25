package codyhuh.gcm.registry;

import codyhuh.gcm.GenerikbsCustomMobs;
import com.mojang.authlib.GameProfile;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Optional;

public class ModSerializers {
    public static final DeferredRegister<EntityDataSerializer<?>> SERIALIZERS = DeferredRegister.create(ForgeRegistries.Keys.ENTITY_DATA_SERIALIZERS, GenerikbsCustomMobs.MOD_ID);

    public static final RegistryObject<EntityDataSerializer<Optional<GameProfile>>> OPTIONAL_GAME_PROFILE = SERIALIZERS.register("game_profile", GameProfileSerializer::new);

    public static class GameProfileSerializer implements EntityDataSerializer<Optional<GameProfile>> {
        public void write(FriendlyByteBuf friendlyByteBuf, Optional<GameProfile> optionalGameProfile) {
            friendlyByteBuf.writeBoolean(optionalGameProfile.isPresent());
            if (optionalGameProfile.isPresent()) {
                friendlyByteBuf.writeNbt(NbtUtils.writeGameProfile(new CompoundTag(), optionalGameProfile.get()));
            }
        }

        public Optional<GameProfile> read(FriendlyByteBuf friendlyByteBuf) {
            return !friendlyByteBuf.readBoolean() ? Optional.empty() : Optional.of(NbtUtils.readGameProfile(friendlyByteBuf.readNbt()));
        }

        public Optional<GameProfile> copy(Optional<GameProfile> optionalGameProfile) {
            return optionalGameProfile;
        }
    }
}