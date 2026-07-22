package net.sievert.jolcraft.datagen.client.sound;

import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.SoundDefinition;
import net.neoforged.neoforge.common.data.SoundDefinitionsProvider;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.datagen.base.JolCraftDataDomain;
import net.sievert.jolcraft.datagen.base.JolCraftMainDataProvider;
import net.sievert.jolcraft.datagen.base.report.JolCraftDataTracking;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.world.sound.JolCraftSounds;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public final class JolCraftSoundProvider extends SoundDefinitionsProvider
        implements JolCraftMainDataProvider<JolCraftSoundProvider> {

    private static final char UNDERSCORE = '_';

    private int addedSoundEvents;
    private @Nullable JolCraftDataTracking tracking;

    public JolCraftSoundProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, JolCraft.MOD_ID, existingFileHelper);
    }

    @Override
    public @NotNull JolCraftDataDomain domain() {
        return JolCraftDataDomain.SOUND;
    }

    @Override
    public @NotNull String id() {
        return domain().getId();
    }

    @Override
    public @NotNull CompletableFuture<?> run(@NotNull CachedOutput cache) {
        this.addedSoundEvents = 0;
        this.tracking = null;

        generate(this, null, null, null);

        return super.run(cache).whenComplete((unused, throwable) -> {
            if (throwable == null) {
                JolCraftDataTracking.logExplicitCount(
                        this,
                        this.addedSoundEvents,
                        JolCraftStrings.spaced(this.id(), JolCraftStrings.plural(JolCraftDictionary.EVENT))
                );
            }
            this.tracking = null;
        });
    }

    @Override
    public void run(
            @NotNull JolCraftSoundProvider target,
            @Nullable PackOutput packOutput,
            @Nullable CompletableFuture<net.minecraft.core.HolderLookup.Provider> lookupProvider,
            @Nullable ExistingFileHelper existingFileHelper,
            @NotNull JolCraftDataTracking tracking
    ) {
        this.tracking = tracking;
        tracking.record(this, "sounds.json");
    }

    @Override
    public void registerSounds() {
        addDwarfVariants(JolCraftSounds.DWARF_AMBIENT, JolCraftLanguageKeys.SUBTITLE_DWARF_AMBIENT, 3);
        addDwarfVariants(JolCraftSounds.DWARF_HURT, JolCraftLanguageKeys.SUBTITLE_DWARF_HIT, 4);
        addDwarfSingle(JolCraftSounds.DWARF_DEATH, JolCraftLanguageKeys.SUBTITLE_DWARF_DEATH);

        addDwarfVariants(JolCraftSounds.DWARF_YES, JolCraftLanguageKeys.SUBTITLE_DWARF_YES, 3);
        addDwarfVariants(JolCraftSounds.DWARF_NO, JolCraftLanguageKeys.SUBTITLE_DWARF_NO, 3);
        addDwarfVariants(JolCraftSounds.DWARF_TRADE, JolCraftLanguageKeys.SUBTITLE_DWARF_TRADE, 3);

        addRandomSingle(JolCraftSounds.LEVEL_UP, JolCraftLanguageKeys.SUBTITLE_LEVEL_UP);

        addRange(
                JolCraftSounds.ARMOR_EQUIP_DEEPSLATE,
                JolCraftLanguageKeys.SUBTITLE_ARMOR_EQUIP_DEEPSLATE,
                vanillaDeepslateBrickPlaceBase(),
                1,
                6
        );

        addStrongboxRange(JolCraftSounds.STRONGBOX_OPEN, JolCraftLanguageKeys.SUBTITLE_STRONGBOX_OPEN, 1, 2);
        addStrongboxRange(JolCraftSounds.STRONGBOX_CLOSE, JolCraftLanguageKeys.SUBTITLE_STRONGBOX_CLOSE, 1, 2);
        addStrongboxRange(JolCraftSounds.STRONGBOX_LOCKPICK, JolCraftLanguageKeys.SUBTITLE_STRONGBOX_LOCKPICK, 1, 4);
        addStrongboxRange(JolCraftSounds.STRONGBOX_LOCKPICK_BREAK, JolCraftLanguageKeys.SUBTITLE_STRONGBOX_LOCKPICK_BREAK, 1, 3);
        addStrongboxRange(JolCraftSounds.STRONGBOX_UNLOCK, JolCraftLanguageKeys.SUBTITLE_STRONGBOX_UNLOCK, 1, 1);

        addCoinRange(JolCraftSounds.COIN_STACK, JolCraftLanguageKeys.SUBTITLE_COIN_STACK, 1, 4);
        addCoinRange(JolCraftSounds.COIN_SINGLE, JolCraftLanguageKeys.SUBTITLE_COIN_SINGLE, 1, 4);

        addVanillaList(
                JolCraftSounds.GEM_CUT,
                JolCraftLanguageKeys.SUBTITLE_GEM_CUT,
                vanillaSmithingTableList()
        );

        addCurseRange(JolCraftSounds.CURSE, JolCraftLanguageKeys.SUBTITLE_CURSE, 1, 10);
    }

    private void addDwarfVariants(Supplier<SoundEvent> event, String subtitleKey, int count) {
        String id = idPath(event);
        String leaf = stripPrefix(id, dwarfPrefix());
        addRange(event, subtitleKey, dwarfBase(leaf), 1, count);
    }

    private void addDwarfSingle(Supplier<SoundEvent> event, String subtitleKey) {
        String id = idPath(event);
        String leaf = stripPrefix(id, dwarfPrefix());
        addSingle(event, subtitleKey, dwarfSingle(leaf));
    }

    private void addStrongboxRange(Supplier<SoundEvent> event, String subtitleKey, int fromInclusive, int toInclusive) {
        addRange(event, subtitleKey, strongboxBase(idPath(event)), fromInclusive, toInclusive);
    }

    private void addCoinRange(Supplier<SoundEvent> event, String subtitleKey, int fromInclusive, int toInclusive) {
        addRange(event, subtitleKey, coinBase(idPath(event)), fromInclusive, toInclusive);
    }

    private void addCurseRange(Supplier<SoundEvent> event, String subtitleKey, int fromInclusive, int toInclusive) {
        addRange(event, subtitleKey, curseBase(idPath(event)), fromInclusive, toInclusive);
    }

    private void addRandomSingle(Supplier<SoundEvent> event, String subtitleKey) {
        String leaf = removeChar(idPath(event), UNDERSCORE);
        addSingle(event, subtitleKey, randomSingle(leaf));
    }

    private void addSingle(Supplier<SoundEvent> event, String subtitleKey, ResourceLocation soundName) {
        this.addedSoundEvents++;
        this.add(event.get(), definition().subtitle(subtitleKey).with(sound(soundId(soundName))));
    }

    private void addRange(Supplier<SoundEvent> event, String subtitleKey, ResourceLocation base, int fromInclusive, int toInclusive) {
        this.addedSoundEvents++;
        this.add(event.get(), definition().subtitle(subtitleKey).with(toSounds(numbered(base, fromInclusive, toInclusive))));
    }

    private void addVanillaList(Supplier<SoundEvent> event, String subtitleKey, List<ResourceLocation> sounds) {
        this.addedSoundEvents++;
        this.add(event.get(), definition().subtitle(subtitleKey).with(toSounds(sounds)));
    }

    private static SoundDefinition.Sound[] toSounds(List<ResourceLocation> names) {
        SoundDefinition.Sound[] out = new SoundDefinition.Sound[names.size()];
        for (int i = 0; i < names.size(); i++) {
            out[i] = sound(soundId(names.get(i)));
        }
        return out;
    }

    private static List<ResourceLocation> numbered(ResourceLocation base, int fromInclusive, int toInclusive) {
        List<ResourceLocation> out = new ArrayList<>(Math.max(0, toInclusive - fromInclusive + 1));
        String ns = base.getNamespace();
        String path = base.getPath();
        for (int i = fromInclusive; i <= toInclusive; i++) {
            out.add(ResourceLocation.fromNamespaceAndPath(ns, path + i));
        }
        return out;
    }

    private static String soundId(ResourceLocation id) {
        return id.toString();
    }

    private static ResourceLocation mod(String path) {
        return JolCraft.location(path);
    }

    private static ResourceLocation vanilla(String path) {
        return ResourceLocation.withDefaultNamespace(path);
    }

    private static String dwarfPrefix() {
        return JolCraftDictionary.DWARF + UNDERSCORE;
    }

    private static ResourceLocation dwarfBase(String leaf) {
        return mod(JolCraftStrings.slashed(JolCraftDictionary.ENTITY, JolCraftDictionary.DWARF, leaf));
    }

    private static ResourceLocation dwarfSingle(String leaf) {
        return dwarfBase(leaf);
    }

    private static ResourceLocation strongboxBase(String id) {
        return mod(JolCraftStrings.slashed(JolCraftDictionary.BLOCK, JolCraftDictionary.STRONGBOX, id));
    }

    private static ResourceLocation coinBase(String id) {
        return mod(JolCraftStrings.slashed(JolCraftDictionary.ITEM, JolCraftDictionary.COIN, id));
    }

    private static ResourceLocation curseBase(String id) {
        return mod(JolCraftStrings.slashed(JolCraftDictionary.CURSE, id));
    }

    private static ResourceLocation randomSingle(String leaf) {
        return mod(JolCraftStrings.slashed(JolCraftDictionary.RANDOM, leaf));
    }

    private static ResourceLocation vanillaDeepslateBrickPlaceBase() {
        String bricks = JolCraftStrings.underscored(
                JolCraftDictionary.DEEPSLATE,
                JolCraftStrings.plural(JolCraftDictionary.BRICK)
        );
        return vanilla(JolCraftStrings.slashed(JolCraftDictionary.BLOCK, bricks, JolCraftDictionary.PLACE));
    }

    private static List<ResourceLocation> vanillaSmithingTableList() {
        String smithingTable = JolCraftStrings.underscored(JolCraftDictionary.SMITHING, JolCraftDictionary.TABLE);
        String folder = JolCraftStrings.slashed(JolCraftDictionary.BLOCK, smithingTable);

        List<ResourceLocation> out = new ArrayList<>(2);
        out.add(vanilla(JolCraftStrings.slashed(folder, smithingTable + 1)));
        out.add(vanilla(JolCraftStrings.slashed(folder, smithingTable + 3)));
        return out;
    }

    private static String idPath(Supplier<SoundEvent> event) {
        DeferredHolder<SoundEvent, SoundEvent> holder = asDeferredHolder(event);
        ResourceLocation id = holder.getId();
        return id.getPath();
    }

    @SuppressWarnings("unchecked")
    private static DeferredHolder<SoundEvent, SoundEvent> asDeferredHolder(Supplier<SoundEvent> event) {
        if (event instanceof DeferredHolder<?, ?> deferred) {
            return (DeferredHolder<SoundEvent, SoundEvent>) deferred;
        }
        throw new IllegalArgumentException(
                "Sound supplier must be DeferredHolder to derive name (got: " + event.getClass().getName() + ")"
        );
    }

    private static String stripPrefix(String s, String prefix) {
        if (s == null) return "";
        if (prefix == null || prefix.isEmpty()) return s;
        return s.startsWith(prefix) ? s.substring(prefix.length()) : s;
    }

    private static String removeChar(String s, char c) {
        if (s == null || s.isEmpty()) return "";
        StringBuilder out = new StringBuilder(s.length());
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if (ch != c) out.append(ch);
        }
        return out.toString();
    }

    @Override
    public @NotNull String getName() {
        return name();
    }
}