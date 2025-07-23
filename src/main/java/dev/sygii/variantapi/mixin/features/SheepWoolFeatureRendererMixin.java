package dev.sygii.variantapi.mixin.features;

import com.llamalad7.mixinextras.sugar.Local;
import dev.sygii.variantapi.VariantAPI;
import dev.sygii.variantapi.acess.EntityAccess;
import dev.sygii.variantapi.variants.Variant;
import dev.sygii.variantapi.variants.feature.CustomWoolFeature;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.feature.SheepWoolFeatureRenderer;
import net.minecraft.entity.passive.SheepEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(SheepWoolFeatureRenderer.class)
public class SheepWoolFeatureRendererMixin {

    @ModifyArgs(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/passive/SheepEntity;FFFFFF)V",at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/feature/SheepWoolFeatureRenderer;render(Lnet/minecraft/client/render/entity/model/EntityModel;Lnet/minecraft/client/render/entity/model/EntityModel;Lnet/minecraft/util/Identifier;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFFFFF)V"))
    private void mixinEyesFeatureTexture(Args args, @Local(argsOnly = true) SheepEntity entity) {
        if (entity != null) {
            Variant variant = ((EntityAccess)entity).getVariant();
            if (!variant.id().equals(VariantAPI.getDefaultVariant().id())) {
                if (variant.getFeatures().containsKey(CustomWoolFeature.ID)) {
                    args.set(2, ((CustomWoolFeature)variant.getFeature(CustomWoolFeature.ID)).getTexture());
                }
            }
        }
    }
}
