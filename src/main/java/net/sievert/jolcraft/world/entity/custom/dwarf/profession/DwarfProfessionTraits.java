package net.sievert.jolcraft.world.entity.custom.dwarf.profession;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.sievert.jolcraft.config.custom.dwarf.DwarfProfessionConfig;
import net.sievert.jolcraft.config.custom.dwarf.DwarfProfessionConfigManager;
import net.sievert.jolcraft.world.entity.custom.dwarf.base.AbstractDwarfEntity;

import javax.annotation.Nullable;
import java.util.Optional;

public final class DwarfProfessionTraits {

    private DwarfProfessionTraits() {}

    // ---------------------------------------------------------
    // Config access
    // ---------------------------------------------------------

    public static DwarfProfessionConfig config(DwarfProfession profession) {
        return DwarfProfessionConfigManager.INSTANCE.get(profession);
    }

    // ---------------------------------------------------------
    // Simple fields
    // ---------------------------------------------------------

    public static int requiredTier(DwarfProfession profession) {
        return config(profession).requiredTier();
    }

    public static long restockTicks(DwarfProfession profession) {
        return config(profession).restockTicks();
    }

    public static float adultVoicePitch(DwarfProfession profession) {
        return config(profession).voicePitch();
    }

    public static boolean canReroll(DwarfProfession profession) {
        return config(profession).canReroll();
    }

    public static boolean canEndorseFlag(DwarfProfession profession) {
        return config(profession).canEndorse();
    }

    public static boolean showProgressBar(DwarfProfession profession) {
        return config(profession).showProgressBar();
    }

    public static boolean showLevel(DwarfProfession profession) {
        return config(profession).showLevel();
    }


    // ---------------------------------------------------------
    // Rules
    // ---------------------------------------------------------

    public static boolean canSign(AbstractDwarfEntity dwarf) {
        DwarfProfessionConfig cfg = config(dwarf.getProfession());
        return eval(cfg.rules().canSign(), dwarf);
    }

    public static boolean canTrade(AbstractDwarfEntity dwarf) {
        DwarfProfessionConfig cfg = config(dwarf.getProfession());
        return eval(cfg.rules().canTrade(), dwarf);
    }

    /**
     * Uses BOTH:
     * - the boolean gate (canEndorse)
     * - the rule gate (rules.canEndorse)
     */
    public static boolean canEndorse(AbstractDwarfEntity dwarf) {
        DwarfProfessionConfig cfg = config(dwarf.getProfession());
        return cfg.canEndorse() && eval(cfg.rules().canEndorse(), dwarf);
    }

    private static boolean eval(DwarfProfessionConfig.Rule rule, AbstractDwarfEntity dwarf) {
        if (rule instanceof DwarfProfessionConfig.Rule.Always) {
            return true;
        }
        if (rule instanceof DwarfProfessionConfig.Rule.MinMerchantLevel(int level)) {
            return dwarf.getMerchantLevel() >= level;
        }
        throw new IllegalStateException("Unhandled rule type: " + rule.getClass().getName());
    }

    // ---------------------------------------------------------
    // Sounds
    // ---------------------------------------------------------

    @Nullable
    public static SoundEvent restockSound(AbstractDwarfEntity dwarf) {
        DwarfProfessionConfig cfg = config(dwarf.getProfession());
        return resolveSound(dwarf, cfg.sounds().restock());
    }

    @Nullable
    public static SoundEvent rerollSound(AbstractDwarfEntity dwarf) {
        DwarfProfessionConfig cfg = config(dwarf.getProfession());
        return resolveSound(dwarf, cfg.sounds().reroll());
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    @Nullable
    private static SoundEvent resolveSound(AbstractDwarfEntity dwarf, Optional<ResourceLocation> idOpt) {
        if (idOpt.isEmpty()) return null;

        HolderLookup.RegistryLookup<SoundEvent> lookup = dwarf.level().registryAccess().lookupOrThrow(Registries.SOUND_EVENT);

        ResourceKey<SoundEvent> key = ResourceKey.create(Registries.SOUND_EVENT, idOpt.get());
        return lookup.get(key).map(Holder::value).orElse(null);
    }
}