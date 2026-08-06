package net.sievert.jolcraft.datagen.tag.provider;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.StructureTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.JolCraftTags;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.datagen.base.report.JolCraftDataTracking;
import net.sievert.jolcraft.datagen.tag.JolCraftMainTagProvider;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.world.item.component.custom.compass.DeepslateCompassStructureGroup;
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
        generate(
                this,
                null,
                CompletableFuture.completedFuture(provider),
                existingFileHelper
        );

        JolCraftDataTracking.logExplicitCount(
                this,
                this.builders.size(),
                JolCraftStrings.spaced(
                        tagType(),
                        JolCraftStrings.plural(domain().getId())
                )
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

        for (DeepslateCompassStructureGroup group
                : DeepslateCompassStructureGroup.values()) {
            var tag = target.tag(group.structureTag());

            for (var structure : group.structures()) {
                if (structure.location().getNamespace().equals(JolCraft.MOD_ID)) {
                    tag.addOptional(structure.location());
                } else {
                    tag.add(structure);
                }
            }
        }
    }
}