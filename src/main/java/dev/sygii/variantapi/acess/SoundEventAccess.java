package dev.sygii.variantapi.acess;

import net.minecraft.util.Identifier;

public interface SoundEventAccess {
    void setId(Identifier id);

    boolean isStaticDistance();
    float getDistanceTT();
}
