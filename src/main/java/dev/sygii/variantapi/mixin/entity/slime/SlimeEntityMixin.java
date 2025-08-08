package dev.sygii.variantapi.mixin.entity.slime;

import com.llamalad7.mixinextras.sugar.Local;
import dev.sygii.variantapi.acess.EntityAccess;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.SlimeEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(SlimeEntity.class)
public abstract class SlimeEntityMixin {

	@ModifyArg(method = "remove", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;spawnEntity(Lnet/minecraft/entity/Entity;)Z"))
	protected Entity modifyTeleportSound(Entity par1) {
		//new ItemStackParticleEffect(ParticleTypes.ITEM, this.getStack()),
		((EntityAccess)par1).setVariant(((EntityAccess)((SlimeEntity)(Object)this)).getVariant());
		return par1;
	}
}
