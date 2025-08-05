package dev.sygii.variantapi.mixin.entity;

import com.llamalad7.mixinextras.sugar.Local;
import dev.sygii.variantapi.acess.EntityAccess;
import dev.sygii.variantapi.mixin.access.QuadrupedEntityModelAccessor;
import dev.sygii.variantapi.variants.Variant;
import dev.sygii.variantapi.variants.feature.*;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Debug(export=true)
@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, M extends EntityModel<T>> {
	@Shadow
	protected M model;

	@Unique
	private final ModelPart horns = getTexturedModelData().createModel();

	/*@Redirect(method = "getRenderLayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/LivingEntityRenderer;getTexture(Lnet/minecraft/entity/Entity;)Lnet/minecraft/util/Identifier;"))
	private Identifier init(LivingEntityRenderer instance, Entity entity) {
		Variant variant = ((EntityAccess)entity).getVariant();
		if (!variant.isDefault()) {
			if (entity instanceof WolfEntity wolfEntity && variant.getFeatures().containsKey(WolfTexturesFeature.ID)) {
				if (wolfEntity.isTamed()) {
					return ((WolfTexturesFeature) variant.getFeature(WolfTexturesFeature.ID)).getTame();
				} else {
					return wolfEntity.hasAngerTime() ? ((WolfTexturesFeature) variant.getFeature(WolfTexturesFeature.ID)).getAngry() : variant.texture();
				}
			}
			return variant.texture();
		}
		return instance.getTexture(entity);
	}*/

	@ModifyVariable(method = "getRenderLayer", at = @At(value = "STORE", target = "Lnet/minecraft/client/render/entity/LivingEntityRenderer;getTexture(Lnet/minecraft/entity/Entity;)Lnet/minecraft/util/Identifier;"))
	private Identifier init(Identifier value, @Local(argsOnly = true) T entity) {
		Variant variant = ((EntityAccess)entity).getVariant();
		if (!variant.isDefault()) {
			if (entity instanceof WolfEntity wolfEntity && variant.getFeatures().containsKey(WolfTexturesFeature.ID)) {
				if (wolfEntity.isTamed()) {
					return ((WolfTexturesFeature) variant.getFeature(WolfTexturesFeature.ID)).getTame();
				} else {
					return wolfEntity.hasAngerTime() ? ((WolfTexturesFeature) variant.getFeature(WolfTexturesFeature.ID)).getAngry() : variant.texture();
				}
			}
			return variant.texture();
		}
		return value;
	}

	@Inject(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/EntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V", shift = At.Shift.AFTER))
	private void renderVariantOverlays(T livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
		Variant variant = ((EntityAccess)livingEntity).getVariant();
		if (!variant.isDefault()) {
			if (livingEntity instanceof SheepEntity sheepEntity) {
				if (variant.getFeatures().containsKey(CustomShearedWoolFeature.ID)) {
					//matrixStack.push();
					float[] hs = SheepEntity.getRgbColor(sheepEntity.getColor());

					RenderLayer FUR_OVERLAY = RenderLayer.getEntityCutoutNoCull(((CustomShearedWoolFeature) variant.getFeature(CustomShearedWoolFeature.ID)).getTexture());
					VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(FUR_OVERLAY);
					this.model.render(matrixStack, vertexConsumer, i, ((LivingEntityRenderer)(Object)this).getOverlay(livingEntity, ((LivingEntityRenderer)(Object)this).getAnimationCounter(livingEntity, g)), hs[0], hs[1], hs[2], 1.0f);
					//matrixStack.pop();
				}
				if (variant.getFeatures().containsKey(HornsFeature.ID)) {
					matrixStack.push();
					ModelPart sheepHead = ((QuadrupedEntityModelAccessor)this.model).getHead();
					horns.copyTransform(sheepHead);

					RenderLayer HORN = RenderLayer.getEntityCutoutNoCull(((HornsFeature) variant.getFeature(HornsFeature.ID)).getTexture());
					float[] colors = ((HornsFeature) variant.getFeature(HornsFeature.ID)).getColors();

					if (sheepEntity.isBaby()) {
						matrixStack.push();
						matrixStack.translate(0.0f, 0.5f, 0.25f);
					}
					horns.render(matrixStack, vertexConsumerProvider.getBuffer(HORN), i, ((LivingEntityRenderer)(Object)this).getOverlay(livingEntity, ((LivingEntityRenderer)(Object)this).getAnimationCounter(livingEntity, g)), colors[0], colors[1], colors[2], 1.0f);
					if (sheepEntity.isBaby()) {
						matrixStack.pop();
					}
					matrixStack.pop();
				}
			}
		}
		((EntityAccess)livingEntity).getVariantOverlays().forEach(k -> {
			RenderLayer layer = RenderLayer.getEntityCutoutNoCull(k.texture());
			if (k.getFeatures().containsKey(CustomRenderLayerFeature.ID)) {
				switch (((CustomRenderLayerFeature)k.getFeature(CustomRenderLayerFeature.ID)).getLayer()) {
					case ENTITY_CUTOUT_NO_CULL:
						layer = RenderLayer.getEntityCutoutNoCull(k.texture());
						break;
					case ENTITY_ALPHA:
						layer = RenderLayer.getEntityAlpha(k.texture());
						break;
					case EYES:
						layer = RenderLayer.getEyes(k.texture());
						break;
				}
			}
			VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(layer);

			int light = i;
			if (k.getFeatures().containsKey(CustomLightingFeature.ID)) {
				light = ((CustomLightingFeature)k.getFeature(CustomLightingFeature.ID)).getLight();
			}
			this.model.render(matrixStack, vertexConsumer, light, ((LivingEntityRenderer)(Object)this).getOverlay(livingEntity, ((LivingEntityRenderer)(Object)this).getAnimationCounter(livingEntity, g)), 1.0F, 1.0F, 1.0F, 1.0F);
		});
	}

	@Unique
	private static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();

		modelPartData.addChild(EntityModelPartNames.HEAD,
				ModelPartBuilder.create()
						.uv(0, 0).cuboid(3.0f, -3.5f, -3.0f, 4.0f, 7.0f, 6.0f)
						.uv(0, 13).cuboid(3.0f, 0.5f, -6.0f, 4.0f, 3.0f, 3.0f)
						.uv(0, 19).cuboid(-7.0f, -3.5f, -3.0f, 4.0f, 7.0f, 6.0f)
						.uv(14, 13).cuboid(-7.0f, 0.5f, -6.0f, 4.0f, 3.0f, 3.0f),
				ModelTransform.pivot(0.0f, -1.5f, -2.1f));
		return TexturedModelData.of(modelData, 32, 32);
	}
}
