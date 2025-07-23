package dev.sygii.variantapi.features;

import dev.sygii.variantapi.acess.EntityAccess;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

public class VariantOverlayFeatureRenderer<T extends Entity, M extends EntityModel<T>> extends FeatureRenderer<T, M> {

    public VariantOverlayFeatureRenderer(FeatureRendererContext<T, M> featureRendererContext) {
        super(featureRendererContext);
    }

    public static int getOverlay(LivingEntity entity, float whiteOverlayProgress) {
        return OverlayTexture.packUv(OverlayTexture.getU(whiteOverlayProgress), OverlayTexture.getV(entity.hurtTime > 0 || entity.deathTime > 0));
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        ((EntityAccess)entity).getVariantOverlays().forEach(k -> {
            VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCull(k.texture()));
            int p = getOverlay((LivingEntity) entity, 0.0F);
            //vertexConsumer.color(p);

            //this.getContextModel().render(matrices, vertexConsumer, light, pcap, 1.0F, 1.0F, 1.0F, 1.0F);



            //((Model) this.getContextModel()).render(matrices, vertexConsumer, 0xF00000, OverlayTexture.DEFAULT_UV, 1.0f, 1.0f, 1.0f, 1.0f);
        });
        /*NbtCompound nbt = new NbtCompound();
        entity.writeNbt(nbt);

        if (nbt.contains(VariantAPI.VARIANT_OVERLAYS_NBT_KEY, NbtElement.LIST_TYPE)) {
            NbtList nbtList = nbt.getList(VariantAPI.VARIANT_OVERLAYS_NBT_KEY, NbtElement.STRING_TYPE);
            if (entity instanceof SkeletonEntity) {
                //System.out.println(nbtList);
            }

            for (int i = 0; i < nbtList.size(); i++) {
                String variantString = nbtList.getString(i);

                Variant variant = VariantAPI.getVariantOverlay(entity.getType(), Identifier.tryParse(variantString));
                if (variant != null) {
                    VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(variant.texture()));
                    ((Model) this.getContextModel()).render(matrices, vertexConsumer, 0xF00000, OverlayTexture.DEFAULT_UV, 1.0f, 1.0f, 1.0f, 1.0f);
                }
            }
        }*/
        /*NbtCompound nbt = new NbtCompound();
        entity.writeCustomDataToNbt(nbt);

        if (nbt.contains(MoreMobVariants.MUDDY_NBT_KEY)) {
            if (nbt.getBoolean(MoreMobVariants.MUDDY_NBT_KEY)) {
                VertexConsumer vertexConsumer = vertexConsumers.getBuffer(this.getMudTexture());
                ((Model)this.getContextModel()).render(matrices, vertexConsumer, 0xF00000, OverlayTexture.DEFAULT_UV, 1.0f, 1.0f, 1.0f, 1.0f);
            }
        }*/
    }
}
