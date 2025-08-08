package dev.sygii.variantapi.variants.feature;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.sygii.variantapi.VariantAPI;
import dev.sygii.variantapi.variants.VariantFeature;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

import java.util.*;

public class CustomSoundsFeature extends VariantFeature {
    public static Identifier ID = VariantAPI.id("custom_sounds");
    //private Map<Identifier, CustomSound> soundMap = new HashMap<>();
    private CustomSoundRecord record;

    public CustomSoundsFeature(JsonObject data) {
        super(ID);

        Map<Identifier, CustomSound> soundMap = new HashMap<>();
        for (JsonElement elem : data.getAsJsonArray("sound_map")) {
            JsonObject obj = elem.getAsJsonObject();

            Identifier from = Identifier.tryParse(obj.get("from").getAsString());
            Identifier to = Identifier.tryParse(obj.get("to").getAsString());

            float volume = 5.0f;
            if (obj.has("volume")) {
                volume = obj.get("volume").getAsFloat();
            }

            float pitch = 5.0f;
            if (obj.has("pitch")) {
                pitch = obj.get("pitch").getAsFloat();
            }

            soundMap.put(from, new CustomSound(SoundEvent.of(to), volume, pitch));
        }

        this.record = new CustomSoundRecord(soundMap);
    }

    public CustomSoundsFeature(CustomSoundRecord record) {
        super(ID);

        this.record = record;
    }

    public Map<Identifier, CustomSound> getSoundMap() {
        return this.record.sounds;
    }

    public record CustomSound(SoundEvent event, float volume, float pitch) {}

    @Override
    public void serialize(PacketByteBuf buf) {
        this.record.write(buf);
    }

    public static CustomSoundsFeature deserialize(PacketByteBuf buf) {
        return new CustomSoundsFeature(CustomSoundRecord.read(buf));
    }

    public record CustomSoundRecord(Map<Identifier, CustomSound> sounds) {

        public void write(PacketByteBuf buf) {
            buf.writeInt(sounds().size());

            for (Identifier id : sounds().keySet()) {
                buf.writeIdentifier(id);
                buf.writeIdentifier(sounds().get(id).event.getId());
                buf.writeFloat(sounds().get(id).volume);
                buf.writeFloat(sounds().get(id).pitch);
            }
        }

        public static CustomSoundRecord read(PacketByteBuf buf) {
            Map<Identifier, CustomSound> soundsMap = new HashMap<>();
            int size = buf.readInt();
            for (int i = 0; i < size; i++) {
                Identifier id = buf.readIdentifier();
                Identifier id2 = buf.readIdentifier();
                float volume = buf.readFloat();
                float pitch = buf.readFloat();
                soundsMap.put(id, new CustomSound(SoundEvent.of(id2), volume, pitch));
            }
            return new CustomSoundRecord(soundsMap);
        }

    }
}
