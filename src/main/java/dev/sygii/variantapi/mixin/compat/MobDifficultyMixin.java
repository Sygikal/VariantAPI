package dev.sygii.variantapi.mixin.compat;

import dev.sygii.variantapi.VariantAPI;
import karashokleo.l2hostility.content.component.chunk.RegionalDifficultyModifier;
import karashokleo.l2hostility.content.component.mob.MobDifficulty;
import net.minecraft.entity.mob.MobEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MobDifficulty.class)
public class MobDifficultyMixin {

	@Shadow
    public MobEntity owner;

	@Inject(method = "init",at = @At(value = "INVOKE", target = "Lkarashokleo/l2hostility/content/component/mob/MobDifficulty;sync()V"), remap = false)
	private void mixinEyesFeatureTexture(RegionalDifficultyModifier difficulty, CallbackInfo ci) {
		VariantAPI.rerollVariants(owner);
	}
}
