package dev.sygii.variantapi.mixin.entity.enderman;

import org.spongepowered.asm.mixin.Mixin;

@Mixin(targets = "net.minecraft.entity.mob.EndermanEntity.TeleportTowardsPlayerGoal")
public abstract class InnerEndermanEntityMixin {

	/*@ModifyConstant(
			method = "tick",
			constant = @Constant(doubleValue = 16.0)
	)
	private double onCreateChild(double constant) {
		return constant;
	}

	@ModifyConstant(
			method = "tick",
			constant = @Constant(doubleValue = 256)
	)
	private double onCreateChild2(double constant) {
		return 8.0;
	}*/
}
