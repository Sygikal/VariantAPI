package dev.sygii.variantapi.mixin.features;

import com.llamalad7.mixinextras.sugar.Local;
import dev.sygii.variantapi.VariantAPI;
import dev.sygii.variantapi.acess.EntityAccess;
import dev.sygii.variantapi.variants.Variant;
import dev.sygii.variantapi.variants.feature.CustomEyesFeature;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.feature.EyesFeatureRenderer;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(EyesFeatureRenderer.class)
public class EyesFeatureRendererMixin {

	@ModifyArgs(method = "render",at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/VertexConsumerProvider;getBuffer(Lnet/minecraft/client/render/RenderLayer;)Lnet/minecraft/client/render/VertexConsumer;"))
	private void mixinEyesFeatureTexture(Args args, @Local(argsOnly = true) Entity entity) {
		if (entity != null) {
			Variant variant = ((EntityAccess)entity).getVariant();
			if (!variant.isDefault()) {
				if (variant.getFeatures().containsKey(CustomEyesFeature.ID)) {
					args.set(0, RenderLayer.getEyes(((CustomEyesFeature)variant.getFeature(CustomEyesFeature.ID)).getTexture()));
				}
			}
		}
	}
}
