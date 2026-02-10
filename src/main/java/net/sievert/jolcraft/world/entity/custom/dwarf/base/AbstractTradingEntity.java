package net.sievert.jolcraft.world.entity.custom.dwarf.base;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.TeleportTransition;
import net.sievert.jolcraft.data.advancement.JolCraftCriteriaTriggers;
import net.sievert.jolcraft.config.dwarf.DwarfProfessionConfigs;
import net.sievert.jolcraft.config.dwarf.DwarfProfessionSettings;
import net.sievert.jolcraft.data.recipe.custom.DwarfTradeRecipe;
import net.sievert.jolcraft.network.JolCraftNetworking;
import net.sievert.jolcraft.network.packet.s2c.ClientboundDwarfMerchantOffersPacket;
import net.sievert.jolcraft.util.JolCraftLogTags;
import net.sievert.jolcraft.util.JolCraftLogs;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.profession.DwarfProfession;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.profession.DwarfProfessionTraits;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.trade.DwarfMerchant;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.trade.DwarfMerchantData;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.trade.DwarfMerchantOffer;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.trade.DwarfMerchantOffers;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.trade.DwarfTrades;
import net.sievert.jolcraft.world.entity.util.EntityData;
import net.sievert.jolcraft.world.gui.custom.menu.DwarfMerchantMenu;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.sound.JolCraftSounds;
import net.sievert.jolcraft.world.sound.util.JolCraftSoundHelper;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.Set;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class AbstractTradingEntity extends AbstractBreedingEntity implements DwarfMerchant, EntityData {

    // ------------------------------------------------------------
    // Trade sound debounce (server-side)
    // ------------------------------------------------------------

    private long lastTradeSoundGameTime = Long.MIN_VALUE;
    private static final long TRADE_SOUND_COOLDOWN_TICKS = 8L;

    // ------------------------------------------------------------
    // Restock
    // ------------------------------------------------------------

    public long lastRestockGameTime = 0L;
    public static final long RESTOCK_INTERVAL_TICKS = 6000L;

    // ------------------------------------------------------------
    // XP / Merchant state
    // ------------------------------------------------------------

    public int dwarfXp;
    public int updateMerchantTimer = 0;

    public boolean increaseProfessionLevelOnUpdate = false;

    @Nullable
    private Player tradingPlayer;

    @Nullable
    public Player lastTradedPlayer;

    @Nullable
    protected DwarfMerchantOffers offers;

    /**
     * Number of offers at the end of {@link #offers} that belong to the RESTOCK_POOL.
     * Offers themselves do not encode pool metadata, so we rely on the invariant:
     * RESTOCK_POOL offers are appended last in {@link #updateTrades()}.
     */
    protected int restockOfferCount = 0;

    /**
     * Persistent selection list for POOL trades.
     * Stored as recipe ids (ResourceLocation) because:
     * - NBT friendly
     * - stable across datapack reloads (as long as ids remain)
     * - avoids ResourceKey/RecipeKey mismatch problems
     */
    protected final List<ResourceLocation> persistentPoolSelections = new ArrayList<>();

    protected AbstractTradingEntity(EntityType<? extends AgeableMob> entityType, Level level) {
        super(entityType, level);
    }

    public static final EntityDataAccessor<Integer> DATA_MERCHANT_LEVEL =
            SynchedEntityData.defineId(AbstractTradingEntity.class, EntityDataSerializers.INT);

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_MERCHANT_LEVEL, DwarfMerchantData.MIN_MERCHANT_LEVEL);
    }

    // ------------------------------------------------------------
    // Merchant Level (JolCraft-owned)
    // ------------------------------------------------------------

    public int getMerchantLevel() {
        return getData(DATA_MERCHANT_LEVEL);
    }

    public void setMerchantLevel(int level) {
        setData(DATA_MERCHANT_LEVEL, level);
    }

    // ------------------------------------------------------------
    // Save / Load
    // ------------------------------------------------------------

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);

        DwarfMerchantData.CODEC
                .encodeStart(NbtOps.INSTANCE, new DwarfMerchantData(this.getMerchantLevel()))
                .resultOrPartial(msg -> JolCraftLogs.error(JolCraftLogTags.ENTITY, "{}", msg))
                .ifPresent(tag -> compound.put("MerchantData", tag));

        compound.putInt("Xp", this.dwarfXp);
        compound.putInt("RestockOfferCount", this.restockOfferCount);

        if (!this.persistentPoolSelections.isEmpty()) {
            ListTag list = new ListTag();
            for (ResourceLocation id : this.persistentPoolSelections) {
                list.add(StringTag.valueOf(id.toString()));
            }
            compound.put("PersistentPoolSelections", list);
        }

        if (!this.level().isClientSide) {
            DwarfMerchantOffers merchantoffers = this.getOffers();
            if (!merchantoffers.isEmpty()) {
                compound.put(
                        "Offers",
                        DwarfMerchantOffers.CODEC
                                .encodeStart(this.registryAccess().createSerializationContext(NbtOps.INSTANCE), merchantoffers)
                                .getOrThrow()
                );
            }
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);

        if (compound.contains("MerchantData", 10)) {
            DwarfMerchantData.CODEC
                    .parse(NbtOps.INSTANCE, compound.get("MerchantData"))
                    .resultOrPartial(msg -> JolCraftLogs.error(JolCraftLogTags.ENTITY, "{}", msg))
                    .ifPresent(data -> setMerchantLevel(data.level()));
        }

        if (compound.contains("Xp", 3)) {
            this.dwarfXp = compound.getInt("Xp");
        }

        if (compound.contains("RestockOfferCount", 3)) {
            this.restockOfferCount = Math.max(0, compound.getInt("RestockOfferCount"));
        } else {
            this.restockOfferCount = 0;
        }

        this.persistentPoolSelections.clear();
        if (compound.contains("PersistentPoolSelections", 9)) {
            ListTag list = compound.getList("PersistentPoolSelections", 8);
            for (int i = 0; i < list.size(); i++) {
                String s = list.getString(i);
                try {
                    this.persistentPoolSelections.add(ResourceLocation.parse(s));
                } catch (Exception ignored) {
                    // ignore invalid ids
                }
            }
        }

        if (compound.contains("Offers")) {
            DwarfMerchantOffers.CODEC
                    .parse(this.registryAccess().createSerializationContext(NbtOps.INSTANCE), compound.get("Offers"))
                    .resultOrPartial(Util.prefix(
                            "Failed to load offers: ",
                            msg -> JolCraftLogs.warn(JolCraftLogTags.ENTITY, "{}", msg)
                    ))
                    .ifPresent(loaded -> this.offers = loaded);
        }
    }

    // ------------------------------------------------------------
    // Trading plumbing
    // ------------------------------------------------------------

    public boolean canTrade() {
        return DwarfProfessionTraits.of(this.getTradeProfession()).canTrade().test((AbstractDwarfEntity) this);
    }

    @Nullable
    protected final DwarfProfessionSettings.TradeSettings getTradeSettingsOrNull() {
        DwarfProfession profession = getTradeProfession();
        return DwarfProfessionConfigs.getOrDefault(profession).tradesOrNull();
    }

    public boolean hasRandomTrades() {
        DwarfProfessionSettings.TradeSettings tradeSettings = getTradeSettingsOrNull();
        if (tradeSettings == null) return false;

        int level = this.getMerchantLevel();
        for (int lvl = 1; lvl <= level; lvl++) {
            if (tradeSettings.rollsFor(DwarfProfessionSettings.TradeSettings.PoolType.RESTOCK_POOL, lvl) > 0) {
                return true;
            }
        }
        return false;
    }

    public boolean canReroll() {
        return DwarfProfessionTraits.of(this.getTradeProfession()).canReroll();
    }

    /**
     * Hook for professions that want "crate restock" to behave differently.
     * Default: same as restock().
     */
    public void crateRestock() {
        restock();
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
    public int getVillagerXp() {
        return this.dwarfXp;
    }

    @Override
    public boolean showProgressBar() {
        return DwarfProfessionTraits.of(this.getTradeProfession()).showProgressBar();
    }

    public boolean showLevel() {
        return true;
    }

    @Override
    public void overrideOffers(@Nullable DwarfMerchantOffers offers) {
        // no-op (no legacy)
    }

    @Override
    public void overrideXp(int xp) {
        // no-op (no legacy)
    }

    protected void stopTrading() {
        this.setTradingPlayer(null);
    }

    @Override
    public boolean isClientSide() {
        return this.level().isClientSide;
    }

    @Nullable
    @Override
    public Entity teleport(TeleportTransition teleportTransition) {
        this.stopTrading();
        return super.teleport(teleportTransition);
    }

    @Override
    public void die(DamageSource cause) {
        if (!this.level().isClientSide) {
            JolCraftLogs.info(
                    JolCraftLogTags.ENTITY,
                    "Dwarf {} died: {}",
                    this,
                    this.getCombatTracker().getDeathMessage().getString()
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
        SoundEvent sound = DwarfProfessionTraits.of(this.getTradeProfession()).restockSound();
        return sound != null ? sound : SoundEvents.VILLAGER_WORK_FISHERMAN;
    }

    public SoundEvent getRerollSound() {
        SoundEvent sound = DwarfProfessionTraits.of(this.getTradeProfession()).rerollSound();
        return sound != null ? sound : SoundEvents.VILLAGER_WORK_FISHERMAN;
    }

    // ------------------------------------------------------------
    // Profession resolution for recipe trades
    // ------------------------------------------------------------

    protected final DwarfProfession getTradeProfession() {
        if (this instanceof AbstractDwarfEntity dwarf) {
            return dwarf.getProfession();
        }
        throw new IllegalStateException("Trading entity is not a dwarf: " + this);
    }

    // ------------------------------------------------------------
    // Validity
    // ------------------------------------------------------------

    @Override
    public boolean stillValid(Player player) {
        return this.getTradingPlayer() == player && this.isAlive() && player.canInteractWithEntity(this, 4.0);
    }

    // ------------------------------------------------------------
    // Offer building (RECIPE + CONFIG)
    // ------------------------------------------------------------

    /**
     * Authoritative trade rebuild.
     * Order:
     *  1) ALL MAIN trades (for all levels ≤ current):
     *      - grouped by level ascending
     *      - ordered trades first (order present), ascending by order
     *      - unordered trades last
     *      - stable tie-break by recipe getId
     *  2) POOL trades rolled per level (1..current)
     *      - permanent additions until full reroll
     *  3) RESTOCK_POOL trades (rolled; rerolled on restock)
     */
    public void updateTrades() {
        if (!(this.level() instanceof ServerLevel serverLevel)) return;

        DwarfMerchantOffers out = this.offers;
        if (out == null) {
            this.offers = out = new DwarfMerchantOffers();
        }
        out.clear();

        DwarfProfession profession = getTradeProfession();
        int currentLevel = this.getMerchantLevel();

        DwarfProfessionSettings settings = DwarfProfessionConfigs.getOrDefault(profession);
        DwarfProfessionSettings.TradeSettings tradeSettings = settings.tradesOrNull();

        // ---------------------------------------------------------------------
        // Collect remaining pooled recipes (≤ current level)
        // ---------------------------------------------------------------------

        List<RecipeHolder<DwarfTradeRecipe>> remainingPool =
                DwarfTrades.getTradeRecipesUpToLevel(
                        serverLevel,
                        profession,
                        DwarfTradeRecipe.TradePool.POOL,
                        currentLevel
                );

        // POOL recipe getId -> holder (datapacks can change)
        Map<ResourceLocation, RecipeHolder<DwarfTradeRecipe>> poolById = new HashMap<>();
        for (RecipeHolder<DwarfTradeRecipe> h : remainingPool) {
            poolById.put(h.id().location(), h);
        }

        List<ResourceLocation> newPersistentPoolSelections = new ArrayList<>();
        Set<ResourceLocation> usedPoolIds = new HashSet<>();

        // =====================================================================
        // 1) MAIN TRADES — ALL LEVELS FIRST
        // =====================================================================

        for (int lvl = 1; lvl <= currentLevel; lvl++) {
            List<RecipeHolder<DwarfTradeRecipe>> mainAtLevel = new ArrayList<>(
                    DwarfTrades.getTradeRecipesAtLevel(
                            serverLevel,
                            profession,
                            DwarfTradeRecipe.TradePool.MAIN,
                            lvl
                    )
            );

            mainAtLevel.sort((a, b) -> {
                DwarfTradeRecipe ra = a.value();
                DwarfTradeRecipe rb = b.value();

                boolean ao = ra.order().isPresent();
                boolean bo = rb.order().isPresent();

                if (ao != bo) {
                    return ao ? -1 : 1;
                }

                if (ao) {
                    int cmp = Integer.compare(
                            ra.order().getAsInt(),
                            rb.order().getAsInt()
                    );
                    if (cmp != 0) return cmp;
                }

                // stable tie-break
                return a.id().location().compareTo(b.id().location());
            });

            for (RecipeHolder<DwarfTradeRecipe> holder : mainAtLevel) {
                addOfferFromRecipe(out, holder.value());
            }
        }

        // =====================================================================
        // 2) POOL TRADES — AFTER ALL MAIN
        // =====================================================================

        for (int lvl = 1; lvl <= currentLevel; lvl++) {
            int rolls = tradeSettings != null
                    ? tradeSettings.rollsFor(
                    DwarfProfessionSettings.TradeSettings.PoolType.POOL,
                    lvl
            )
                    : 0;

            for (int r = 0; r < rolls; r++) {
                RecipeHolder<DwarfTradeRecipe> chosen =
                        takeNextPersistedOrRollPool(
                                lvl,
                                remainingPool,
                                poolById,
                                usedPoolIds,
                                this.persistentPoolSelections,
                                newPersistentPoolSelections
                        );

                if (chosen == null) break;
                addOfferFromRecipe(out, chosen.value());
            }
        }

        // Persist pool selections actually used
        this.persistentPoolSelections.clear();
        this.persistentPoolSelections.addAll(newPersistentPoolSelections);

        // =====================================================================
        // 3) RESTOCK_POOL — LAST (single canonical implementation)
        // =====================================================================

        this.restockOfferCount = appendRestockPoolOffers(serverLevel, out, profession, currentLevel, tradeSettings);
    }

    protected void addOfferFromRecipe(DwarfMerchantOffers out, DwarfTradeRecipe recipe) {
        DwarfMerchantOffer offer = new DwarfTrades.RecipeListing(recipe).getOffer(this, this.random);
        if (offer != null) {
            out.add(offer);
        }
    }

    private static int requireWeight(RecipeHolder<DwarfTradeRecipe> h) {
        return h.value().weight().orElseThrow(() ->
                new IllegalStateException("Missing weight for pooled trade: " + h.id().location()));
    }

    @Nullable
    private RecipeHolder<DwarfTradeRecipe> takeNextPersistedOrRollPool(
            int unlockLevel,
            List<RecipeHolder<DwarfTradeRecipe>> remainingPool,
            Map<ResourceLocation, RecipeHolder<DwarfTradeRecipe>> poolById,
            Set<ResourceLocation> usedPoolIds,
            List<ResourceLocation> oldSelections,
            List<ResourceLocation> newSelections
    ) {
        for (ResourceLocation id : oldSelections) {
            if (usedPoolIds.contains(id)) continue;

            RecipeHolder<DwarfTradeRecipe> holder = poolById.get(id);
            if (holder == null) {
                // recipe removed/renamed in datapack -> skip
                usedPoolIds.add(id);
                continue;
            }
            if (holder.value().merchantLevel() > unlockLevel) continue;

            usedPoolIds.add(id);
            newSelections.add(id);

            remainingPool.remove(holder);
            poolById.remove(id);
            return holder;
        }

        RecipeHolder<DwarfTradeRecipe> rolled =
                rollOneFromRemainingByUnlockLevel(remainingPool, unlockLevel, this.random);
        if (rolled == null) return null;

        ResourceLocation id = rolled.id().location();
        usedPoolIds.add(id);
        newSelections.add(id);
        poolById.remove(id);
        return rolled;
    }

    @Nullable
    private static RecipeHolder<DwarfTradeRecipe> rollOneFromRemainingByUnlockLevel(
            List<RecipeHolder<DwarfTradeRecipe>> remaining,
            int unlockLevel,
            RandomSource random
    ) {
        int totalWeight = 0;
        for (RecipeHolder<DwarfTradeRecipe> h : remaining) {
            if (h.value().merchantLevel() > unlockLevel) continue;
            totalWeight += requireWeight(h);
        }
        if (totalWeight <= 0) return null;

        int roll = random.nextInt(totalWeight);
        for (int i = 0; i < remaining.size(); i++) {
            RecipeHolder<DwarfTradeRecipe> h = remaining.get(i);
            if (h.value().merchantLevel() > unlockLevel) continue;

            roll -= requireWeight(h);
            if (roll < 0) {
                remaining.remove(i);
                return h;
            }
        }

        return null;
    }

    private static List<RecipeHolder<DwarfTradeRecipe>> rollFromRemainingByUnlockLevel(
            List<RecipeHolder<DwarfTradeRecipe>> remaining,
            int rolls,
            int unlockLevel,
            RandomSource random
    ) {
        int capped = Math.min(Math.max(rolls, 0), remaining.size());
        List<RecipeHolder<DwarfTradeRecipe>> out = new ArrayList<>(capped);

        boolean hasExactAtLevel = false;
        for (RecipeHolder<DwarfTradeRecipe> h : remaining) {
            DwarfTradeRecipe r = h.value();
            if (r.merchantLevel() == unlockLevel && r.exactLevel()) {
                hasExactAtLevel = true;
                break;
            }
        }

        for (int i = 0; i < capped; i++) {
            RecipeHolder<DwarfTradeRecipe> one = hasExactAtLevel
                    ? rollOneFromExactLevel(remaining, unlockLevel, random)
                    : rollOneFromRemainingByUnlockLevel(remaining, unlockLevel, random);

            if (one == null) break;
            out.add(one);
        }

        return out;
    }

    @Nullable
    private static RecipeHolder<DwarfTradeRecipe> rollOneFromExactLevel(
            List<RecipeHolder<DwarfTradeRecipe>> remaining,
            int exactLevel,
            RandomSource random
    ) {
        int totalWeight = 0;
        for (RecipeHolder<DwarfTradeRecipe> h : remaining) {
            DwarfTradeRecipe r = h.value();
            if (r.merchantLevel() != exactLevel) continue;
            if (!r.exactLevel()) continue;
            totalWeight += requireWeight(h);
        }
        if (totalWeight <= 0) return null;

        int roll = random.nextInt(totalWeight);
        for (int i = 0; i < remaining.size(); i++) {
            RecipeHolder<DwarfTradeRecipe> h = remaining.get(i);
            DwarfTradeRecipe r = h.value();
            if (r.merchantLevel() != exactLevel) continue;
            if (!r.exactLevel()) continue;

            roll -= requireWeight(h);
            if (roll < 0) {
                remaining.remove(i);
                return h;
            }
        }

        return null;
    }

    private int appendRestockPoolOffers(
            ServerLevel serverLevel,
            DwarfMerchantOffers out,
            DwarfProfession profession,
            int currentLevel,
            @Nullable DwarfProfessionSettings.TradeSettings tradeSettings
    ) {
        List<RecipeHolder<DwarfTradeRecipe>> remainingRestock = DwarfTrades.getTradeRecipesUpToLevel(serverLevel, profession, DwarfTradeRecipe.TradePool.RESTOCK_POOL, currentLevel);

        int added = 0;
        for (int lvl = 1; lvl <= currentLevel; lvl++) {
            int rolls = tradeSettings != null
                    ? tradeSettings.rollsFor(DwarfProfessionSettings.TradeSettings.PoolType.RESTOCK_POOL, lvl)
                    : 0;
            if (rolls <= 0) continue;

            List<RecipeHolder<DwarfTradeRecipe>> picked =
                    rollFromRemainingByUnlockLevel(remainingRestock, rolls, lvl, this.random);

            for (RecipeHolder<DwarfTradeRecipe> holder : picked) {
                addOfferFromRecipe(out, holder.value());
                added++;
            }
        }

        return added;
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
                            this.getVillagerXp(),
                            this.showProgressBar(),
                            this.showLevel(),
                            this.canRestock()
                    )
            );
        }
    }

    // ------------------------------------------------------------
    // Restock / Reroll
    // ------------------------------------------------------------

    public boolean shouldRestock() {
        return this.level() instanceof ServerLevel serverLevel &&
                serverLevel.getGameTime() >= lastRestockGameTime + RESTOCK_INTERVAL_TICKS;
    }

    /**
     * Restock rules:
     * - MAIN + POOL: restock uses only (no reroll)
     * - RESTOCK_POOL: reroll the restock suffix
     */
    public void restock() {
        if (!(this.level() instanceof ServerLevel serverLevel)) return;

        DwarfMerchantOffers offers = this.getOffers();
        if (offers.isEmpty()) return;

        boolean didAnything = false;

        int restockStart = Math.max(0, offers.size() - Math.max(0, this.restockOfferCount));

        // MAIN + POOL: restock uses only
        for (int i = 0; i < restockStart; i++) {
            DwarfMerchantOffer offer = offers.get(i);
            if (offer.needsRestock()) {
                offer.resetUses();
                didAnything = true;
            }
        }

        // RESTOCK_POOL: reroll suffix
        if (this.restockOfferCount > 0) {
            for (int i = 0; i < this.restockOfferCount && !offers.isEmpty(); i++) {
                offers.removeLast();
            }

            DwarfProfession profession = getTradeProfession();
            int currentLevel = this.getMerchantLevel();

            DwarfProfessionSettings settings = DwarfProfessionConfigs.getOrDefault(profession);
            DwarfProfessionSettings.TradeSettings tradeSettings = settings.tradesOrNull();

            this.restockOfferCount = appendRestockPoolOffers(serverLevel, offers, profession, currentLevel, tradeSettings);
            didAnything = true;
        }

        if (didAnything) {
            this.lastRestockGameTime = this.level().getGameTime();
            JolCraftSoundHelper.entity(this, Objects.requireNonNull(getRestockSound()));
        }
    }

    public void restockBountiesOnly() {
        if (this.level().isClientSide) return;
        if (this.getOffers().isEmpty()) return;

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

    /**
     * Full reroll:
     * - clear POOL memory
     * - rebuild everything from recipes + config
     */
    public void rerollTrades() {
        if (this.level().isClientSide) return;

        this.persistentPoolSelections.clear();
        this.getOffers().clear();
        this.updateTrades();

        JolCraftSoundHelper.entity(this, Objects.requireNonNull(getRerollSound()));
    }

    // ------------------------------------------------------------
    // XP / Level-up
    // ------------------------------------------------------------

    protected void rewardTradeXp(DwarfMerchantOffer offer) {
        int i = 3 + this.random.nextInt(4);
        this.dwarfXp = this.dwarfXp + offer.getXp();
        this.lastTradedPlayer = this.getTradingPlayer();

        if (this.shouldIncreaseLevel()) {
            this.updateMerchantTimer = 40;
            this.increaseProfessionLevelOnUpdate = true;
            i += 5;
        }

        if (offer.shouldRewardExp()) {
            this.level().addFreshEntity(new ExperienceOrb(this.level(), this.getX(), this.getY() + 0.5, this.getZ(), i));
        }
    }

    public boolean shouldIncreaseLevel() {
        int level = this.getMerchantLevel();
        return DwarfMerchantData.canLevelUp(level) && this.dwarfXp >= DwarfMerchantData.getMaxXpPerLevel(level);
    }

    public void increaseMerchantCareer() {
        if (this.level().isClientSide) return;

        int current = this.getMerchantLevel();
        if (DwarfMerchantData.canLevelUp(current)) {
            int next = current + 1;
            this.setMerchantLevel(next);

            JolCraftLogs.info(
                    JolCraftLogTags.ENTITY,
                    "Dwarf {} leveled up to {}",
                    this.getTradeProfession(),
                    DwarfMerchantData.Level.fromId(next)
            );

            // Rebuild for the new level (POOL selections persist)
            this.updateTrades();
            this.resendOffersToTradingPlayer();
        }
    }

    // ------------------------------------------------------------
    // UI
    // ------------------------------------------------------------

    @Override
    public void openTradingScreen(Player player, Component displayName, int level) {
        OptionalInt menuId = player.openMenu(new SimpleMenuProvider(
                (containerId, inventory, accessingPlayer) -> new DwarfMerchantMenu(containerId, inventory, this),
                displayName
        ));

        if (menuId.isPresent() && !player.level().isClientSide() && player instanceof ServerPlayer serverPlayer) {
            DwarfMerchantOffers offers = this.getOffers();
            if (!offers.isEmpty()) {
                JolCraftNetworking.sendToClient(
                        serverPlayer,
                        new ClientboundDwarfMerchantOffersPacket(
                                menuId.getAsInt(),
                                offers,
                                level,
                                this.getVillagerXp(),
                                this.showProgressBar(),
                                this.showLevel(),
                                this.canRestock()
                        )
                );
            }
        }
    }

    // ------------------------------------------------------------
    // Trade notifications
    // ------------------------------------------------------------

    @Override
    public void notifyTrade(DwarfMerchantOffer offer) {
        if (this.level().isClientSide) return;

        Player player = this.getTradingPlayer();

        offer.increaseUses();
        this.ambientSoundTime = -this.getAmbientSoundInterval();
        this.rewardTradeXp(offer);

        if (player instanceof ServerPlayer serverPlayer) {
            player.awardStat(Stats.TRADED_WITH_VILLAGER);
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

        if (this.offers == null) {
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
}