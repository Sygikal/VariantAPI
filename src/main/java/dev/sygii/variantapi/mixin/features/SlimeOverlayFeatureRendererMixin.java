package dev.sygii.variantapi.mixin.features;

import com.llamalad7.mixinextras.sugar.Local;
import dev.sygii.variantapi.acess.EntityAccess;
import dev.sygii.variantapi.variants.Variant;
import dev.sygii.variantapi.variants.feature.CustomWoolFeature;
import dev.sygii.variantapi.variants.feature.SlimeOverlayFeature;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.SlimeOverlayFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(SlimeOverlayFeatureRenderer.class)
public class SlimeOverlayFeatureRendererMixin<T extends LivingEntity> {

    @Shadow @Final private EntityModel<T> model;

    @ModifyArgs(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V",at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/EntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V"))
    private void mixinEyesFeatureTexture(Args args, @Local(argsOnly = true) T entity, @Local(argsOnly = true) VertexConsumerProvider vertexConsumerProvider, @Local boolean bl) {
        if (entity != null) {
            Variant variant = ((EntityAccess)entity).getVariant();
            if (!variant.isDefault()) {
                if (variant.getFeatures().containsKey(SlimeOverlayFeature.ID)) {
                    Identifier texture = ((SlimeOverlayFeature)variant.getFeature(SlimeOverlayFeature.ID)).getTexture();
                    //RenderLayer layer = this.model.getLayer(((SlimeOverlayFeature)variant.getFeature(SlimeOverlayFeature.ID)).getTexture());
                    //RenderLayer layer = RenderLayer.getEntityTranslucent(((SlimeOverlayFeature)variant.getFeature(SlimeOverlayFeature.ID)).getTexture());
                    //VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(layer);
                    VertexConsumer vertexConsumer;
                    if (bl) {
                        vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getOutline(texture));
                    } else {
                        vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntityTranslucent(texture));
                    }
                    args.set(1, vertexConsumer);
                }
            }
        }
    }

    /*@Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V", at = @At("HEAD"), cancellable = true)
    private void cancelRender(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
        ci.cancel();
    }*/

}
