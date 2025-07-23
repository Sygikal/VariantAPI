package dev.sygii.variantapi.mixin.entity;

import dev.sygii.variantapi.VariantAPI;
import dev.sygii.variantapi.acess.EntityAccess;
import dev.sygii.variantapi.variants.Variant;
import dev.sygii.variantapi.variants.feature.CustomLightingFeature;
import dev.sygii.variantapi.variants.feature.CustomRenderLayerFeature;
import dev.sygii.variantapi.variants.feature.CustomShearedWoolFeature;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Debug(export=true)
@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, M extends EntityModel<T>> {
	@Shadow
	protected M model;

	@Redirect(method = "getRenderLayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/LivingEntityRenderer;getTexture(Lnet/minecraft/entity/Entity;)Lnet/minecraft/util/Identifier;"))
	private Identifier init(LivingEntityRenderer instance, Entity entity) {

		Variant variant = ((EntityAccess)entity).getVariant();
		if (!variant.id().equals(VariantAPI.getDefaultVariant().id())) {
			return variant.texture();
		}
		return instance.getTexture(entity);
	}

	@Inject(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/EntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V", shift = At.Shift.AFTER))
	private void renderVariantOverlays(T livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
		Variant variant = ((EntityAccess)livingEntity).getVariant();
		if (!variant.id().equals(VariantAPI.getDefaultVariant().id())) {
			if (livingEntity instanceof SheepEntity sheepEntity) {
				if (variant.getFeatures().containsKey(CustomShearedWoolFeature.ID)) {
					//matrixStack.push();
					float[] hs = SheepEntity.getRgbColor(sheepEntity.getColor());

					RenderLayer FUR_OVERLAY = RenderLayer.getEntityCutoutNoCull(((CustomShearedWoolFeature) variant.getFeature(CustomShearedWoolFeature.ID)).getTexture());
					VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(FUR_OVERLAY);
					this.model.render(matrixStack, vertexConsumer, i, ((LivingEntityRenderer)(Object)this).getOverlay(livingEntity, ((LivingEntityRenderer)(Object)this).getAnimationCounter(livingEntity, g)), hs[0], hs[1], hs[2], 1.0f);
					//matrixStack.pop();
				}
			}
		}
		((EntityAccess)livingEntity).getVariantOverlays().forEach(k -> {
			RenderLayer layer = RenderLayer.getEntityCutoutNoCull(k.texture());
			if (k.getFeatures().containsKey(CustomRenderLayerFeature.ID)) {
				layer = ((CustomRenderLayerFeature)k.getFeature(CustomRenderLayerFeature.ID)).getLayer(k.texture());
			}
			VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(layer);

			int light = i;
			if (k.getFeatures().containsKey(CustomLightingFeature.ID)) {
				light = ((CustomLightingFeature)k.getFeature(CustomLightingFeature.ID)).getLight();
			}
			this.model.render(matrixStack, vertexConsumer, light, ((LivingEntityRenderer)(Object)this).getOverlay(livingEntity, ((LivingEntityRenderer)(Object)this).getAnimationCounter(livingEntity, g)), 1.0F, 1.0F, 1.0F, 1.0F);
		});
	}
}
