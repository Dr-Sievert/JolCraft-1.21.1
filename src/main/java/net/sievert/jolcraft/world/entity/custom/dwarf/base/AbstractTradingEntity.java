package net.sievert.jolcraft.world.entity.custom.dwarf.base;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.TeleportTransition;
import net.sievert.jolcraft.advancement.JolCraftCriteriaTriggers;
import net.sievert.jolcraft.world.entity.util.EntityData;
import net.sievert.jolcraft.world.entity.util.dwarf.trade.DwarfMerchant;
import net.sievert.jolcraft.world.entity.util.dwarf.trade.DwarfMerchantOffer;
import net.sievert.jolcraft.world.entity.util.dwarf.trade.DwarfMerchantOffers;
import net.sievert.jolcraft.world.entity.util.dwarf.trade.DwarfTrades;
import net.sievert.jolcraft.world.gui.custom.menu.DwarfMerchantMenu;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.network.JolCraftNetworking;
import net.sievert.jolcraft.network.packet.s2c.ClientboundDwarfMerchantOffersPacket;
import net.sievert.jolcraft.world.sound.JolCraftSounds;
import net.sievert.jolcraft.world.sound.util.JolCraftSoundHelper;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalInt;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class AbstractTradingEntity extends AbstractBreedingEntity implements DwarfMerchant, EntityData {

    public long lastRestockGameTime = 0L;
    public static final long RESTOCK_INTERVAL_TICKS = 6000L;
    public int dwarfXp;
    public int updateMerchantTimer = 0;
    public static final Logger LOGGER = LogUtils.getLogger();
    public boolean increaseProfessionLevelOnUpdate = false;
    @Nullable
    private Player tradingPlayer;
    @Nullable
    public Player lastTradedPlayer;
    @Nullable
    protected DwarfMerchantOffers offers;

    protected AbstractTradingEntity(EntityType<? extends AgeableMob> entityType, Level level) {
        super(entityType, level);
    }

    public static final EntityDataAccessor<VillagerData> DATA_VILLAGER_DATA =
            SynchedEntityData.defineId(AbstractTradingEntity.class, EntityDataSerializers.VILLAGER_DATA);

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_VILLAGER_DATA, new VillagerData(
                VillagerType.PLAINS,
                VillagerProfession.NONE,
                1));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        VillagerData.CODEC
                .encodeStart(NbtOps.INSTANCE, this.getVillagerData())
                .resultOrPartial(LOGGER::error)
                .ifPresent(p_35454_ -> compound.put("VillagerData", p_35454_));
        compound.putInt("Xp", this.dwarfXp);
        if (!this.level().isClientSide) {
            DwarfMerchantOffers merchantoffers = this.getOffers();
            if (!merchantoffers.isEmpty()) {
                compound.put(
                        "Offers", DwarfMerchantOffers.CODEC.encodeStart(this.registryAccess().createSerializationContext(NbtOps.INSTANCE), merchantoffers).getOrThrow()
                );
            }
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("VillagerData", 10)) {
            VillagerData.CODEC
                    .parse(NbtOps.INSTANCE, compound.get("VillagerData"))
                    .resultOrPartial(LOGGER::error)
                    .ifPresent(data -> setData(DATA_VILLAGER_DATA, data));
        }
        if (compound.contains("Xp", 3)) {
            this.dwarfXp = compound.getInt("Xp");
        }
        if (compound.contains("Offers")) {
            DwarfMerchantOffers.CODEC
                    .parse(this.registryAccess().createSerializationContext(NbtOps.INSTANCE), compound.get("Offers"))
                    .resultOrPartial(Util.prefix("Failed to load offers: ", LOGGER::warn))
                    .ifPresent(p_323775_ -> this.offers = p_323775_);
        }
    }

    public boolean canTrade() {
        return false;
    }

    public boolean hasRandomTrades(){ return false; }

    public boolean canReroll(){ return true; }

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
        return true;
    }

    public boolean showLevel() {
        return true;
    }

    @Override
    public void overrideOffers(@Nullable DwarfMerchantOffers offers) {
    }

    @Override
    public void overrideXp(int xp) {
    }

    protected void stopTrading() {
        this.setTradingPlayer(null);
    }

    public VillagerData getVillagerData() {
        return getData(DATA_VILLAGER_DATA);
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

    @Nullable
    protected SoundEvent getRestockSound() {
        return SoundEvents.VILLAGER_WORK_FISHERMAN;
    }

    @Nullable
    protected SoundEvent getRerollSound() {
        return SoundEvents.VILLAGER_WORK_FISHERMAN;
    }

    public Int2ObjectMap<DwarfTrades.ItemListing[]> instanceTrades;

    public static Int2ObjectMap<DwarfTrades.ItemListing[]> toIntMap(Map<Integer, DwarfTrades.ItemListing[]> pMap) {
        return new Int2ObjectOpenHashMap<>(pMap);
    }

    @Override
    public boolean stillValid(Player player) {
        return this.getTradingPlayer() == player && this.isAlive() && player.canInteractWithEntity(this, 4.0);
    }

    public void setVillagerData(VillagerData villagerData) {
        VillagerData villagerdata = this.getVillagerData();
        if (villagerdata.getProfession() != villagerData.getProfession()) {
            this.offers = null;
        }
        setData(DATA_VILLAGER_DATA, villagerData);
    }

    protected void updateTrades() {
        if (this.level().isClientSide) return;
        int level = this.getVillagerData().getLevel();
        if (instanceTrades != null) {
            DwarfTrades.ItemListing[] listings = instanceTrades.get(level);
            if (listings != null) {
                this.addOffersFromItemListings(this.getOffers(), listings, listings.length);
            }
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
                            this.getVillagerData().getLevel(),
                            this.getVillagerXp(),
                            this.showProgressBar(),
                            this.showLevel(),
                            this.canRestock()
                    )
            );
        }
    }

    public boolean shouldRestock() {
        return this.level() instanceof ServerLevel serverLevel &&
                serverLevel.getGameTime() >= lastRestockGameTime + RESTOCK_INTERVAL_TICKS;
    }

    public void restock() {
        if (this.getOffers().isEmpty()) return;

        boolean needsRestock = false;
        for (DwarfMerchantOffer offer : this.getOffers()) {
            if (offer.needsRestock()) {
                offer.resetUses();
                needsRestock = true;
            }
        }
        if (needsRestock) {
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

    public void rerollTrades() {
        this.getOffers().clear();
        int originalLevel = this.getVillagerData().getLevel();
        for (int i = 1; i <= originalLevel; i++) {
            this.setVillagerData(this.getVillagerData().setLevel(i));
            this.updateTrades();
        }
        this.setVillagerData(this.getVillagerData().setLevel(originalLevel));
        JolCraftSoundHelper.entity(this, Objects.requireNonNull(getRerollSound()));    }

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
        int i = this.getVillagerData().getLevel();
        return VillagerData.canLevelUp(i) && this.dwarfXp >= VillagerData.getMaxXpPerLevel(i);
    }

    public void increaseMerchantCareer() {
        if (this.level().isClientSide) return;
        int current = this.getVillagerData().getLevel();
        if (VillagerData.canLevelUp(current)) {
            int next = current + 1;
            this.setVillagerData(this.getVillagerData().setLevel(next));
            this.updateTrades();
            this.resendOffersToTradingPlayer();
        }
    }

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

    @Override
    public void notifyTrade(DwarfMerchantOffer offer) {
        if (this.level().isClientSide) return;
        Player player = this.getTradingPlayer();
        offer.increaseUses();
        this.ambientSoundTime = -this.getAmbientSoundInterval();
        this.rewardTradeXp(offer);
        if (player instanceof ServerPlayer serverPlayer) {
            player.awardStat(Stats.TRADED_WITH_VILLAGER);
            JolCraftCriteriaTriggers.TRADE_WITH_DWARF.trigger(serverPlayer, (AbstractDwarfEntity) this);
        }
    }

    @Override
    public DwarfMerchantOffers getOffers() {
        if (this.level().isClientSide) {
            throw new IllegalStateException("Cannot load Villager offers on the client");
        } else {
            if (this.offers == null) {
                this.offers = new DwarfMerchantOffers();
                this.updateTrades();
            }
            return this.offers;
        }
    }

    @Override
    public void notifyTradeUpdated(ItemStack stack) {
        if (!this.level().isClientSide && this.ambientSoundTime > -this.getAmbientSoundInterval() + 20) {
            this.ambientSoundTime = -this.getAmbientSoundInterval();
            this.makeSound(this.getTradeUpdatedSound(!stack.isEmpty()));
        }
    }

    protected void addOffersFromItemListings(DwarfMerchantOffers givenMerchantOffers, DwarfTrades.ItemListing[] newTrades, int maxNumbers) {
        if (this.level().isClientSide) return;
        ArrayList<DwarfTrades.ItemListing> arraylist = Lists.newArrayList(newTrades);
        int i = 0;

        while (i < maxNumbers && !arraylist.isEmpty()) {
            DwarfMerchantOffer merchantoffer = arraylist.remove(this.random.nextInt(arraylist.size())).getOffer(this, this.random);
            if (merchantoffer != null) {
                givenMerchantOffers.add(merchantoffer);
                i++;
            }
        }
    }
}
