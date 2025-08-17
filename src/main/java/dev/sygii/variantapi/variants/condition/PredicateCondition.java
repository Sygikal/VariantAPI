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
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

import java.util.Optional;

public class PredicateCondition extends VariantCondition {
    public static Identifier ID = VariantAPI.id("predicate_condition");

    private final Identifier predicate;
    public PredicateCondition(Identifier predicate) {
        super(ID);
        this.predicate = predicate;
    }

    @Override
    public boolean condition(MobEntity entity) {
        if (entity.getWorld() instanceof ServerWorld serverWorld) {

            //? if =1.20.1 {
            
            LootCondition lootCondition = serverWorld.getServer().getLootManager().getElement(LootDataType.PREDICATES, predicate);
            if (lootCondition != null) {
            //?} else {
            /*RegistryKey<LootCondition> registryKey = RegistryKey.of(RegistryKeys.PREDICATE, predicate);
            Optional<LootCondition> optional = serverWorld.getServer()
                    .getReloadableRegistries()
                    .createRegistryLookup()
                    .getOptionalEntry(RegistryKeys.PREDICATE, registryKey)
                    .map(RegistryEntry::value);
            if (optional.isEmpty()) {
                return false;
            } else {
                LootCondition lootCondition = (LootCondition)optional.get();
            *///?}
                LootContextParameterSet lootContextParameterSet = new LootContextParameterSet.Builder(serverWorld)
                        .add(LootContextParameters.THIS_ENTITY, entity)
                        .add(LootContextParameters.ORIGIN, entity.getPos())
                        .build(LootContextTypes.SELECTOR);
                LootContext lootContext = new LootContext.Builder(lootContextParameterSet).build(null);
                lootContext.markActive(LootContext.predicate(lootCondition));
                return lootCondition.test(lootContext);
            }
        }
        return false;
    }
}
