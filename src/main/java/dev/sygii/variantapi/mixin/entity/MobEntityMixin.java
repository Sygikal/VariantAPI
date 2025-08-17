package dev.sygii.variantapi.mixin.entity;

import dev.sygii.variantapi.VariantAPI;
import dev.sygii.variantapi.acess.EntityAccess;
import dev.sygii.variantapi.variants.Variant;
import dev.sygii.variantapi.variants.feature.server.DaylightImmuneFeature;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.nbt.*;
import net.minecraft.util.Identifier;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin {

	MobEntity entity = ((MobEntity)(Object)this);

	// THESE METHODS SHOULD ONLY BE CALLED BY THE SERVER
	@Inject(method = "writeCustomDataToNbt", at = @At("RETURN"))
	protected void onWriteCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
		nbt.putString(VariantAPI.VARIANT_NBT_KEY, ((EntityAccess)entity).getVariant().id().toString());

		NbtList nbtList = new NbtList();
		for (Variant var :  ((EntityAccess)entity).getVariantOverlays()) {
			nbtList.add(NbtString.of(var.id().toString()));
		}
		nbt.put(VariantAPI.VARIANT_OVERLAYS_NBT_KEY, nbtList);
	}

	@Inject(method = "readCustomDataFromNbt", at = @At("RETURN"))
	protected void onReadCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
		if (!nbt.getString(VariantAPI.VARIANT_NBT_KEY).isEmpty()/* && !nbt.getString(VariantAPI.VARIANT_NBT_KEY).equals(VariantAPI.getDefaultVariant().id().toString())*/) {
			((EntityAccess)entity).setVariant(VariantAPI.getVariant(entity.getType(), Identifier.tryParse(nbt.getString(VariantAPI.VARIANT_NBT_KEY))));
		} /*else { "attachmentsapi:attachments":[{"custom:synced":["default"]}]
			((EntityAccess)entity).setVariant(VariantAPI.getRandomVariant(entity, entity.getType(), nbt, entity.getWorld(), entity.getRandom().nextLong(), entity.getWorld().getBiome(entity.getBlockPos()), entity.getWorld().getMoonSize()));
		}*/

		if (nbt.contains(VariantAPI.VARIANT_OVERLAYS_NBT_KEY)) {
			NbtList nbtList = nbt.getList(VariantAPI.VARIANT_OVERLAYS_NBT_KEY, NbtElement.STRING_TYPE);

			((EntityAccess)entity).setVariantOverlays(new ArrayList<>());
			for (int i = 0; i < nbtList.size(); i++) {
				String variantString = nbtList.getString(i);

				Variant variant = VariantAPI.getVariantOverlay(entity.getType(), Identifier.tryParse(variantString));
				if (variant != null) {
					((EntityAccess)entity).addVariantOverlay(variant);
				}
			}
		}/* else {
			((EntityAccess)entity).setVariantOverlays(VariantAPI.getOverlays(entity, entity.getType(), nbt, entity.getWorld(), entity.getRandom().nextLong(), entity.getWorld().getBiome(entity.getBlockPos()), entity.getWorld().getMoonSize()));
			/*VariantAPI.getOverlays("empty", entity, entity.getType(), nbt, entity.getWorld(), entity.getRandom().nextLong(), entity.getWorld().getBiome(entity.getBlockPos()), entity.getWorld().getMoonSize()).forEach(k ->  {
				((EntityAccess)entity).addVariantOverlay(k);
			});/
		}*/
		VariantAPI.syncVariants(entity);
	}

	@Inject(method = "initialize", at = @At("RETURN"))
	//? if =1.20.1 {
	protected void onInitialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt, CallbackInfoReturnable<EntityData> ci) {
	//?} else {
	/*protected void onInitialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, CallbackInfoReturnable<EntityData> ci) {
	*///?}
		//((EntityAccess)entity).setVariant(VariantAPI.getRandomVariant(entity));
		//((EntityAccess)entity).setVariantOverlays(VariantAPI.getOverlays(entity));
		VariantAPI.rollRandomVariants(entity);
		VariantAPI.syncVariantAttributes(entity);
		/*StatusEffect asd = Registries.STATUS_EFFECT.get(Identifier.of("minecraft","fire_resistance"));
		int j;
		Integer seconds = -1;
		if (seconds != null) {
			if (asd.isInstant()) {
				j = seconds;
			} else if (seconds == -1) {
				j = -1;
			} else {
				j = seconds * 20;
			}
		} else if (asd.isInstant()) {
			j = 1;
		} else {
			j = 600;
		}
		StatusEffectInstance statusEffectInstance = new StatusEffectInstance(asd, j, 1, false, false);
		entity.addStatusEffect(statusEffectInstance);*/
	}

	//THIS IS CALLED ON BOTH CLIENT AND SERVER
	@Inject(method = "tick", at = @At("RETURN"))
	protected void onTick(CallbackInfo ci) { }

	@Inject(method = "isAffectedByDaylight", at = @At("HEAD"), cancellable = true)
	protected void onIsAffectedByDaylight(CallbackInfoReturnable<Boolean> cir) {
		if (((EntityAccess)entity).getVariant().getFeatures().containsKey(DaylightImmuneFeature.ID)) {
			cir.setReturnValue(false);
		}
	}
}
