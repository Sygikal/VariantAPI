package dev.sygii.variantapi.mixin.entity.enderman;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.sygii.variantapi.acess.EntityAccess;
import dev.sygii.variantapi.variants.feature.CustomSoundsFeature;
import dev.sygii.variantapi.variants.feature.server.WaterImmuneFeature;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Debug(export = true)
@Mixin(EndermanEntity.class)
public abstract class EndermanEntityMixin {

	EndermanEntity entity = ((EndermanEntity)(Object)this);


	@WrapOperation(method = "playAngrySound", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;playSound(DDDLnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FFZ)V"))
	protected void modifyAngrySound(World instance, double x, double y, double z, SoundEvent sound, SoundCategory category, float volume, float pitch, boolean useDistance, Operation<Void> original) {

		if (((EntityAccess)entity).getVariant().getFeatures().containsKey(CustomSoundsFeature.ID)) {
			if (((CustomSoundsFeature)((EntityAccess)entity).getVariant().getFeatures().get(CustomSoundsFeature.ID)).getSoundMap().containsKey(sound.getId())) {
				CustomSoundsFeature.CustomSound newSound = ((CustomSoundsFeature)((EntityAccess)entity).getVariant().getFeatures().get(CustomSoundsFeature.ID)).getSoundMap().get(sound.getId());
				original.call(instance, x, y, z, newSound.event(), category, newSound.volume() == 5.0f ? volume : newSound.volume(), newSound.pitch() == 5.0f ? pitch : newSound.pitch(), useDistance);
			}
		}else {
			original.call(instance, x, y, z, sound, category, volume, pitch, useDistance);
		}
	}

	@WrapOperation(method = "teleportTo(DDD)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;playSound(Lnet/minecraft/entity/player/PlayerEntity;DDDLnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FF)V"))
	protected void modifyTeleportSound(World instance, PlayerEntity except, double x, double y, double z, SoundEvent sound, SoundCategory category, float volume, float pitch, Operation<Void> original) {

		if (((EntityAccess)entity).getVariant().getFeatures().containsKey(CustomSoundsFeature.ID)) {
			if (((CustomSoundsFeature)((EntityAccess)entity).getVariant().getFeatures().get(CustomSoundsFeature.ID)).getSoundMap().containsKey(sound.getId())) {
				CustomSoundsFeature.CustomSound newSound = ((CustomSoundsFeature)((EntityAccess)entity).getVariant().getFeatures().get(CustomSoundsFeature.ID)).getSoundMap().get(sound.getId());
				original.call(instance, except, x, y, z, newSound.event(), category, newSound.volume() == 5.0f ? volume : newSound.volume(), newSound.pitch() == 5.0f ? pitch : newSound.pitch());
			}
		}else {
			original.call(instance, except, x, y, z, sound, category, volume, pitch);
		}
	}

	@Inject(
			method = "hurtByWater",
			at = @At(value = "RETURN"),
			cancellable = true
	)
	private void onCreateChild(CallbackInfoReturnable<Boolean> cir) {
		if ((((EntityAccess)entity).getVariant().getFeatures().containsKey(WaterImmuneFeature.ID))) {
			cir.setReturnValue(false);
		}
	}
}
