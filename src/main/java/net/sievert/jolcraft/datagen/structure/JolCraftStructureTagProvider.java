package net.sievert.jolcraft.datagen.structure;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.StructureTagsProvider;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.JolCraftTags;
import net.sievert.jolcraft.worldgen.structure.JolCraftStructures;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

import static net.sievert.jolcraft.JolCraft.location;

public final class JolCraftStructureTagProvider extends StructureTagsProvider {

    public JolCraftStructureTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
        super(output, provider, JolCraft.MOD_ID);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        this.tag(JolCraftTags.Structures.ON_FORGE_EXPLORER_MAPS)
                .addOptional(location(JolCraftStructures.FORGE_ID));

        this.tag(JolCraftTags.Structures.DWARVEN_STRUCTURES)
                .addOptional(location(JolCraftStructures.FORGE_ID))
                .addOptional(location(JolCraftStructures.DWARVEN_TRAIL_RUIN_ID));

        this.tag(JolCraftTags.Structures.ANCIENT_STRUCTURES)
                .add(BuiltinStructures.ANCIENT_CITY)
                .add(BuiltinStructures.TRAIL_RUINS);
    }
}
