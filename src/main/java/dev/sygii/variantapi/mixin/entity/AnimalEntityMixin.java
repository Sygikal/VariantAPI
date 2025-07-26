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
		/*SheepEntity child = ci.getReturnValue();

		MobVariant variant = Variants.getChildVariant(EntityType.SHEEP, world, ((SheepEntity)(Object)this), entity);

		// Determine horn colour
		NbtCompound nbtParent1 = new NbtCompound();
		((SheepEntity)(Object)this).writeCustomDataToNbt(nbtParent1);
		NbtCompound nbtParent2 = new NbtCompound();
		entity.writeCustomDataToNbt(nbtParent2);

		String colour = "";
		if (nbtParent1.contains(MoreMobVariants.SHEEP_HORN_COLOUR_NBT_KEY)
				&& !nbtParent1.getString(MoreMobVariants.SHEEP_HORN_COLOUR_NBT_KEY).isEmpty()
				&& nbtParent2.contains(MoreMobVariants.SHEEP_HORN_COLOUR_NBT_KEY)
				&& !nbtParent2.getString(MoreMobVariants.SHEEP_HORN_COLOUR_NBT_KEY).isEmpty()
				&& entity.getRandom().nextDouble() <= SheepHornSettings.getInheritChance()) {
			colour = entity.getRandom().nextBoolean() ? nbtParent1.getString(MoreMobVariants.SHEEP_HORN_COLOUR_NBT_KEY) : nbtParent2.getString(MoreMobVariants.SHEEP_HORN_COLOUR_NBT_KEY);
		} else {
			SheepHornSettings.SheepHornColour col = SheepHornSettings.getRandomSheepHornColour(entity.getRandom(), world.getBiome(((SheepEntity)(Object)this).getBlockPos()));
			if (col != null) {
				colour = col.getId();
			}
		}

		// Write variant to child's NBT
		NbtCompound childNbt = new NbtCompound();
		child.writeNbt(childNbt);
		childNbt.putString(MoreMobVariants.NBT_KEY, variant.getIdentifier().toString());
		childNbt.putString(MoreMobVariants.SHEEP_HORN_COLOUR_NBT_KEY, colour);
		child.readCustomDataFromNbt(childNbt);
		return passiveEntity;*/
        PassiveEntity ent = original.call(instance, serverWorld, passiveEntity);
		((EntityAccess)ent).variantapi$setParents(entity, passiveEntity);
		((EntityAccess)ent).setVariant(VariantAPI.getRandomVariant(ent, ent.getType()));
		((EntityAccess)ent).setVariantOverlays(VariantAPI.getOverlays(ent, ent.getType()));
		return ent;
	}
}
