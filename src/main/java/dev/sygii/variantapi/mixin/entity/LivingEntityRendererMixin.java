package dev.sygii.variantapi.mixin.entity;

import com.llamalad7.mixinextras.sugar.Local;
import dev.sygii.variantapi.acess.EntityAccess;
import dev.sygii.variantapi.mixin.access.QuadrupedEntityModelAccessor;
import dev.sygii.variantapi.variants.Variant;
import dev.sygii.variantapi.variants.feature.*;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
	//? if =1.20.1 {
	@Inject(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/EntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V", shift = At.Shift.AFTER))
	//?} else {
	/*@Inject(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/EntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;III)V", shift = At.Shift.AFTER))
	*///?}
	private void renderVariantOverlays(T livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
		Variant variant = ((EntityAccess)livingEntity).getVariant();
		if (!variant.isDefault()) {
			if (livingEntity instanceof SheepEntity sheepEntity) {
				if (variant.getFeatures().containsKey(CustomShearedWoolFeature.ID)) {
					RenderLayer FUR_OVERLAY = RenderLayer.getEntityCutoutNoCull(((CustomShearedWoolFeature) variant.getFeature(CustomShearedWoolFeature.ID)).getTexture());
					VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(FUR_OVERLAY);
					//? if =1.20.1 {
					float[] floats = SheepEntity.getRgbColor(sheepEntity.getColor());
					int hs = -1;
					//?} else {
					/*float[] floats = {1.0f, 1.0f, 1.0f, 1.0f};
					int hs = SheepEntity.getRgbColor(sheepEntity.getColor());
					*///?}
					renderModel(this.model, matrixStack, vertexConsumer, i, ((LivingEntityRenderer)(Object)this).getOverlay(livingEntity, ((LivingEntityRenderer)(Object)this).getAnimationCounter(livingEntity, g)), hs, floats[0], floats[1], floats[2], 1.0f);
				}
			}
			if (variant.getFeatures().containsKey(HornsFeature.ID)) {
				ModelPart head = null;
				if (this.model instanceof BipedEntityModel<?> biped) {
					head = biped.getHead();
				}else if (this.model instanceof QuadrupedEntityModel<?> quad) {
					head = ((QuadrupedEntityModelAccessor)quad).getHead();
				}else if (this.model instanceof SinglePartEntityModel<?> single) {
					head = single.getPart().getChild(EntityModelPartNames.HEAD);
				}

				if (head != null) {
					matrixStack.push();
					horns.copyTransform(head);

					RenderLayer HORN = RenderLayer.getEntityCutoutNoCull(((HornsFeature) variant.getFeature(HornsFeature.ID)).getTexture());

					if (livingEntity.isBaby()) {
						matrixStack.push();
						matrixStack.translate(0.0f, 0.5f, 0.25f);
					}
					//matrixStack.translate(0.0f, -0.2f, 0.0f);
					float[] colors = ((HornsFeature) variant.getFeature(HornsFeature.ID)).getColors();
					renderModel(horns, matrixStack, vertexConsumerProvider.getBuffer(HORN), i, ((LivingEntityRenderer) (Object) this).getOverlay(livingEntity, ((LivingEntityRenderer) (Object) this).getAnimationCounter(livingEntity, g)), ((HornsFeature) variant.getFeature(HornsFeature.ID)).getColor(), colors[0], colors[1], colors[2], 1.0F);
					if (livingEntity.isBaby()) {
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
			renderModel(this.model, matrixStack, vertexConsumer, light, ((LivingEntityRenderer)(Object)this).getOverlay(livingEntity, ((LivingEntityRenderer)(Object)this).getAnimationCounter(livingEntity, g)), -1, 1.0F, 1.0F, 1.0F, 1.0F);
		});
		/*if (livingEntity instanceof SlimeEntity slimeEntity) {
			if (slimeEntity.isAlive() && !slimeEntity.isInvisible()) {
				ItemStack stack = Items.TNT.getDefaultStack();
				if (stack.isEmpty()) {
					return;
				}
				if (!stack.isEmpty()) {
					matrixStack.push();
					matrixStack.multiply(new Quaternionf().rotateX(MathHelper.PI));
					matrixStack.translate(0, -1, 0);
					matrixStack.multiply(new Quaternionf().rotateX(MathHelper.PI / 2f));
					matrixStack.multiply(new Quaternionf().rotateY(slimeEntity.getId() % 360));
					matrixStack.translate(0, -(4 * 0.0626), 0);
					matrixStack.translate(0, 0, -0.0626 / 4);
					matrixStack.multiply(new Quaternionf().rotateY(MathHelper.PI / 2f));
					MinecraftClient.getInstance()
							.getItemRenderer()
							.renderItem(stack, ModelTransformationMode.GROUND, i, OverlayTexture.DEFAULT_UV, matrixStack, vertexConsumerProvider, slimeEntity.getWorld(), (int) slimeEntity.getBlockPos()
									.asLong());
					matrixStack.pop();
				}
			}
		}*/
	}

	public void renderModel(ModelPart model,  MatrixStack matrixStack, VertexConsumer vertexConsumer, int light, int overlay, int color, float color1, float color2, float color3, float color4) {
		//? if =1.20.1 {
		model.render(matrixStack, vertexConsumer, light, overlay, color1, color2, color3, color4);
		 //?} else {
		/*model.render(matrixStack, vertexConsumer, light, overlay, color);
		*///?}
	}

	public void renderModel(M model,  MatrixStack matrixStack, VertexConsumer vertexConsumer, int light, int overlay, int color, float color1, float color2, float color3, float color4) {
		//? if =1.20.1 {
		model.render(matrixStack, vertexConsumer, light, overlay, color1, color2, color3, color4);
		//?} else {
		/*model.render(matrixStack, vertexConsumer, light, overlay, color);
		*///?}
	}

	// TODO: code will remain unused until i figure out how to unmap field names :cry:
	/*

	Field field = getHeadModel(this.model.getClass());
					if (field != null) {
						field.setAccessible(true);

						ModelPart head = (ModelPart) field.get(this.model);
	public Field getHeadModel(Class clazz) {
		if (clazz != null) {
			try {
				String unmappedClassName = FabricLoader.getInstance().getMappingResolver().unmapClassName("intermediary", clazz.getName());
				String namedFieldName = FabricLoader.getInstance().getMappingResolver().mapFieldName("named", unmappedClassName, "head", "Lnet/minecraft/class_630;");
				String intermediaryFieldName = FabricLoader.getInstance().getMappingResolver().mapFieldName("intermediary", unmappedClassName, "head", "Lnet/minecraft/class_630;");
				System.out.println(namedFieldName + " | " + intermediaryFieldName);
				Field thisClass = clazz.getDeclaredField(intermediaryFieldName);
				//System.out.println(thisClass);
				return thisClass;
			} catch (NoSuchFieldException e) {
				//System.out.println("going higher! " + clazz.getSuperclass().getName());
				return getHeadModel(clazz.getSuperclass());
			}
		}
		return null;
	}*/

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
