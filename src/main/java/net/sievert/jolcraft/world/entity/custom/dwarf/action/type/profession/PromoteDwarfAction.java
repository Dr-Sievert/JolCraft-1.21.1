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
import net.sievert.jolcraft.world.entity.custom.dwarf.base.AbstractDwarfEntity;
import net.sievert.jolcraft.world.entity.custom.dwarf.action.DwarfActionType;
import net.sievert.jolcraft.world.entity.custom.dwarf.action.type.InspectDwarfAction;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.world.entity.custom.dwarf.loadout.DwarfLoadouts;
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
    public DwarfActionType.Subtype getSubtype() {return DwarfActionType.Subtype.PROMOTE;}

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
        if (ticksRemaining > 0) ticksRemaining--;

        if (ticksRemaining == 20) {
            smokeEffect();
        }

        if (ticksRemaining == 2) {
            transformEffect();
        }
    }

    private void smokeEffect() {
        dwarf.spawnColoredParticles(0.35F, 0.35F, 0.35F, (float) 0.8, 24, 0.7);
        JolCraftSoundHelper.entity(dwarf, SoundEvents.EVOKER_CAST_SPELL, 1.0F, 1.5F);
    }

    private void transformEffect() {
        dwarf.spawnColoredParticles(0.35F, 0.35F, 0.35F, (float) 1.25, 64, 2.5);
        JolCraftSoundHelper.entity(dwarf, SoundEvents.EVOKER_CAST_SPELL, 1.5F, 1.0F);
    }

    @Override
    public boolean isStopped() {
        return ticksRemaining <= 0;
    }

    @Override
    public void stop() {
        dwarf.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
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
            Map.entry(JolCraftItems.CONTRACT_PRIEST.get(), JolCraftEntities.DWARF_PRIEST.get())

            /*

            // Tier 5
            Map.entry(JolCraftItems.CONTRACT_BLACKSMITH.getEntityType(), JolCraftEntities.DWARF_BLACKSMITH.getEntityType()),
            Map.entry(JolCraftItems.CONTRACT_CHAMPION.getEntityType(), JolCraftEntities.DWARF_CHAMPION.getEntityType()),
            Map.entry(JolCraftItems.CONTRACT_SMELTER.getEntityType(), JolCraftEntities.DWARF_SMELTER.getEntityType())

            */
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

        dwarf.setProfession(profession);
        DwarfLoadouts.applySpawnLoadout(
                dwarf,
                level,
                level.getCurrentDifficultyAt(dwarf.blockPosition()),
                null
        );

        JolCraftLogs.info(
                JolCraftLogTags.ENTITY,
                "{} at {} in {} promoted by {} to {}",
                DwarfProfession.getDisplayName(dwarf).getString(),
                JolCraftLogs.roundedPos(dwarf),
                dwarf.level().dimension().location(),
                player.getDisplayName().getString(),
                profession
        );
    }
}
