package dev.sygii.variantapi.acess;

import dev.sygii.variantapi.variants.Variant;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.PassiveEntity;
import org.spongepowered.asm.mixin.Mixin;
import oshi.util.tuples.Pair;

import java.util.ArrayList;

public interface EntityAccess {
    Variant getVariant();
    void setVariant(Variant variant);

    ArrayList<Variant> getVariantOverlays();
    void addVariantOverlay(Variant variant);
    void setVariantOverlays(ArrayList<Variant> variants);

    Pair<PassiveEntity, PassiveEntity> variantapi$getParents();
    void variantapi$setParents(PassiveEntity father, PassiveEntity mother);

    boolean isBreedingHandled();
    void setBreedingHandled(boolean handled);
}
