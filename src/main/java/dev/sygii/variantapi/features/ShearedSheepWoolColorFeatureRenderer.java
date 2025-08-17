package dev.sygii.variantapi.features;

import dev.sygii.variantapi.VariantAPI;
import dev.sygii.variantapi.acess.EntityAccess;
import dev.sygii.variantapi.variants.Variant;
import dev.sygii.variantapi.variants.feature.CustomShearedWoolFeature;
import dev.sygii.variantapi.variants.feature.CustomWoolFeature;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.SheepEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.SheepEntity;

public class ShearedSheepWoolColorFeatureRenderer <T extends SheepEntity, M extends SheepEntityModel<T>> extends FeatureRenderer<T, M> {
    public ShearedSheepWoolColorFeatureRenderer(FeatureRendererContext<T, M> context) {
        super(context);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        /*
        if (!sheepEntity.isSheared()) {
             return;
        }
        */

        Variant variant = ((EntityAccess)entity).getVariant();
        if (!variant.isDefault()) {
            if (variant.getFeatures().containsKey(CustomShearedWoolFeature.ID)) {
                //float[] hs = SheepEntity.getRgbColor(entity.getColor());

                RenderLayer FUR_OVERLAY = RenderLayer.getEntityCutoutNoCull(((CustomShearedWoolFeature)variant.getFeature(CustomShearedWoolFeature.ID)).getTexture());
                VertexConsumer vertexConsumer = vertexConsumers.getBuffer(FUR_OVERLAY);
                //((Model)this.getContextModel()).render(matrices, vertexConsumer, 0xF00000, OverlayTexture.DEFAULT_UV, hs[0], hs[1], hs[2], 1.0f);
            }
        }
    };

}