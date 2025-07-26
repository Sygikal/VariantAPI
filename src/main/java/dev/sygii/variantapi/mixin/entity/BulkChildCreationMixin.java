package dev.sygii.variantapi.mixin.entity;

import dev.sygii.variantapi.VariantAPI;
import dev.sygii.variantapi.acess.EntityAccess;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({
        SheepEntity.class,
        PigEntity.class
})
public class BulkChildCreationMixin {

    /*@Inject(
            method = "createChild(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/passive/PassiveEntity;)Lnet/minecraft/entity/passive/PassiveEntity;",
            at = @At("RETURN")
    )
    private void onCreateChild(ServerWorld world, PassiveEntity entity, CallbackInfoReturnable<PassiveEntity> ci) {
        PassiveEntity child = ci.getReturnValue();

        /*MobVariant variant = Variants.getChildVariant(EntityType.SHEEP, world, ((SheepEntity)(Object)this), entity);

        // Determine horn colour
        NbtCompound nbtParent1 = new NbtCompound();
        ((SheepEntity)(Object)this).writeCustomDataToNbt(nbtParent1);
        NbtCompound nbtParent2 = new NbtCompound();
        entity.writeCustomDataToNbt(nbtParent2);

        String colour = "";
        if (nbtParent1.contains(MoreMobVariants.SHEEP_HORN_COLOUR_NBT_KEY)
                && !nbtParent1.getString(MoreMobVariants.SHEEP_HORN_COLOUR_NBT_KEY).isEmpty()
                && nbtParent2.contains(MoreMobVariants.SHEEP_HORN_COLOUR_NBT_KEY)
                && !nbtParent2.getString(MoreMobVariants.SHEEP_HORN_COLOUR_NBT_KEY).isEmpty()
                && entity.getRandom().nextDouble() <= SheepHornSettings.getInheritChance()) {
            colour = entity.getRandom().nextBoolean() ? nbtParent1.getString(MoreMobVariants.SHEEP_HORN_COLOUR_NBT_KEY) : nbtParent2.getString(MoreMobVariants.SHEEP_HORN_COLOUR_NBT_KEY);
        } else {
            SheepHornSettings.SheepHornColour col = SheepHornSettings.getRandomSheepHornColour(entity.getRandom(), world.getBiome(((SheepEntity)(Object)this).getBlockPos()));
            if (col != null) {
                colour = col.getId();
            }
        }

        // Write variant to child's NBT
        NbtCompound childNbt = new NbtCompound();
        child.writeNbt(childNbt);
        childNbt.putString(MoreMobVariants.NBT_KEY, variant.getIdentifier().toString());
        childNbt.putString(MoreMobVariants.SHEEP_HORN_COLOUR_NBT_KEY, colour);
        child.readCustomDataFromNbt(childNbt);//
        ((EntityAccess)child).setVariant(VariantAPI.getRandomVariant(child, child.getType(), null, child.getWorld(), child.getRandom().nextLong(), child.getWorld().getBiome(child.getBlockPos()), child.getWorld().getMoonSize()));
        System.out.println("asd");
    }*/

}
