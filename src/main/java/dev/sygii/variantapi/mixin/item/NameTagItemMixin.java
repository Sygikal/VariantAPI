package dev.sygii.variantapi.mixin.item;

import dev.sygii.variantapi.VariantAPI;
import dev.sygii.variantapi.acess.EntityAccess;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.NameTagItem;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NameTagItem.class)
public class NameTagItemMixin {

	@Inject(method = "useOnEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;setCustomName(Lnet/minecraft/text/Text;)V"))
	private void setVariantAfterUseOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
		String name = stack.getName().getString();
		if (entity instanceof MobEntity mobEntity) {
			if (VariantAPI.entityToCustomNameToVariantMap.get(entity.getType()) != null && VariantAPI.entityToCustomNameToVariantMap.get(entity.getType()).get(name) != null) {
				((EntityAccess) entity).setVariant(VariantAPI.entityToCustomNameToVariantMap.get(entity.getType()).get(name));
				VariantAPI.syncVariants(mobEntity);
			}
			if (VariantAPI.entityToCustomNameToVariantOverlayMap.get(entity.getType()) != null && VariantAPI.entityToCustomNameToVariantOverlayMap.get(entity.getType()).get(name) != null) {
				((EntityAccess) entity).setVariantOverlays(VariantAPI.entityToCustomNameToVariantOverlayMap.get(entity.getType()).get(name));
				VariantAPI.syncVariants(mobEntity);
			}
		}
	}
}
