package dev.sygii.variantapi.mixin.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.sygii.variantapi.acess.EntityAccess;
import dev.sygii.variantapi.variants.feature.server.WaterAffinityFeature;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

	LivingEntity entity = ((LivingEntity)(Object)this);

	@WrapOperation(
			method = "tickMovement",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;hurtByWater()Z")
	)
	private boolean onCreateChild(LivingEntity instance, Operation<Boolean> original) {
		if (((EntityAccess)entity).getVariant().getFeatures().containsKey(WaterAffinityFeature.ID)) {
			return ((WaterAffinityFeature)((EntityAccess)entity).getVariant().getFeatures().get(WaterAffinityFeature.ID)).isSensitiveToWater();
		}
		return original.call(instance);
	}
}
