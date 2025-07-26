package dev.sygii.variantapi.mixin.entity;

import dev.sygii.variantapi.VariantAPI;
import dev.sygii.variantapi.acess.EntityAccess;
import dev.sygii.variantapi.variants.Variant;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.PassiveEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
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
}
