package net.sievert.jolcraft.world.entity.custom.dwarf.util.profession;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.world.entity.custom.dwarf.base.AbstractDwarfEntity;
import net.sievert.jolcraft.world.item.JolCraftItems;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Predicate;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class DwarfProfessionTraits {

    // -------------------------------------------------------------------------
    // Traits
    // -------------------------------------------------------------------------

    public record Traits(
            ContractItem contract,
            int requiredTier,
            boolean canReroll,
            float adultVoicePitch,
            boolean showProgressBar,
            boolean neverEndorse,
            Predicate<AbstractDwarfEntity> canSign,
            Predicate<AbstractDwarfEntity> canEndorse,
            Predicate<AbstractDwarfEntity> canTrade,
            @Nullable SoundEvent restockSound,
            @Nullable SoundEvent rerollSound
    ) {}

    @FunctionalInterface
    public interface ContractItem {
        ItemStack create();
    }

    // -------------------------------------------------------------------------
    // Registry
    // -------------------------------------------------------------------------

    private static final Map<DwarfProfession, Traits> TRAITS = new EnumMap<>(DwarfProfession.class);

    static {
        // Defaults
        Traits defaults = new Traits(
                () -> new ItemStack(JolCraftItems.CONTRACT_SIGNED.get()),
                0,
                true,
                1.0F,
                true,
                false,
                dwarf -> true,
                dwarf -> dwarf.getMerchantLevel() >= 1,
                dwarf -> true,
                SoundEvents.VILLAGER_WORK_FISHERMAN,
                SoundEvents.VILLAGER_WORK_FISHERMAN
        );

        // Start by applying defaults to all professions
        for (DwarfProfession profession : DwarfProfession.values()) {
            TRAITS.put(profession, defaults);
        }

        // -----------------------------------------------------------------
        // Profession overrides (mirrors current profession entity subclasses)
        // -----------------------------------------------------------------

        // BASE
        override(DwarfProfession.NONE,
                () -> new ItemStack(ItemStack.EMPTY.getItem()),
                defaults.requiredTier(),
                defaults.canReroll(),
                defaults.adultVoicePitch(),
                defaults.showProgressBar(),
                true,
                defaults.canSign(),
                defaults.canEndorse(),
                defaults.canTrade(),
                defaults.restockSound(),
                defaults.rerollSound()
        );

        // GUILDMASTER
        override(DwarfProfession.GUILDMASTER,
                () -> new ItemStack(JolCraftItems.CONTRACT_GUILDMASTER.get()),
                defaults.requiredTier(), false, 0.8F, false, true,
                defaults.canSign(),
                defaults.canEndorse(),
                defaults.canTrade(),
                SoundEvents.VILLAGER_WORK_CARTOGRAPHER,
                SoundEvents.VILLAGER_WORK_CARTOGRAPHER
        );

        // ALCHEMIST
        override(DwarfProfession.ALCHEMIST,
                () -> new ItemStack(JolCraftItems.CONTRACT_ALCHEMIST.get()),
                3, true, 1.1F, true, false,
                defaults.canSign(),
                defaults.canEndorse(),
                defaults.canTrade(),
                SoundEvents.VILLAGER_WORK_CLERIC,
                SoundEvents.VILLAGER_WORK_CLERIC
        );

        // ARCANIST
        override(DwarfProfession.ARCANIST,
                () -> new ItemStack(JolCraftItems.CONTRACT_ARCANIST.get()),
                3, true, 0.85F, true, false,
                defaults.canSign(),
                defaults.canEndorse(),
                defaults.canTrade(),
                SoundEvents.VILLAGER_WORK_LIBRARIAN,
                SoundEvents.VILLAGER_WORK_LIBRARIAN
        );

        // ARTISAN
        override(DwarfProfession.ARTISAN,
                () -> new ItemStack(JolCraftItems.CONTRACT_ARTISAN.get()),
                2, true, 0.9F, true, false,
                defaults.canSign(),
                defaults.canEndorse(),
                defaults.canTrade(),
                SoundEvents.VILLAGER_WORK_TOOLSMITH,
                SoundEvents.VILLAGER_WORK_TOOLSMITH
        );

        // BREWMASTER
        override(DwarfProfession.BREWMASTER,
                () -> new ItemStack(JolCraftItems.CONTRACT_BREWMASTER.get()),
                1, true, 0.9F, true, false,
                defaults.canSign(),
                defaults.canEndorse(),
                defaults.canTrade(),
                SoundEvents.VILLAGER_WORK_CLERIC,
                SoundEvents.VILLAGER_WORK_CLERIC
        );

        // EXPLORER
        override(DwarfProfession.EXPLORER,
                () -> new ItemStack(JolCraftItems.CONTRACT_EXPLORER.get()),
                2, true, defaults.adultVoicePitch(), false, false,
                defaults.canSign(),
                defaults.canEndorse(),
                defaults.canTrade(),
                SoundEvents.VILLAGER_WORK_CARTOGRAPHER,
                SoundEvents.VILLAGER_WORK_CARTOGRAPHER
        );

        // GUARD
        override(DwarfProfession.GUARD,
                () -> new ItemStack(JolCraftItems.CONTRACT_GUARD.get()),
                1, false, 0.7F, true, false,
                defaults.canSign(),
                defaults.canEndorse(),
                dwarf -> dwarf.getMerchantLevel() >= 5,
                SoundEvents.VILLAGER_WORK_WEAPONSMITH,
                SoundEvents.VILLAGER_WORK_WEAPONSMITH
        );

        // HISTORIAN
        override(DwarfProfession.HISTORIAN,
                () -> new ItemStack(JolCraftItems.CONTRACT_HISTORIAN.get()),
                defaults.requiredTier(), true, 1.1F, true, false,
                defaults.canSign(),
                defaults.canEndorse(),
                defaults.canTrade(),
                SoundEvents.VILLAGER_WORK_LIBRARIAN,
                SoundEvents.VILLAGER_WORK_LIBRARIAN
        );

        // KEEPER
        override(DwarfProfession.KEEPER,
                () -> new ItemStack(JolCraftItems.CONTRACT_KEEPER.get()),
                1, true, defaults.adultVoicePitch(), true, false,
                defaults.canSign(),
                defaults.canEndorse(),
                defaults.canTrade(),
                SoundEvents.VILLAGER_WORK_FARMER,
                SoundEvents.VILLAGER_WORK_FARMER
        );

        // MERCHANT
        override(DwarfProfession.MERCHANT,
                () -> new ItemStack(JolCraftItems.CONTRACT_MERCHANT.get()),
                defaults.requiredTier(), false, defaults.adultVoicePitch(), true, false,
                defaults.canSign(),
                defaults.canEndorse(),
                defaults.canTrade(),
                defaults.restockSound(),
                defaults.rerollSound()
        );

        // MINER
        override(DwarfProfession.MINER,
                () -> new ItemStack(JolCraftItems.CONTRACT_MINER.get()),
                2, false, 1.1F, true, false,
                defaults.canSign(),
                defaults.canEndorse(),
                defaults.canTrade(),
                SoundEvents.VILLAGER_WORK_MASON,
                SoundEvents.VILLAGER_WORK_MASON
        );

        // PRIEST
        override(DwarfProfession.PRIEST,
                () -> new ItemStack(JolCraftItems.CONTRACT_PRIEST.get()),
                3, true, 0.9F, true, false,
                defaults.canSign(),
                defaults.canEndorse(),
                defaults.canTrade(),
                SoundEvents.VILLAGER_WORK_LIBRARIAN,
                SoundEvents.VILLAGER_WORK_LIBRARIAN
        );

        // SCRAPPER
        override(DwarfProfession.SCRAPPER,
                () -> new ItemStack(JolCraftItems.CONTRACT_SCRAPPER.get()),
                defaults.requiredTier(), true, 1.4F, true, false,
                defaults.canSign(),
                defaults.canEndorse(),
                defaults.canTrade(),
                SoundEvents.VILLAGER_WORK_TOOLSMITH,
                SoundEvents.VILLAGER_WORK_TOOLSMITH
        );
    }

    private DwarfProfessionTraits() {}

    public static Traits of(DwarfProfession profession) {
        return TRAITS.getOrDefault(profession, TRAITS.get(DwarfProfession.NONE));
    }

    // -------------------------------------------------------------------------
    // Internals
    // -------------------------------------------------------------------------

    private static void override(
            DwarfProfession profession,
            ContractItem contract,
            int requiredTier,
            boolean canReroll,
            float adultVoicePitch,
            boolean showProgressBar,
            boolean neverEndorse,
            Predicate<AbstractDwarfEntity> canSign,
            Predicate<AbstractDwarfEntity> canEndorse,
            Predicate<AbstractDwarfEntity> canTrade,
            @Nullable SoundEvent restockSound,
            @Nullable SoundEvent rerollSound
    ) {
        TRAITS.put(profession, new Traits(
                contract,
                requiredTier,
                canReroll,
                adultVoicePitch,
                showProgressBar,
                neverEndorse,
                canSign,
                canEndorse,
                canTrade,
                restockSound,
                rerollSound
        ));
    }
}