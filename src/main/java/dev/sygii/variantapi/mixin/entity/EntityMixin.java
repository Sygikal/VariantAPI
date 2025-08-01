package dev.sygii.variantapi.mixin.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.sygii.variantapi.VariantAPI;
import dev.sygii.variantapi.acess.EntityAccess;
import dev.sygii.variantapi.acess.SoundEventAccess;
import dev.sygii.variantapi.variants.Variant;
import dev.sygii.variantapi.variants.feature.CustomSoundsFeature;
import dev.sygii.variantapi.variants.feature.DisplayNameFeature;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import oshi.util.tuples.Pair;

import java.util.ArrayList;

@Mixin(Entity.class)
public abstract class EntityMixin implements EntityAccess {
	@Unique
	private Variant variant = VariantAPI.getDefaultVariant();
	@Unique
	private ArrayList<Variant> variantOverlays = new ArrayList<>();
	@Unique
	private Pair<PassiveEntity, PassiveEntity> parents;
	@Unique
	private boolean breedingHandled;

	@Override
	public Variant getVariant() {
		return this.variant;
	}

	@Override
	public void setVariant(Variant variant) {
		this.variant = variant;
	}

	@Override
	public ArrayList<Variant> getVariantOverlays() {
		return this.variantOverlays;
	}

	@Override
	public void addVariantOverlay(Variant variant) {
		this.variantOverlays.add(variant);
	}

	@Override
	public void setVariantOverlays(ArrayList<Variant> variants) {
		this.variantOverlays = variants;
	}

	@Override
	public Pair<PassiveEntity, PassiveEntity> variantapi$getParents() {
		return this.parents;
	}

	@Override
	public void variantapi$setParents(PassiveEntity father, PassiveEntity mother) {
		this.parents = new Pair<>(father, mother);
	}

	@Override
	public boolean isBreedingHandled() {
		return this.breedingHandled;
	}

	@Override
	public void setBreedingHandled(boolean handled) {
		this.breedingHandled = handled;
	}

	@Inject(method = "getDefaultName", at = @At(value = "RETURN"), cancellable = true)
	protected void getVariantName(CallbackInfoReturnable<Text> cir) {
		if (variant.getFeatures().containsKey(DisplayNameFeature.ID)) {
			cir.setReturnValue(((DisplayNameFeature)variant.getFeatures().get(DisplayNameFeature.ID)).getDisplayName());
		}
	}

	@WrapOperation(method = "playSound", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;playSound(Lnet/minecraft/entity/player/PlayerEntity;DDDLnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FF)V"))
	protected void modifySound(World instance, PlayerEntity except, double x, double y, double z, SoundEvent sound, SoundCategory category, float volume, float pitch, Operation<Void> original) {
		if (variant.getFeatures().containsKey(CustomSoundsFeature.ID)) {
			if (((CustomSoundsFeature)variant.getFeatures().get(CustomSoundsFeature.ID)).getSoundMap().containsKey(sound.getId())) {
				CustomSoundsFeature.CustomSound newSound = ((CustomSoundsFeature)variant.getFeatures().get(CustomSoundsFeature.ID)).getSoundMap().get(sound.getId());
				//SoundEvent newSound = sound;
				//((SoundEventAccess)newSound).setId(((CustomSoundsFeature)variant.getFeatures().get(CustomSoundsFeature.ID)).getSoundMap().get(sound.getId()));
				//System.out.println(newSound.pitch() + " " + pitch);
				original.call(instance, except, x, y, z, newSound.event(), category, newSound.volume() == 5.0f ? volume : newSound.volume(), newSound.pitch() == 5.0f ? pitch : newSound.pitch());
			}
		}else {
			original.call(instance, except, x, y, z, sound, category, volume, pitch);
		}
	}
}
