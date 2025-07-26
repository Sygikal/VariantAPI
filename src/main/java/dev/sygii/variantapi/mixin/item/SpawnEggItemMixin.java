package dev.sygii.variantapi.mixin.item;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.sygii.variantapi.acess.EntityAccess;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SpawnEggItem.class)
public abstract class SpawnEggItemMixin {

	@WrapOperation(
			method = "spawnBaby",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/passive/PassiveEntity;createChild(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/passive/PassiveEntity;)Lnet/minecraft/entity/passive/PassiveEntity;")
	)
	private PassiveEntity onCreateChild(PassiveEntity instance, ServerWorld serverWorld, PassiveEntity passiveEntity, Operation<PassiveEntity> original) {
		PassiveEntity ent = original.call(instance, serverWorld, passiveEntity);
		((EntityAccess)ent).setVariant(((EntityAccess)passiveEntity).getVariant());
		((EntityAccess)ent).setVariantOverlays(((EntityAccess)passiveEntity).getVariantOverlays());
		return ent;
	}
}
