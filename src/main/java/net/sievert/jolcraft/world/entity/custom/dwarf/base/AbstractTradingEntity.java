package net.sievert.jolcraft.world.entity.custom.dwarf.base;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.sievert.jolcraft.world.player.JolCraftStats;
import net.sievert.jolcraft.world.player.advancement.JolCraftCriteriaTriggers;
import net.sievert.jolcraft.config.custom.dwarf.trade.DwarfProfessionTradePoolConfig;
import net.sievert.jolcraft.config.custom.dwarf.trade.TradePoolType;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.world.recipe.custom.dwarf_trade.DwarfTradeRecipe;
import net.sievert.jolcraft.network.JolCraftNetworking;
import net.sievert.jolcraft.network.packet.s2c.ClientboundDwarfMerchantOffersPacket;
import net.sievert.jolcraft.util.log.JolCraftLogTags;
import net.sievert.jolcraft.util.log.JolCraftLogs;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfessionTraits;
import net.sievert.jolcraft.world.entity.custom.dwarf.trade.DwarfMerchant;
import net.sievert.jolcraft.world.entity.custom.dwarf.trade.DwarfMerchantData;
import net.sievert.jolcraft.world.entity.custom.dwarf.trade.DwarfMerchantOffer;
import net.sievert.jolcraft.world.entity.custom.dwarf.trade.DwarfMerchantOffers;
import net.sievert.jolcraft.world.entity.custom.dwarf.trade.DwarfTrades;
import net.sievert.jolcraft.world.gui.menu.DwarfMerchantMenu;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.sound.JolCraftSounds;
import net.sievert.jolcraft.world.sound.util.JolCraftSoundHelper;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class AbstractTradingEntity extends AbstractBreedingEntity implements DwarfMerchant {

    private static final String NBT_XP = JolCraftDictionary.XP;
    private static final String NBT_OFFERS = JolCraftStrings.plural(JolCraftDictionary.OFFER);
    private static final String NBT_MERCHANT_DATA =
            JolCraftStrings.underscored(JolCraftDictionary.MERCHANT, JolCraftDictionary.DATA);
    private static final String NBT_LAST_RESTOCK_TIME =
            JolCraftStrings.underscored(
                    JolCraftDictionary.LAST,
                    JolCraftDictionary.RESTOCK,
                    JolCraftDictionary.TIME
            );

    private static final long TRADE_SOUND_COOLDOWN_TICKS = 8L;

    public static final EntityDataAccessor<Integer> DATA_MERCHANT_LEVEL =
            SynchedEntityData.defineId(AbstractTradingEntity.class, EntityDataSerializers.INT);

    private long lastTradeSoundGameTime = Long.MIN_VALUE;

    public long lastRestockGameTime;
    public int dwarfXp;
    public int updateMerchantTimer = 0;
    public boolean increaseProfessionLevelOnUpdate = false;

    @Nullable
    private Player tradingPlayer;

    @Nullable
    public Player lastTradedPlayer;

    @Nullable
    protected DwarfMerchantOffers offers;

    protected AbstractTradingEntity(EntityType<? extends AgeableMob> entityType, Level level) {
        super(entityType, level);
        this.lastRestockGameTime = level.getGameTime();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_MERCHANT_LEVEL, DwarfMerchantData.MIN_MERCHANT_LEVEL);
    }

    public int getMerchantLevel() {
        return getData(DATA_MERCHANT_LEVEL);
    }

    public void setMerchantLevel(int level) {
        setData(DATA_MERCHANT_LEVEL, level);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);

        DwarfMerchantData.CODEC
                .encodeStart(NbtOps.INSTANCE, new DwarfMerchantData(this.getMerchantLevel()))
                .resultOrPartial(msg -> JolCraftLogs.error(JolCraftLogTags.ENTITY, "{}", msg))
                .ifPresent(tag -> compound.put(NBT_MERCHANT_DATA, tag));

        compound.putInt(NBT_XP, this.dwarfXp);
        compound.putLong(NBT_LAST_RESTOCK_TIME, this.lastRestockGameTime);

        if (!this.level().isClientSide && this.offers != null && !this.offers.isEmpty()) {
            compound.put(
                    NBT_OFFERS,
                    DwarfMerchantOffers.CODEC
                            .encodeStart(this.registryAccess().createSerializationContext(NbtOps.INSTANCE), this.offers)
                            .getOrThrow()
            );
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);

        if (compound.contains(NBT_MERCHANT_DATA, 10)) {
            DwarfMerchantData.CODEC
                    .parse(NbtOps.INSTANCE, compound.get(NBT_MERCHANT_DATA))
                    .resultOrPartial(msg -> JolCraftLogs.error(JolCraftLogTags.ENTITY, "{}", msg))
                    .ifPresent(data -> setMerchantLevel(data.level()));
        }

        if (compound.contains(NBT_XP, 3)) {
            this.dwarfXp = compound.getInt(NBT_XP);
        }

        if (compound.contains(NBT_LAST_RESTOCK_TIME, 4)) {
            this.lastRestockGameTime = compound.getLong(NBT_LAST_RESTOCK_TIME);
        }

        if (compound.contains(NBT_OFFERS)) {
            DwarfMerchantOffers.CODEC
                    .parse(this.registryAccess().createSerializationContext(NbtOps.INSTANCE), compound.get(NBT_OFFERS))
                    .resultOrPartial(Util.prefix(
                            "Failed to load offers: ",
                            msg -> JolCraftLogs.warn(JolCraftLogTags.ENTITY, "{}", msg)
                    ))
                    .ifPresent(loaded -> this.offers = loaded);
        }
    }

    public boolean canTrade() {
        if (this instanceof AbstractDwarfEntity dwarf) {
            return DwarfProfessionTraits.canTrade(dwarf);
        }
        return false;
    }

    public boolean canReroll() {
        return DwarfProfessionTraits.canReroll(this.getTradeProfession());
    }

    @SuppressWarnings("UnusedReturnValue")
    public boolean crateRestock() {
        return restock();
    }

    @Override
    public void setTradingPlayer(@Nullable Player player) {
        this.tradingPlayer = player;
    }

    @Nullable
    @Override
    public Player getTradingPlayer() {
        return this.tradingPlayer;
    }

    public boolean isTrading() {
        return this.tradingPlayer != null;
    }

    @Override
    public int getDwarfXp() {
        return this.dwarfXp;
    }

    @Override
    public boolean showProgressBar() {
        return DwarfProfessionTraits.showProgressBar(this.getTradeProfession());
    }

    public boolean showLevel() {
        return DwarfProfessionTraits.showLevel(this.getTradeProfession());
    }

    protected void stopTrading() {
        this.setTradingPlayer(null);
    }

    @Override
    public boolean isClientSide() {
        return this.level().isClientSide;
    }

    @Override
    public void die(DamageSource cause) {
        if (!this.level().isClientSide) {
            JolCraftLogs.info(
                    JolCraftLogTags.ENTITY,
                    "{} at {} in {}",
                    this.getCombatTracker().getDeathMessage().getString(),
                    JolCraftLogs.roundedPos(this),
                    this.level().dimension().location()
            );
        }

        super.die(cause);
        this.stopTrading();
    }

    @Nullable
    @Override
    public SoundEvent getNotifyTradeSound() {
        return JolCraftSounds.DWARF_YES.get();
    }

    @Nullable
    protected SoundEvent getTradeUpdatedSound(boolean isYesSound) {
        return isYesSound ? JolCraftSounds.DWARF_YES.get() : JolCraftSounds.DWARF_NO.get();
    }

    public SoundEvent getRestockSound() {
        SoundEvent sound = null;
        if (this instanceof AbstractDwarfEntity dwarf) {
            sound = DwarfProfessionTraits.restockSound(dwarf);
        }
        return sound != null ? sound : SoundEvents.VILLAGER_WORK_FISHERMAN;
    }

    public SoundEvent getRerollSound() {
        SoundEvent sound = null;
        if (this instanceof AbstractDwarfEntity dwarf) {
            sound = DwarfProfessionTraits.rerollSound(dwarf);
        }
        return sound != null ? sound : SoundEvents.VILLAGER_WORK_FISHERMAN;
    }

    public final DwarfProfession getTradeProfession() {
        if (this instanceof AbstractDwarfEntity dwarf) {
            return dwarf.getProfession();
        }
        throw new IllegalStateException("Trading entity is not a dwarf: " + this);
    }

    @Override
    public boolean stillValid(Player player) {
        return this.getTradingPlayer() == player && this.isAlive() && player.canInteractWithEntity(this, 4.0);
    }

    public void updateTrades() {
        rebuildTrades(DwarfTrades.RefreshMode.FULL);
    }

    private void rebuildTrades(DwarfTrades.RefreshMode mode) {
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        DwarfMerchantOffers rebuilt = new DwarfMerchantOffers();

        DwarfProfession profession = getTradeProfession();
        DwarfMerchantData.Level level = DwarfMerchantData.Level.fromId(this.getMerchantLevel());

        var recipes = DwarfTrades.getTradeRecipesForMode(serverLevel, profession, level, this.random, mode);

        JolCraftLogs.info(
                JolCraftLogTags.ENTITY,
                "Rebuilding dwarf trades for profession={} level={} mode={} recipeCount={}",
                profession,
                level,
                mode,
                recipes.size()
        );

        for (var holder : recipes) {
            addOfferFromRecipe(
                    rebuilt,
                    holder
            );
        }

        JolCraftLogs.info(
                JolCraftLogTags.ENTITY,
                "Finished rebuilding dwarf trades for profession={} level={} mode={} offerCount={}",
                profession,
                level,
                mode,
                rebuilt.size()
        );

        this.offers = rebuilt;
    }

    private void updateTradesForLevelUp() {
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        List<DwarfMerchantOffer> preservedGlobalOffers =
                new ArrayList<>();

        Set<ResourceLocation> existingGlobalRecipeIds =
                new HashSet<>();

        if (this.offers != null) {
            for (DwarfMerchantOffer offer : this.offers) {
                if (offer.getTradeGroup()
                        != DwarfTradeRecipe.TradeGroup.GLOBAL_POOL) {
                    continue;
                }

                preservedGlobalOffers.add(offer);
                existingGlobalRecipeIds.add(
                        offer.getSourceRecipeId()
                );
            }
        }

        /*
         * Rebuild the regular trades using their existing behavior.
         * The temporary global selection produced by FULL is removed below.
         */
        rebuildTrades(
                DwarfTrades.RefreshMode.FULL
        );

        if (this.offers == null) {
            this.offers =
                    new DwarfMerchantOffers();
        }

        /*
         * Discard the newly rerolled global offers generated by FULL.
         */
        this.offers.removeIf(
                offer -> offer.getTradeGroup()
                        == DwarfTradeRecipe.TradeGroup.GLOBAL_POOL
        );

        /*
         * Restore the existing global offers without copying them,
         * preserving their uses, demand and other runtime state.
         */
        this.offers.addAll(
                preservedGlobalOffers
        );

        DwarfProfession profession =
                getTradeProfession();

        DwarfMerchantData.Level level =
                DwarfMerchantData.Level.fromId(
                        this.getMerchantLevel()
                );

        var additionalGlobalRecipes =
                DwarfTrades.getAdditionalGlobalTradeRecipes(
                        serverLevel,
                        profession,
                        level,
                        this.random,
                        existingGlobalRecipeIds
                );

        for (var holder : additionalGlobalRecipes) {
            addOfferFromRecipe(
                    this.offers,
                    holder
            );
        }

        JolCraftLogs.info(
                JolCraftLogTags.ENTITY,
                "Added global dwarf trades for profession={} level={} previousGlobalCount={} addedGlobalCount={}",
                profession,
                level,
                preservedGlobalOffers.size(),
                additionalGlobalRecipes.size()
        );
    }

    private void addOfferFromRecipe(
            @NotNull DwarfMerchantOffers target,
            @NotNull RecipeHolder<DwarfTradeRecipe> recipe
    ) {
        DwarfMerchantOffer offer =
                new DwarfTrades.RecipeListing(
                        recipe
                ).getOffer(this);

        if (offer != null) {
            target.add(offer);
        }
    }

    public void resendOffersToTradingPlayer() {
        DwarfMerchantOffers merchantOffers = this.getOffers();
        Player player = this.getTradingPlayer();

        if (player instanceof ServerPlayer serverPlayer && !merchantOffers.isEmpty()) {
            JolCraftNetworking.sendToClient(
                    serverPlayer,
                    new ClientboundDwarfMerchantOffersPacket(
                            serverPlayer.containerMenu.containerId,
                            merchantOffers,
                            this.getMerchantLevel(),
                            this.getDwarfXp(),
                            this.showProgressBar(),
                            this.showLevel(),
                            this.canRestock()
                    )
            );
        }
    }

    public boolean shouldRestock() {
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return false;
        }

        long interval = DwarfProfessionTraits.restockTicks(this.getTradeProfession());
        return serverLevel.getGameTime() >= this.lastRestockGameTime + interval;
    }

    private boolean rerollsOnRestock(@Nullable DwarfTradeRecipe.TradeGroup group) {
        if (group == null) {
            return false;
        }

        TradePoolType type = group.poolType();
        if (type == null) {
            return false;
        }

        return DwarfProfessionTraits.config(this.getTradeProfession())
                .tradePools()
                .get(type)
                .map(DwarfProfessionTradePoolConfig::rerollsOnRestock)
                .orElse(false);
    }

    private boolean isRestockRerollOffer(@Nullable DwarfMerchantOffer offer) {
        if (offer == null) {
            return false;
        }
        return rerollsOnRestock(offer.getTradeGroup());
    }

    private boolean hasAnyOfferNeedingUseReset(DwarfMerchantOffers currentOffers) {
        for (DwarfMerchantOffer offer : currentOffers) {
            if (offer.needsRestock() && !isRestockRerollOffer(offer)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasAnyRestockRerollOffer(DwarfMerchantOffers currentOffers) {
        for (DwarfMerchantOffer offer : currentOffers) {
            if (isRestockRerollOffer(offer)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isGlobalOffer(
            @Nullable DwarfMerchantOffer offer
    ) {
        return offer != null
                && offer.getTradeGroup()
                == DwarfTradeRecipe.TradeGroup.GLOBAL_POOL;
    }

    private static boolean isGlobalRecipe(
            @NotNull RecipeHolder<DwarfTradeRecipe> holder
    ) {
        DwarfTradeRecipe.TradePoolEntry pool =
                holder.value().pool();

        return pool != null
                && pool.group()
                == DwarfTradeRecipe.TradeGroup.GLOBAL_POOL;
    }

    public boolean restock() {
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return false;
        }

        DwarfMerchantOffers currentOffers =
                this.getOffers();

        if (currentOffers.isEmpty()) {
            return false;
        }

        boolean canResetAny =
                hasAnyOfferNeedingUseReset(
                        currentOffers
                );

        boolean canRerollAnyPool =
                hasAnyRestockRerollOffer(
                        currentOffers
                );

        if (!canResetAny
                && !canRerollAnyPool) {
            return false;
        }

        boolean resetAnyOffer = false;

        for (DwarfMerchantOffer offer : currentOffers) {
            if (!offer.needsRestock()) {
                continue;
            }

            if (isRestockRerollOffer(offer)) {
                continue;
            }

            offer.resetUses();
            resetAnyOffer = true;
        }

        boolean rebuiltAnyPool = false;

        if (canRerollAnyPool) {
            List<DwarfMerchantOffer> preserved =
                    new ArrayList<>();

            int globalRerollCount = 0;

            for (DwarfMerchantOffer offer : currentOffers) {
                if (!isRestockRerollOffer(offer)) {
                    preserved.add(
                            offer.copy()
                    );

                    continue;
                }

                if (isGlobalOffer(offer)) {
                    globalRerollCount++;
                }
            }

            currentOffers.clear();
            currentOffers.addAll(
                    preserved
            );

            DwarfProfession profession =
                    getTradeProfession();

            DwarfMerchantData.Level level =
                    DwarfMerchantData.Level.fromId(
                            this.getMerchantLevel()
                    );

            var rerolledRecipes =
                    DwarfTrades.getTradeRecipesForMode(
                            serverLevel,
                            profession,
                            level,
                            this.random,
                            DwarfTrades.RefreshMode.RESTOCK
                    );

            /*
             * The regular selector uses the configured per-level
             * global increment. Skip its global results because a
             * restock must preserve the currently unlocked count.
             */
            for (var holder : rerolledRecipes) {
                if (isGlobalRecipe(holder)) {
                    continue;
                }

                addOfferFromRecipe(
                        currentOffers,
                        holder
                );
            }

            if (globalRerollCount > 0) {
                var globalRecipes =
                        DwarfTrades.getGlobalTradeRecipes(
                                serverLevel,
                                profession,
                                this.random,
                                globalRerollCount,
                                Set.of()
                        );

                for (var holder : globalRecipes) {
                    addOfferFromRecipe(
                            currentOffers,
                            holder
                    );
                }
            }

            rebuiltAnyPool = true;
        }

        if (!resetAnyOffer
                && !rebuiltAnyPool) {
            return false;
        }

        this.lastRestockGameTime =
                serverLevel.getGameTime();

        JolCraftSoundHelper.entity(
                this,
                Objects.requireNonNull(
                        getRestockSound()
                )
        );

        this.resendOffersToTradingPlayer();

        return true;
    }

    public void restockBountiesOnly() {
        if (this.level().isClientSide) {
            return;
        }

        if (this.getOffers().isEmpty()) {
            return;
        }

        boolean restocked = false;

        for (DwarfMerchantOffer offer : this.getOffers()) {
            if (offer.getResult().is(JolCraftItems.BOUNTY.get()) && offer.needsRestock()) {
                offer.resetUses();
                restocked = true;
            }
        }

        if (restocked) {
            JolCraftSoundHelper.entity(this, Objects.requireNonNull(getRestockSound()));
        }
    }

    public void rerollTrades() {
        if (this.level().isClientSide) {
            return;
        }

        int globalOfferCount = 0;

        if (this.offers != null) {
            for (DwarfMerchantOffer offer : this.offers) {
                if (isGlobalOffer(offer)) {
                    globalOfferCount++;
                }
            }
        }

        rebuildTrades(
                DwarfTrades.RefreshMode.REROLL
        );

        if (this.offers == null) {
            this.offers =
                    new DwarfMerchantOffers();
        }

        /*
         * REROLL generates only the configured per-level global
         * increment. Remove that temporary selection and reroll the
         * complete number of globals already unlocked.
         */
        this.offers.removeIf(
                AbstractTradingEntity::isGlobalOffer
        );

        if (
                globalOfferCount > 0
                        && this.level()
                        instanceof ServerLevel serverLevel
        ) {
            var globalRecipes =
                    DwarfTrades.getGlobalTradeRecipes(
                            serverLevel,
                            getTradeProfession(),
                            this.random,
                            globalOfferCount,
                            Set.of()
                    );

            for (var holder : globalRecipes) {
                addOfferFromRecipe(
                        this.offers,
                        holder
                );
            }
        }

        JolCraftSoundHelper.entity(
                this,
                Objects.requireNonNull(
                        getRerollSound()
                )
        );

        this.resendOffersToTradingPlayer();
    }

    protected void rewardTradeXp(DwarfMerchantOffer offer) {
        int xpReward = 3 + this.random.nextInt(4);

        this.dwarfXp += offer.getXp();
        this.lastTradedPlayer = this.getTradingPlayer();

        xpReward += triggerLevelUp(this);

        if (offer.shouldRewardExp()) {
            this.level().addFreshEntity(
                    new ExperienceOrb(this.level(), this.getX(), this.getY() + 0.5, this.getZ(), xpReward)
            );
        }
    }

    public static int triggerLevelUp(AbstractTradingEntity dwarf) {
        if (dwarf.shouldIncreaseLevel()) {
            dwarf.updateMerchantTimer = 40;
            dwarf.increaseProfessionLevelOnUpdate = true;
            return 5;
        }
        return 0;
    }

    public boolean shouldIncreaseLevel() {
        int level = this.getMerchantLevel();
        return DwarfMerchantData.canLevelUp(level) && this.dwarfXp >= DwarfMerchantData.getMaxXpPerLevel(level);
    }

    public void increaseMerchantCareer() {
        if (this.level().isClientSide) {
            return;
        }

        int current =
                this.getMerchantLevel();

        if (!DwarfMerchantData.canLevelUp(current)) {
            return;
        }

        int next =
                current + 1;

        this.setMerchantLevel(next);

        JolCraftLogs.info(
                JolCraftLogTags.ENTITY,
                "{} at {} leveled up to {}",
                DwarfProfession.getDisplayName(this)
                        .getString(),
                JolCraftLogs.roundedPos(this),
                DwarfMerchantData.Level.fromId(next)
        );

        updateTradesForLevelUp();
        resendOffersToTradingPlayer();
    }

    @Override
    public void openTradingScreen(Player player, Component displayName, int level) {
        this.setTradingPlayer(player);

        OptionalInt menuId = player.openMenu(new SimpleMenuProvider(
                (containerId, inventory, accessingPlayer) -> new DwarfMerchantMenu(containerId, inventory, this),
                displayName
        ));

        if (menuId.isEmpty()) {
            this.setTradingPlayer(null);
            return;
        }

        if (!player.level().isClientSide() && player instanceof ServerPlayer serverPlayer) {
            DwarfMerchantOffers offers = this.getOffers();
            if (!offers.isEmpty()) {
                JolCraftNetworking.sendToClient(
                        serverPlayer,
                        new ClientboundDwarfMerchantOffersPacket(
                                menuId.getAsInt(),
                                offers,
                                level,
                                this.getDwarfXp(),
                                this.showProgressBar(),
                                this.showLevel(),
                                this.canRestock()
                        )
                );
            }
        }
    }

    @Override
    public void notifyTrade(DwarfMerchantOffer offer) {
        if (this.level().isClientSide) {
            return;
        }

        Player player = this.getTradingPlayer();

        offer.increaseUses();
        this.ambientSoundTime = -this.getAmbientSoundInterval();
        this.rewardTradeXp(offer);

        if (player instanceof ServerPlayer serverPlayer) {
            player.awardStat(JolCraftStats.TRADE_WITH_DWARF.get());
            JolCraftCriteriaTriggers.TRADE_WITH_DWARF.trigger(serverPlayer, this.getTradeProfession());
        }

        SoundEvent sound = this.getNotifyTradeSound();
        if (sound != null) {
            long now = this.level().getGameTime();
            if (now - this.lastTradeSoundGameTime >= TRADE_SOUND_COOLDOWN_TICKS) {
                this.lastTradeSoundGameTime = now;
                JolCraftSoundHelper.entity(this, sound);
            }
        }
    }

    @Override
    public DwarfMerchantOffers getOffers() {
        if (this.level().isClientSide) {
            throw new IllegalStateException("Cannot load Dwarf offers on the client");
        }

        if (this.offers == null || this.offers.isEmpty()) {
            this.offers = new DwarfMerchantOffers();
            this.updateTrades();
        }

        return this.offers;
    }

    @Override
    public void notifyTradeUpdated(ItemStack stack) {
        if (!this.level().isClientSide && this.ambientSoundTime > -this.getAmbientSoundInterval() + 20) {
            this.ambientSoundTime = -this.getAmbientSoundInterval();
            this.makeSound(this.getTradeUpdatedSound(!stack.isEmpty()));
        }
    }

    @Override
    public void overrideOffers(DwarfMerchantOffers offers) {}

    @Override
    public void overrideXp(int xp) {
        this.dwarfXp = Math.max(0, xp);
    }
}