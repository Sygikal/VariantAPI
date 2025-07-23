package dev.sygii.variantapi.variants.condition;

import com.google.gson.JsonObject;
import dev.sygii.variantapi.variants.VariantCondition;
import karashokleo.l2hostility.content.component.mob.MobDifficulty;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.Identifier;

public class L2HostilityLevelCondition extends VariantCondition {
    public static Identifier ID = Identifier.of("l2hostility", "l2hostility_level_condition");

    private final int min;
    public L2HostilityLevelCondition(int min) {
        super(ID);
        this.min = min;
    }

    @Override
    public boolean condition(MobEntity entity) {
        if (FabricLoader.getInstance().isModLoaded("l2hostility")) {
            int l2level = MobDifficulty.get(entity).get().getLevel();
            return l2level >= min;
        }
        return false;
    }
}
