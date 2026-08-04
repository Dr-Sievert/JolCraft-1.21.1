package net.sievert.jolcraft.world.entity.custom.dwarf.profession;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.sievert.jolcraft.config.custom.dwarf.DwarfProfessionConfig;
import net.sievert.jolcraft.config.custom.dwarf.DwarfProfessionConfigManager;
import net.sievert.jolcraft.config.custom.dwarf.rule.DwarfProfessionRule;
import net.sievert.jolcraft.world.entity.custom.dwarf.base.AbstractDwarfEntity;
import net.sievert.jolcraft.world.entity.custom.dwarf.trade.DwarfMerchantData.Level;

import javax.annotation.Nullable;
import java.util.Optional;

public final class DwarfProfessionTraits {

    private DwarfProfessionTraits() {}

    public static DwarfProfessionConfig config(
            DwarfProfession profession
    ) {
        return DwarfProfessionConfigManager.INSTANCE.get(profession);
    }

    public static int requiredTier(
            DwarfProfession profession
    ) {
        return config(profession).requiredTier();
    }

    public static long restockTicks(
            DwarfProfession profession
    ) {
        return config(profession).restockTicks();
    }

    public static float adultVoicePitch(
            DwarfProfession profession
    ) {
        return config(profession).voicePitch();
    }

    public static boolean canReroll(
            DwarfProfession profession
    ) {
        return config(profession).canReroll();
    }

    public static boolean canEndorseFlag(
            DwarfProfession profession
    ) {
        return config(profession).canEndorse();
    }

    public static boolean showProgressBar(
            DwarfProfession profession
    ) {
        return config(profession).showProgressBar();
    }

    public static boolean showLevel(
            DwarfProfession profession
    ) {
        return config(profession).showLevel();
    }

    public static boolean canSign(
            AbstractDwarfEntity dwarf
    ) {
        DwarfProfessionConfig config =
                config(dwarf.getProfession());

        return evaluate(
                config.rules().canSign(),
                dwarf
        );
    }

    public static boolean canTrade(
            AbstractDwarfEntity dwarf
    ) {
        DwarfProfessionConfig config =
                config(dwarf.getProfession());

        return evaluate(
                config.rules().canTrade(),
                dwarf
        );
    }

    public static boolean canEndorse(
            AbstractDwarfEntity dwarf
    ) {
        DwarfProfessionConfig config =
                config(dwarf.getProfession());

        return config.canEndorse()
                && evaluate(
                config.rules().canEndorse(),
                dwarf
        );
    }

    private static boolean evaluate(
            DwarfProfessionRule rule,
            AbstractDwarfEntity dwarf
    ) {
        if (rule instanceof DwarfProfessionRule.Always) {
            return true;
        }

        if (
                rule instanceof
                        DwarfProfessionRule.MinMerchantLevel(
                                Level minimumLevel
                        )
        ) {
            return dwarf.getMerchantLevel()
                    >= minimumLevel.getId();
        }

        throw new IllegalStateException(
                "Unhandled rule type: "
                        + rule.getClass().getName()
        );
    }

    @Nullable
    public static SoundEvent restockSound(
            AbstractDwarfEntity dwarf
    ) {
        DwarfProfessionConfig config =
                config(dwarf.getProfession());

        return resolveSound(
                dwarf,
                config.sounds().restock()
        );
    }

    @Nullable
    public static SoundEvent rerollSound(
            AbstractDwarfEntity dwarf
    ) {
        DwarfProfessionConfig config =
                config(dwarf.getProfession());

        return resolveSound(
                dwarf,
                config.sounds().reroll()
        );
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    @Nullable
    private static SoundEvent resolveSound(
            AbstractDwarfEntity dwarf,
            Optional<ResourceLocation> id
    ) {
        if (id.isEmpty()) {
            return null;
        }

        HolderLookup.RegistryLookup<SoundEvent> lookup =
                dwarf.level()
                        .registryAccess()
                        .lookupOrThrow(
                                Registries.SOUND_EVENT
                        );

        ResourceKey<SoundEvent> key =
                ResourceKey.create(
                        Registries.SOUND_EVENT,
                        id.get()
                );

        return lookup.get(key)
                .map(Holder::value)
                .orElse(null);
    }
}