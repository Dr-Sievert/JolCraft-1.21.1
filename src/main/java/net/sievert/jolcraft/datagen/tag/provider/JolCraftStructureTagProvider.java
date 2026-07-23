package net.sievert.jolcraft.datagen.tag.provider;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.StructureTagsProvider;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.JolCraftTags;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.datagen.base.report.JolCraftDataTracking;
import net.sievert.jolcraft.datagen.tag.JolCraftMainTagProvider;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.world.worldgen.structure.JolCraftStructures;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public final class JolCraftStructureTagProvider
        extends StructureTagsProvider
        implements JolCraftMainTagProvider<JolCraftStructureTagProvider> {

    private final @Nullable ExistingFileHelper existingFileHelper;

    public JolCraftStructureTagProvider(
            @NotNull PackOutput output,
            @NotNull CompletableFuture<HolderLookup.Provider> lookupProvider,
            @Nullable ExistingFileHelper existingFileHelper
    ) {
        super(output, lookupProvider, JolCraft.MOD_ID, existingFileHelper);
        this.existingFileHelper = existingFileHelper;
    }

    @Override
    public @NotNull String tagType() {
        return JolCraftDictionary.STRUCTURE;
    }

    @Override
    public @NotNull String getName() {
        return name();
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        generate(this, null, CompletableFuture.completedFuture(provider), existingFileHelper);

        JolCraftDataTracking.logExplicitCount(
                this,
                this.builders.size(),
                JolCraftStrings.spaced(tagType(), JolCraftStrings.plural(domain().getId()))
        );
    }

    @Override
    public void run(
            @NotNull JolCraftStructureTagProvider target,
            @Nullable PackOutput packOutput,
            @Nullable CompletableFuture<HolderLookup.Provider> lookupProvider,
            @Nullable ExistingFileHelper existingFileHelper,
            @NotNull JolCraftDataTracking tracking
    ) {
        target.tag(JolCraftTags.Structures.FEATURE_PROTECTED)
                .addOptional(JolCraftStructures.DWARVEN_FORTRESS.id());

        target.tag(JolCraftTags.Structures.ON_DWARVEN_FORTRESS_EXPLORER_MAPS)
                .addOptional(JolCraftStructures.DWARVEN_FORTRESS.id());

        target.tag(JolCraftTags.Structures.DWARVEN)
                .addOptional(JolCraftStructures.DWARVEN_FORTRESS.id());

        target.tag(JolCraftTags.Structures.VILLAGES)
                .add(BuiltinStructures.VILLAGE_PLAINS)
                .add(BuiltinStructures.VILLAGE_DESERT)
                .add(BuiltinStructures.VILLAGE_SAVANNA)
                .add(BuiltinStructures.VILLAGE_SNOWY)
                .add(BuiltinStructures.VILLAGE_TAIGA);

        target.tag(JolCraftTags.Structures.PILLAGERS)
                .add(BuiltinStructures.PILLAGER_OUTPOST)
                .add(BuiltinStructures.WOODLAND_MANSION);

        target.tag(JolCraftTags.Structures.NETHER_PORTALS)
                .add(BuiltinStructures.RUINED_PORTAL_STANDARD)
                .add(BuiltinStructures.RUINED_PORTAL_DESERT)
                .add(BuiltinStructures.RUINED_PORTAL_MOUNTAIN)
                .add(BuiltinStructures.RUINED_PORTAL_JUNGLE)
                .add(BuiltinStructures.RUINED_PORTAL_SWAMP)
                .add(BuiltinStructures.RUINED_PORTAL_OCEAN);

        target.tag(JolCraftTags.Structures.SURFACE)
                .addTag(JolCraftTags.Structures.VILLAGES)
                .addTag(JolCraftTags.Structures.PILLAGERS)
                .addTag(JolCraftTags.Structures.NETHER_PORTALS)
                .add(BuiltinStructures.MINESHAFT_MESA)
                .add(BuiltinStructures.JUNGLE_TEMPLE)
                .add(BuiltinStructures.DESERT_PYRAMID)
                .add(BuiltinStructures.IGLOO)
                .add(BuiltinStructures.SWAMP_HUT);

        target.tag(JolCraftTags.Structures.RUINS)
                .add(BuiltinStructures.TRAIL_RUINS)
                .add(BuiltinStructures.OCEAN_RUIN_COLD)
                .add(BuiltinStructures.OCEAN_RUIN_WARM);

        target.tag(JolCraftTags.Structures.OCEAN)
                .add(BuiltinStructures.BURIED_TREASURE)
                .add(BuiltinStructures.SHIPWRECK)
                .add(BuiltinStructures.SHIPWRECK_BEACHED)
                .add(BuiltinStructures.OCEAN_RUIN_COLD)
                .add(BuiltinStructures.OCEAN_RUIN_WARM)
                .add(BuiltinStructures.OCEAN_MONUMENT);

        target.tag(JolCraftTags.Structures.UNDERGROUND)
                .add(BuiltinStructures.MINESHAFT)
                .add(BuiltinStructures.ANCIENT_CITY)
                .add(BuiltinStructures.TRIAL_CHAMBERS)
                .add(BuiltinStructures.STRONGHOLD)
                .addOptional(JolCraftStructures.DWARVEN_FORTRESS.id());
    }
}