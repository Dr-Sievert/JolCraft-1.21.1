package net.sievert.jolcraft.world.worldgen.structure;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.worldgen.JolCraftStructureIds;
import net.sievert.jolcraft.world.worldgen.structure.custom.DwarvenFortressStructure;
import net.sievert.jolcraft.world.worldgen.structure.custom.DwarvenTrailStructure;
import net.sievert.jolcraft.world.worldgen.structure.custom.ForgeStructure;

import static net.sievert.jolcraft.JolCraft.location;

public final class JolCraftStructures {

    private JolCraftStructures() {}

    public static final DeferredRegister<StructureType<?>> STRUCTURE_TYPES =
            DeferredRegister.create(Registries.STRUCTURE_TYPE, JolCraft.MOD_ID);

    public record RegisteredStructure<T extends Structure>(
            ResourceLocation id,
            ResourceKey<Structure> key,
            ResourceKey<StructureSet> setKey,
            DeferredHolder<StructureType<?>, StructureType<T>> type
    ) {}

    public static final RegisteredStructure<DwarvenFortressStructure> DWARVEN_FORTRESS =
            register(JolCraftStructureIds.DWARVEN_FORTRESS, DwarvenFortressStructure.CODEC);

    public static final RegisteredStructure<ForgeStructure> FORGE =
            register(JolCraftStructureIds.FORGE, ForgeStructure.CODEC);

    public static final RegisteredStructure<DwarvenTrailStructure> DWARVEN_TRAIL_RUIN =
            register(JolCraftStructureIds.DWARVEN_TRAIL_RUIN, DwarvenTrailStructure.CODEC);

    private static <T extends Structure> RegisteredStructure<T> register(String path, MapCodec<T> codec) {
        ResourceLocation id = location(path);

        ResourceKey<Structure> key =
                ResourceKey.create(Registries.STRUCTURE, id);

        ResourceKey<StructureSet> setKey =
                ResourceKey.create(Registries.STRUCTURE_SET, id);

        DeferredHolder<StructureType<?>, StructureType<T>> type =
                STRUCTURE_TYPES.register(path, () -> explicitStructureTypeTyping(codec));

        return new RegisteredStructure<>(id, key, setKey, type);
    }

    private static <T extends Structure> StructureType<T> explicitStructureTypeTyping(MapCodec<T> structureCodec) {
        return () -> structureCodec;
    }
}