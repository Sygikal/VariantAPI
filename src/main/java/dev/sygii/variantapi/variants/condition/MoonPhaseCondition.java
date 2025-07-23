package dev.sygii.variantapi.variants.condition;

import dev.sygii.variantapi.VariantAPI;
import dev.sygii.variantapi.variants.VariantCondition;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.loot.LootDataType;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

public class MoonPhaseCondition extends VariantCondition {
    public static Identifier ID = VariantAPI.id("moon_phase_condition");

    private final int size;
    public MoonPhaseCondition(int size) {
        super(ID);
        this.size = size;
    }

    @Override
    public boolean condition(MobEntity entity) {
         if (!(entity.getWorld().getMoonSize() > size)) {
            return true;
        }
        return false;
    }
}
