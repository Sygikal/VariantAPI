package dev.sygii.variantapi.mixin.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.sygii.variantapi.VariantAPI;
import dev.sygii.variantapi.acess.EntityAccess;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;

@Debug(export = true)
@Mixin(AnimalEntity.class)
public abstract class AnimalEntityMixin {

	AnimalEntity entity = ((AnimalEntity)(Object)this);

	@WrapOperation(
			method = "breed(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/passive/AnimalEntity;)V",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/passive/AnimalEntity;createChild(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/passive/PassiveEntity;)Lnet/minecraft/entity/passive/PassiveEntity;")
	)
	private PassiveEntity onCreateChild(AnimalEntity instance, ServerWorld serverWorld, PassiveEntity passiveEntity, Operation<PassiveEntity> original) {
        PassiveEntity ent = original.call(instance, serverWorld, passiveEntity);
		((EntityAccess)ent).variantapi$setParents(entity, passiveEntity);
		//((EntityAccess)ent).setVariant(VariantAPI.getRandomVariant(ent));
		//((EntityAccess)ent).setVariantOverlays(VariantAPI.getOverlays(ent));
		VariantAPI.rollRandomVariants(ent);
		return ent;
	}
}
