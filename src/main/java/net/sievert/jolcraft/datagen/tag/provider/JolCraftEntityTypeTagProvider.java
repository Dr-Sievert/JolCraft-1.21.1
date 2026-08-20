package net.sievert.jolcraft.datagen.tag.provider;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.JolCraftTags;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.datagen.base.report.JolCraftDataTracking;
import net.sievert.jolcraft.datagen.tag.JolCraftMainTagProvider;
import net.sievert.jolcraft.util.JolCraftStrings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public final class JolCraftEntityTypeTagProvider
        extends EntityTypeTagsProvider
        implements JolCraftMainTagProvider<JolCraftEntityTypeTagProvider> {

    private final @Nullable ExistingFileHelper existingFileHelper;

    public JolCraftEntityTypeTagProvider(
            @NotNull PackOutput output,
            @NotNull CompletableFuture<HolderLookup.Provider> lookupProvider,
            @Nullable ExistingFileHelper existingFileHelper
    ) {
        super(output, lookupProvider, JolCraft.MOD_ID, existingFileHelper);
        this.existingFileHelper = existingFileHelper;
    }

    @Override
    public @NotNull String tagType() {
        return JolCraftStrings.underscored(JolCraftDictionary.ENTITY, JolCraftDictionary.TYPE);
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
            @NotNull JolCraftEntityTypeTagProvider target,
            @Nullable PackOutput packOutput,
            @Nullable CompletableFuture<HolderLookup.Provider> lookupProvider,
            @Nullable ExistingFileHelper existingFileHelper,
            @NotNull JolCraftDataTracking tracking
    ) {
        target.tag(JolCraftTags.EntityTypes.EXPLOSION_IMMUNE)
                .add(EntityType.WITHER)
                .add(EntityType.ENDER_DRAGON)
                .add(EntityType.WARDEN);

        target.tag(JolCraftTags.EntityTypes.EXPLOSION_RESISTANT)
                .add(EntityType.CREEPER)
                .add(EntityType.RAVAGER)
                .add(EntityType.ARMADILLO)
                .add(EntityType.BREEZE)
                .add(EntityType.IRON_GOLEM);

        target.tag(JolCraftTags.EntityTypes.EXPLOSION_VULNERABLE)
                .add(EntityType.SILVERFISH)
                .addTag(EntityTypeTags.SKELETONS);

        target.tag(JolCraftTags.EntityTypes.FIRE_IMMUNE)
                .add(EntityType.BLAZE)
                .add(EntityType.ENDER_DRAGON)
                .add(EntityType.GHAST)
                .add(EntityType.MAGMA_CUBE)
                .add(EntityType.SHULKER)
                .add(EntityType.STRIDER)
                .add(EntityType.VEX)
                .add(EntityType.WARDEN)
                .add(EntityType.WITHER)
                .add(EntityType.ZOGLIN)
                .add(EntityType.ZOMBIFIED_PIGLIN);

        target.tag(JolCraftTags.EntityTypes.FIRE_RESISTANT)
                .add(EntityType.HUSK)
                .add(EntityType.DROWNED)
                .add(EntityType.GUARDIAN)
                .add(EntityType.ELDER_GUARDIAN)
                .add(EntityType.IRON_GOLEM)
                .add(EntityType.WITHER_SKELETON)
                .add(EntityType.HOGLIN)
                .add(EntityType.PIGLIN)
                .add(EntityType.PIGLIN_BRUTE);

        target.tag(JolCraftTags.EntityTypes.FIRE_VULNERABLE)
                .add(EntityType.BOGGED)
                .add(EntityType.STRAY)
                .add(EntityType.SNOW_GOLEM)
                .add(EntityType.POLAR_BEAR);

        target.tag(JolCraftTags.EntityTypes.FROST_IMMUNE)
                .addTag(EntityTypeTags.FREEZE_IMMUNE_ENTITY_TYPES);

        target.tag(JolCraftTags.EntityTypes.FROST_RESISTANT)
                .add(EntityType.BREEZE);

        target.tag(JolCraftTags.EntityTypes.FROST_VULNERABLE)
                .addTag(EntityTypeTags.FREEZE_HURTS_EXTRA_TYPES)
                .add(EntityType.IRON_GOLEM)
                .add(EntityType.GHAST)
                .add(EntityType.WITHER_SKELETON);

        target.tag(JolCraftTags.EntityTypes.MAGIC_IMMUNE);

        target.tag(JolCraftTags.EntityTypes.MAGIC_RESISTANT)
                .add(EntityType.ALLAY)
                .add(EntityType.ENDERMAN)
                .add(EntityType.ENDERMITE)
                .add(EntityType.WITCH)
                .add(EntityType.ILLUSIONER)
                .add(EntityType.EVOKER);

        target.tag(JolCraftTags.EntityTypes.MAGIC_VULNERABLE)
                .add(EntityType.VILLAGER);

        target.tag(JolCraftTags.EntityTypes.POISON_IMMUNE)
                .addTag(EntityTypeTags.UNDEAD)
                .add(EntityType.SLIME)
                .add(EntityType.IRON_GOLEM)
                .add(EntityType.CAVE_SPIDER);

        target.tag(JolCraftTags.EntityTypes.POISON_RESISTANT)
                .add(EntityType.PUFFERFISH)
                .add(EntityType.WITCH)
                .add(EntityType.FROG)
                .add(EntityType.AXOLOTL)
                .add(EntityType.BEE)
                .add(EntityType.EVOKER)
                .add(EntityType.SPIDER);

        target.tag(JolCraftTags.EntityTypes.POISON_VULNERABLE)
                .add(EntityType.ALLAY);

        target.tag(JolCraftTags.EntityTypes.WITHER_IMMUNE)
                .add(EntityType.WITHER_SKELETON)
                .add(EntityType.WITHER);

        target.tag(JolCraftTags.EntityTypes.WITHER_RESISTANT)
                .add(EntityType.PHANTOM)
                .add(EntityType.ALLAY);

        target.tag(JolCraftTags.EntityTypes.WITHER_VULNERABLE);
    }
}