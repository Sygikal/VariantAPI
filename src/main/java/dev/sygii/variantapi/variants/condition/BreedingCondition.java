package dev.sygii.variantapi.variants.condition;

import dev.sygii.variantapi.VariantAPI;
import dev.sygii.variantapi.acess.EntityAccess;
import dev.sygii.variantapi.variants.VariantCondition;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.util.Identifier;
import oshi.util.tuples.Pair;

public class BreedingCondition extends VariantCondition {
    public static Identifier ID = VariantAPI.id("breeding_condition");
    private final Identifier father;
    private final Identifier mother;

    public BreedingCondition(Identifier father, Identifier mother) {
        super(ID);
        this.father = father;
        this.mother = mother;
    }

    @Override
    public boolean condition(MobEntity entity) {
        if (((EntityAccess)entity).variantapi$getParents() != null) {
            Pair<PassiveEntity, PassiveEntity> parents = ((EntityAccess)entity).variantapi$getParents();
            if ((((EntityAccess)parents.getA()).getVariant().id().equals(father) && ((EntityAccess)parents.getB()).getVariant().id().equals(mother)) ||
                    (((EntityAccess)parents.getA()).getVariant().id().equals(mother) && ((EntityAccess)parents.getB()).getVariant().id().equals(father))) {
                ((EntityAccess)entity).setBreedingHandled(true);
                return true;
            }
        }
        return false;
    }
}
