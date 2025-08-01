package dev.sygii.variantapi.mixin.access;

import dev.sygii.variantapi.acess.SoundEventAccess;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.QuadrupedEntityModel;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SoundEvent.class)
public class SoundEventMixin implements SoundEventAccess {

    @Shadow @Final private boolean staticDistance;

    @Shadow @Final private float distanceToTravel;

    @Mutable
    @Shadow @Final private Identifier id;

    @Override
    public void setId(Identifier id) {
        this.id = id;
    }

    @Override
    public boolean isStaticDistance() {
        return this.staticDistance;
    }

    @Override
    public float getDistanceTT() {
        return this.distanceToTravel;
    }
}
