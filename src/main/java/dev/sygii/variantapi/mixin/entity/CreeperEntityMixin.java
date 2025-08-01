package dev.sygii.variantapi.mixin.entity;

import com.llamalad7.mixinextras.sugar.Local;
import dev.sygii.variantapi.VariantAPI;
import dev.sygii.variantapi.acess.EntityAccess;
import dev.sygii.variantapi.variants.feature.CustomSoundsFeature;
import dev.sygii.variantapi.variants.feature.server.ExplosionRadiusFeature;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.passive.PassiveEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(CreeperEntity.class)
public abstract class CreeperEntityMixin {

	CreeperEntity entity = ((CreeperEntity)(Object)this);

	@ModifyArg(
			method = "explode",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;createExplosion(Lnet/minecraft/entity/Entity;DDDFLnet/minecraft/world/World$ExplosionSourceType;)Lnet/minecraft/world/explosion/Explosion;"),
			index = 4
	)
	private float onCreateChild(float power, @Local float f) {
		if ((((EntityAccess)entity).getVariant().getFeatures().containsKey(ExplosionRadiusFeature.ID))) {
			return ((ExplosionRadiusFeature)(((EntityAccess)entity).getVariant().getFeatures().get(ExplosionRadiusFeature.ID))).getRadius() * f;
		}
		return power * f;
	}
}
