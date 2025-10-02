package net.sievert.jolcraft.worldgen.structure;

import com.mojang.serialization.MapCodec;
import net.sievert.jolcraft.JolCraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.sievert.jolcraft.worldgen.custom.structure.DwarvenTrailStructure;
import net.sievert.jolcraft.worldgen.custom.structure.ForgeStructure;

public class JolCraftStructures {

    /**
     * We are using the Deferred Registry system to register our structure as this is the preferred way on NeoForge.
     * This will handle registering the base structure for us at the correct time so we don't have to handle it ourselves.
     */
    public static final DeferredRegister<StructureType<?>> DEFERRED_REGISTRY_STRUCTURE = DeferredRegister.create(Registries.STRUCTURE_TYPE, JolCraft.MOD_ID);

    /**
     * Registers the base structure itself and sets what its path is. In this case,
     * this base structure will have the resourcelocation of structure_tutorial:sky_structures.
     */
    public static final DeferredHolder<StructureType<?>, StructureType<ForgeStructure>> FORGE_STRUCTURE = DEFERRED_REGISTRY_STRUCTURE.register("forge", () -> explicitStructureTypeTyping(ForgeStructure.CODEC));

    public static final DeferredHolder<StructureType<?>, StructureType<DwarvenTrailStructure>> DWARVEN_TRAIL_STRUCTURE = DEFERRED_REGISTRY_STRUCTURE.register("dwarven_trail_ruin", () -> explicitStructureTypeTyping(DwarvenTrailStructure.CODEC));

    /**
     * Originally, I had a double lambda ()->()-> for the RegistryObject line above, but it turns out that
     * some IDEs cannot resolve the typing correctly. This method explicitly states what the return type
     * is so that the IDE can put it into the DeferredRegistry properly.
     */
    private static <T extends Structure> StructureType<T> explicitStructureTypeTyping(MapCodec<T> structureCodec) {
        return () -> structureCodec;
    }
}