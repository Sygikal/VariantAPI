package dev.sygii.variantapi.mixin.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.sygii.variantapi.acess.EntityAccess;
import dev.sygii.variantapi.variants.feature.server.CustomLootFeature;
import dev.sygii.variantapi.variants.feature.server.WaterAffinityFeature;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

	LivingEntity entity = ((LivingEntity)(Object)this);

	@WrapOperation(
			method = "tickMovement",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;hurtByWater()Z")
	)
	private boolean onIsHurtByWater(LivingEntity instance, Operation<Boolean> original) {
		if (((EntityAccess)entity).getVariant().getFeatures().containsKey(WaterAffinityFeature.ID)) {
			return ((WaterAffinityFeature)((EntityAccess)entity).getVariant().getFeatures().get(WaterAffinityFeature.ID)).isSensitiveToWater();
		}
		return original.call(instance);
	}

	@Inject(method = "getLootTable", at = @At("HEAD"), cancellable = true)
	private void overrideLootTable(CallbackInfoReturnable<Identifier> cir) {
		if (((EntityAccess)entity).getVariant().getFeatures().containsKey(CustomLootFeature.ID)) {
			cir.setReturnValue(((CustomLootFeature)((EntityAccess)entity).getVariant().getFeatures().get(CustomLootFeature.ID)).getLootTableId());
		}
	}
}
