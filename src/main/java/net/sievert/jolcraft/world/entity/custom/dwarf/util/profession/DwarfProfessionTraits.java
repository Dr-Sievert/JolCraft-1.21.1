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

    public record BountyRewardParticles(float r, float g, float b, float scale) {}

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
            boolean canBountyInteract,
            @Nullable SoundEvent restockSound,
            @Nullable SoundEvent rerollSound,
            @Nullable SoundEvent bountyRewardSound,
            @Nullable BountyRewardParticles bountyRewardParticles
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
                false,
                SoundEvents.VILLAGER_WORK_FISHERMAN,
                SoundEvents.VILLAGER_WORK_FISHERMAN,
                null,
                null
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
                () -> ItemStack.EMPTY,
                defaults.requiredTier(),
                defaults.canReroll(),
                defaults.adultVoicePitch(),
                defaults.showProgressBar(),
                true,
                defaults.canSign(),
                defaults.canEndorse(),
                defaults.canTrade(),
                defaults.canBountyInteract(),
                defaults.restockSound(),
                defaults.rerollSound(),
                defaults.bountyRewardSound(),
                defaults.bountyRewardParticles()
        );

        // GUILDMASTER
        override(DwarfProfession.GUILDMASTER,
                () -> new ItemStack(JolCraftItems.CONTRACT_GUILDMASTER.get()),
                defaults.requiredTier(), false, 0.8F, false, true,
                defaults.canSign(),
                defaults.canEndorse(),
                defaults.canTrade(),
                defaults.canBountyInteract(),
                SoundEvents.VILLAGER_WORK_CARTOGRAPHER,
                SoundEvents.VILLAGER_WORK_CARTOGRAPHER,
                defaults.bountyRewardSound(),
                defaults.bountyRewardParticles()
        );

        // ALCHEMIST
        override(DwarfProfession.ALCHEMIST,
                () -> new ItemStack(JolCraftItems.CONTRACT_ALCHEMIST.get()),
                3, true, 1.1F, true, false,
                defaults.canSign(),
                defaults.canEndorse(),
                defaults.canTrade(),
                defaults.canBountyInteract(),
                SoundEvents.VILLAGER_WORK_CLERIC,
                SoundEvents.VILLAGER_WORK_CLERIC,
                defaults.bountyRewardSound(),
                defaults.bountyRewardParticles()
        );

        // ARCANIST
        override(DwarfProfession.ARCANIST,
                () -> new ItemStack(JolCraftItems.CONTRACT_ARCANIST.get()),
                3, true, 0.85F, true, false,
                defaults.canSign(),
                defaults.canEndorse(),
                defaults.canTrade(),
                defaults.canBountyInteract(),
                SoundEvents.VILLAGER_WORK_LIBRARIAN,
                SoundEvents.VILLAGER_WORK_LIBRARIAN,
                defaults.bountyRewardSound(),
                defaults.bountyRewardParticles()
        );

        // ARTISAN
        override(DwarfProfession.ARTISAN,
                () -> new ItemStack(JolCraftItems.CONTRACT_ARTISAN.get()),
                2, true, 0.9F, true, false,
                defaults.canSign(),
                defaults.canEndorse(),
                defaults.canTrade(),
                defaults.canBountyInteract(),
                SoundEvents.VILLAGER_WORK_TOOLSMITH,
                SoundEvents.VILLAGER_WORK_TOOLSMITH,
                defaults.bountyRewardSound(),
                defaults.bountyRewardParticles()
        );

        // BREWMASTER
        override(DwarfProfession.BREWMASTER,
                () -> new ItemStack(JolCraftItems.CONTRACT_BREWMASTER.get()),
                1, true, 0.9F, true, false,
                defaults.canSign(),
                defaults.canEndorse(),
                defaults.canTrade(),
                defaults.canBountyInteract(),
                SoundEvents.VILLAGER_WORK_CLERIC,
                SoundEvents.VILLAGER_WORK_CLERIC,
                defaults.bountyRewardSound(),
                defaults.bountyRewardParticles()
        );

        // EXPLORER
        override(DwarfProfession.EXPLORER,
                () -> new ItemStack(JolCraftItems.CONTRACT_EXPLORER.get()),
                2, true, defaults.adultVoicePitch(), false, false,
                defaults.canSign(),
                defaults.canEndorse(),
                defaults.canTrade(),
                defaults.canBountyInteract(),
                SoundEvents.VILLAGER_WORK_CARTOGRAPHER,
                SoundEvents.VILLAGER_WORK_CARTOGRAPHER,
                defaults.bountyRewardSound(),
                defaults.bountyRewardParticles()
        );

        // GUARD
        override(DwarfProfession.GUARD,
                () -> new ItemStack(JolCraftItems.CONTRACT_GUARD.get()),
                1, false, 0.7F, true, false,
                defaults.canSign(),
                defaults.canEndorse(),
                dwarf -> dwarf.getMerchantLevel() >= 5,
                defaults.canBountyInteract(),
                SoundEvents.VILLAGER_WORK_WEAPONSMITH,
                SoundEvents.VILLAGER_WORK_WEAPONSMITH,
                defaults.bountyRewardSound(),
                defaults.bountyRewardParticles()
        );

        // HISTORIAN
        override(DwarfProfession.HISTORIAN,
                () -> new ItemStack(JolCraftItems.CONTRACT_HISTORIAN.get()),
                defaults.requiredTier(), true, 1.1F, true, false,
                defaults.canSign(),
                defaults.canEndorse(),
                defaults.canTrade(),
                defaults.canBountyInteract(),
                SoundEvents.VILLAGER_WORK_LIBRARIAN,
                SoundEvents.VILLAGER_WORK_LIBRARIAN,
                defaults.bountyRewardSound(),
                defaults.bountyRewardParticles()
        );

        // KEEPER
        override(DwarfProfession.KEEPER,
                () -> new ItemStack(JolCraftItems.CONTRACT_KEEPER.get()),
                1, true, defaults.adultVoicePitch(), true, false,
                defaults.canSign(),
                defaults.canEndorse(),
                defaults.canTrade(),
                defaults.canBountyInteract(),
                SoundEvents.VILLAGER_WORK_FARMER,
                SoundEvents.VILLAGER_WORK_FARMER,
                defaults.bountyRewardSound(),
                defaults.bountyRewardParticles()
        );

        // MERCHANT
        override(DwarfProfession.MERCHANT,
                () -> new ItemStack(JolCraftItems.CONTRACT_MERCHANT.get()),
                defaults.requiredTier(), false, defaults.adultVoicePitch(), true, false,
                defaults.canSign(),
                defaults.canEndorse(),
                defaults.canTrade(),
                true,
                defaults.restockSound(),
                defaults.rerollSound(),
                SoundEvents.VILLAGER_WORK_FISHERMAN,
                new BountyRewardParticles(1.0F, 0.84F, 0.0F, 0.5F)
        );

        // MINER
        override(DwarfProfession.MINER,
                () -> new ItemStack(JolCraftItems.CONTRACT_MINER.get()),
                2, false, 1.1F, true, false,
                defaults.canSign(),
                defaults.canEndorse(),
                defaults.canTrade(),
                true,
                SoundEvents.VILLAGER_WORK_MASON,
                SoundEvents.VILLAGER_WORK_MASON,
                SoundEvents.VILLAGER_WORK_MASON,
                new BountyRewardParticles(0.25F, 0.25F, 0.30F, 0.7F)
        );

        // PRIEST
        override(DwarfProfession.PRIEST,
                () -> new ItemStack(JolCraftItems.CONTRACT_PRIEST.get()),
                3, true, 0.9F, true, false,
                defaults.canSign(),
                defaults.canEndorse(),
                defaults.canTrade(),
                defaults.canBountyInteract(),
                SoundEvents.VILLAGER_WORK_LIBRARIAN,
                SoundEvents.VILLAGER_WORK_LIBRARIAN,
                defaults.bountyRewardSound(),
                defaults.bountyRewardParticles()
        );

        // SCRAPPER
        override(DwarfProfession.SCRAPPER,
                () -> new ItemStack(JolCraftItems.CONTRACT_SCRAPPER.get()),
                defaults.requiredTier(), true, 1.4F, true, false,
                defaults.canSign(),
                defaults.canEndorse(),
                defaults.canTrade(),
                defaults.canBountyInteract(),
                SoundEvents.VILLAGER_WORK_TOOLSMITH,
                SoundEvents.VILLAGER_WORK_TOOLSMITH,
                defaults.bountyRewardSound(),
                defaults.bountyRewardParticles()
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
            boolean canBountyInteract,
            @Nullable SoundEvent restockSound,
            @Nullable SoundEvent rerollSound,
            @Nullable SoundEvent bountyRewardSound,
            @Nullable BountyRewardParticles bountyRewardParticles
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
                canBountyInteract,
                restockSound,
                rerollSound,
                bountyRewardSound,
                bountyRewardParticles
        ));
    }
}