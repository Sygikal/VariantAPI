package dev.sygii.variantapi.variants.feature;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.sygii.variantapi.VariantAPI;
import dev.sygii.variantapi.variants.VariantFeature;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CustomSoundsFeature extends VariantFeature {
    public static Identifier ID = VariantAPI.id("custom_sounds");
    private final Map<Identifier, CustomSound> soundMap = new HashMap<>();

    public CustomSoundsFeature(JsonObject data) {
        super(ID, true);

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

            this.soundMap.put(from, new CustomSound(SoundEvent.of(to), volume, pitch));
        }
    }

    public Map<Identifier, CustomSound> getSoundMap() {
        return this.soundMap;
    }

    public record CustomSound(SoundEvent event, float volume, float pitch) {}

    /*@Override
    public void serialize(PacketByteBuf buf) {
        buf.writeString(this.displayName);
    }

    public static CustomSoundsFeature deserialize(PacketByteBuf buf) {
        return new CustomSoundsFeature(buf.readString());
    }

    public Text getDisplayName() {
        return Text.translatable(this.displayName);
    }*/
}
