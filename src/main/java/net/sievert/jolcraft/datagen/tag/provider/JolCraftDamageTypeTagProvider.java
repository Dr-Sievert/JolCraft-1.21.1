package net.sievert.jolcraft.datagen.tag.provider;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.DamageTypeTagsProvider;
import net.minecraft.tags.DamageTypeTags;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.JolCraftTags;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.datagen.base.report.JolCraftDataTracking;
import net.sievert.jolcraft.datagen.tag.JolCraftMainTagProvider;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.world.entity.damage.JolCraftDamageTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public final class JolCraftDamageTypeTagProvider
        extends DamageTypeTagsProvider
        implements JolCraftMainTagProvider<JolCraftDamageTypeTagProvider> {

    private final @Nullable ExistingFileHelper existingFileHelper;

    public JolCraftDamageTypeTagProvider(
            @NotNull PackOutput output,
            @NotNull CompletableFuture<HolderLookup.Provider> lookupProvider,
            @Nullable ExistingFileHelper existingFileHelper
    ) {
        super(output, lookupProvider, JolCraft.MOD_ID, existingFileHelper);
        this.existingFileHelper = existingFileHelper;
    }

    @Override
    public @NotNull String tagType() {
        return JolCraftStrings.underscored(
                JolCraftDictionary.DAMAGE,
                JolCraftDictionary.TYPE
        );
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
            @NotNull JolCraftDamageTypeTagProvider target,
            @Nullable PackOutput packOutput,
            @Nullable CompletableFuture<HolderLookup.Provider> lookupProvider,
            @Nullable ExistingFileHelper existingFileHelper,
            @NotNull JolCraftDataTracking tracking
    ) {
        target.tag(JolCraftTags.DamageTypes.CURSE)
                .add(JolCraftDamageTypes.CURSED_WOUND)
                .add(JolCraftDamageTypes.VITALITY_CURSE);

        target.tag(Tags.DamageTypes.IS_MAGIC)
                .addTag(JolCraftTags.DamageTypes.CURSE);

        target.tag(DamageTypeTags.BYPASSES_ARMOR)
                .addTag(JolCraftTags.DamageTypes.CURSE);

        target.tag(DamageTypeTags.BYPASSES_ENCHANTMENTS)
                .add(JolCraftDamageTypes.VITALITY_CURSE);

        target.tag(DamageTypeTags.BYPASSES_RESISTANCE)
                .add(JolCraftDamageTypes.VITALITY_CURSE);
    }
}