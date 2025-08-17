package dev.sygii.variantapi.variants.condition;

import dev.sygii.variantapi.VariantAPI;
import dev.sygii.variantapi.variants.VariantCondition;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.CatVariant;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.StructureTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.structure.Structure;

public class StructureCondition extends VariantCondition {
    public static Identifier ID = VariantAPI.id("structure_condition");
    private final Identifier structure;
    private TagKey<Structure> structureTag = null;

    public StructureCondition(String structure) {
        super(ID);
        if (structure.startsWith("#")) {
            structureTag = TagKey.of(RegistryKeys.STRUCTURE, Identifier.tryParse(structure.replace("#", "")));
        }
        this.structure = Identifier.tryParse(structure.replace("#", ""));
    }

    @Override
    public boolean condition(MobEntity entity) {
        if (entity.getWorld() instanceof ServerWorld serverWorld) {
            if (structureTag != null) {
                return serverWorld.getStructureAccessor().getStructureContaining(entity.getBlockPos(), structureTag).hasChildren();
            } else {
                //? if =1.20.1 {
                return serverWorld.getStructureAccessor().getStructureContaining(entity.getBlockPos(), RegistryKey.of(RegistryKeys.STRUCTURE, structure)).hasChildren();
                //?} else {
                /*return false;
                *///?}
            }
        }
        return false;
    }
}
