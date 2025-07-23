package dev.sygii.variantapi.acess;

import dev.sygii.variantapi.variants.Variant;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;

import java.util.ArrayList;

public interface EntityAccess {
    Variant getVariant();
    void setVariant(Variant variant);

    ArrayList<Variant> getVariantOverlays();
    void addVariantOverlay(Variant variant);
    void setVariantOverlays(ArrayList<Variant> variants);
}
