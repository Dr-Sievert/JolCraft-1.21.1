package net.sievert.jolcraft.world.entity.custom.dwarf.action.type.profession;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.util.log.JolCraftLogTags;
import net.sievert.jolcraft.util.log.JolCraftLogs;
import net.sievert.jolcraft.world.entity.JolCraftEntities;
import net.sievert.jolcraft.world.entity.custom.dwarf.action.DwarfActionType;
import net.sievert.jolcraft.world.entity.custom.dwarf.action.type.InspectDwarfAction;
import net.sievert.jolcraft.world.entity.custom.dwarf.base.AbstractDwarfEntity;
import net.sievert.jolcraft.world.entity.custom.dwarf.loadout.DwarfLoadouts;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.sound.util.JolCraftSoundHelper;

import javax.annotation.Nullable;
import java.util.Map;

public class PromoteDwarfAction extends InspectDwarfAction {

    public int ticksRemaining = 0;

    public PromoteDwarfAction(AbstractDwarfEntity dwarf, Player player, InteractionHand hand, ItemStack itemstack) {
        super(dwarf, player, hand, itemstack);
    }

    @Override
    public DwarfActionType.Subtype getSubtype() {
        return DwarfActionType.Subtype.PROMOTE;
    }

    @Override
    public void start() {
        this.ticksRemaining = 40;
        dwarf.resetPaid();
        startInspect(dwarf, player, hand, itemstack);
        dwarf.spawnColoredParticles(0.35F, 0.35F, 0.35F, 0.7F, 16, 0.5D);
        JolCraftSoundHelper.entity(dwarf, SoundEvents.EVOKER_CAST_SPELL, 1.0F, 1.5F);
    }

    @Override
    public void tick() {
        if (ticksRemaining > 0) {
            ticksRemaining--;
        }

        if (ticksRemaining == 20) {
            smokeEffect();
        }

        if (ticksRemaining == 2) {
            transformEffect();
        }
    }

    private void smokeEffect() {
        dwarf.spawnColoredParticles(0.35F, 0.35F, 0.35F, 0.8F, 24, 0.7D);
        JolCraftSoundHelper.entity(dwarf, SoundEvents.EVOKER_CAST_SPELL, 1.0F, 1.5F);
    }

    private void transformEffect() {
        dwarf.spawnColoredParticles(0.35F, 0.35F, 0.35F, 1.25F, 64, 2.5D);
        JolCraftSoundHelper.entity(dwarf, SoundEvents.EVOKER_CAST_SPELL, 1.5F, 1.0F);
    }

    @Override
    public boolean isStopped() {
        return ticksRemaining <= 0;
    }

    @Override
    public void stop() {
        dwarf.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);

        /*
         * Promotion replaces the entity. Clear the transactional inspect action
         * before copying the dwarf so the consumed contract is not persisted as
         * an interrupted action and refunded by the replacement entity.
         */
        dwarf.getActionHelper().stopAction(dwarf);

        transformToProfession();
        this.previousMainHandItem = ItemStack.EMPTY;
    }

    @Nullable
    public EntityType<? extends AbstractDwarfEntity> resolveProfessionType(ItemStack contractStack) {
        return CONTRACT_TO_PROFESSION.get(contractStack.getItem());
    }

    public static final Map<Item, EntityType<? extends AbstractDwarfEntity>> CONTRACT_TO_PROFESSION = Map.ofEntries(

            Map.entry(JolCraftItems.CONTRACT_GUILDMASTER.get(), JolCraftEntities.DWARF_GUILDMASTER.get()),

            // Tier 1
            Map.entry(JolCraftItems.CONTRACT_MERCHANT.get(), JolCraftEntities.DWARF_MERCHANT.get()),
            Map.entry(JolCraftItems.CONTRACT_HISTORIAN.get(), JolCraftEntities.DWARF_HISTORIAN.get()),
            Map.entry(JolCraftItems.CONTRACT_SCRAPPER.get(), JolCraftEntities.DWARF_SCRAPPER.get()),

            // Tier 2
            Map.entry(JolCraftItems.CONTRACT_GUARD.get(), JolCraftEntities.DWARF_GUARD.get()),
            Map.entry(JolCraftItems.CONTRACT_BREWMASTER.get(), JolCraftEntities.DWARF_BREWMASTER.get()),
            Map.entry(JolCraftItems.CONTRACT_KEEPER.get(), JolCraftEntities.DWARF_KEEPER.get()),

            // Tier 3
            Map.entry(JolCraftItems.CONTRACT_ARTISAN.get(), JolCraftEntities.DWARF_ARTISAN.get()),
            Map.entry(JolCraftItems.CONTRACT_EXPLORER.get(), JolCraftEntities.DWARF_EXPLORER.get()),
            Map.entry(JolCraftItems.CONTRACT_MINER.get(), JolCraftEntities.DWARF_MINER.get()),

            // Tier 4
            Map.entry(JolCraftItems.CONTRACT_ALCHEMIST.get(), JolCraftEntities.DWARF_ALCHEMIST.get()),
            Map.entry(JolCraftItems.CONTRACT_ARCANIST.get(), JolCraftEntities.DWARF_ARCANIST.get()),
            Map.entry(JolCraftItems.CONTRACT_PRIEST.get(), JolCraftEntities.DWARF_PRIEST.get()),

            // Tier 5
            Map.entry(JolCraftItems.CONTRACT_BLACKSMITH.get(), JolCraftEntities.DWARF_BLACKSMITH.get()),
            Map.entry(JolCraftItems.CONTRACT_CHAMPION.get(), JolCraftEntities.DWARF_CHAMPION.get()),
            Map.entry(JolCraftItems.CONTRACT_SMELTER.get(), JolCraftEntities.DWARF_SMELTER.get())
    );

    public void transformToProfession() {
        if (!(dwarf.level() instanceof ServerLevel level)) {
            return;
        }

        EntityType<? extends AbstractDwarfEntity> professionType =
                resolveProfessionType(itemstack);

        if (professionType == null) {
            return;
        }

        DwarfProfession profession =
                DwarfProfession.fromEntityType(professionType);

        AbstractDwarfEntity promotedDwarf =
                professionType.create(level);

        if (promotedDwarf == null) {
            JolCraftLogs.error(
                    JolCraftLogTags.ENTITY,
                    "Failed to create promoted dwarf entity for profession {}",
                    profession
            );
            return;
        }

        /*
         * Copy the complete dwarf state before replacing the entity. This keeps
         * its UUID, name, appearance, age, health, equipment, attachments and
         * other persisted data while allowing the registered entity type to
         * change for rendering and integrations such as Jade.
         */
        promotedDwarf.restoreFrom(dwarf);
        promotedDwarf.setProfession(profession);

        DwarfLoadouts.applyLoadout(
                promotedDwarf
        );

        dwarf.discard();

        if (!level.addWithUUID(promotedDwarf)) {
            JolCraftLogs.error(
                    JolCraftLogTags.ENTITY,
                    "Failed to add promoted dwarf entity for profession {} at {} in {}",
                    profession,
                    JolCraftLogs.roundedPos(promotedDwarf),
                    level.dimension().location()
            );
            return;
        }

        JolCraftLogs.info(
                JolCraftLogTags.ENTITY,
                "{} at {} in {} promoted by {} to {}",
                DwarfProfession.getDisplayName(promotedDwarf).getString(),
                JolCraftLogs.roundedPos(promotedDwarf),
                promotedDwarf.level().dimension().location(),
                player.getDisplayName().getString(),
                profession
        );
    }
}