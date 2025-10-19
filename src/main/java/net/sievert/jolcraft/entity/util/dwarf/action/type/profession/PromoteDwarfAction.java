package net.sievert.jolcraft.entity.util.dwarf.action.type.profession;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.entity.JolCraftEntities;
import net.sievert.jolcraft.entity.custom.dwarf.base.AbstractBreedingEntity;
import net.sievert.jolcraft.entity.custom.dwarf.base.AbstractDwarfEntity;
import net.sievert.jolcraft.entity.util.dwarf.action.DwarfActionType;
import net.sievert.jolcraft.entity.util.dwarf.action.type.InspectDwarfAction;
import net.sievert.jolcraft.item.JolCraftItems;

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
        dwarf.level().playSound(null, dwarf.blockPosition(), SoundEvents.EVOKER_CAST_SPELL, SoundSource.NEUTRAL, 1.0F, 1.5F);
    }
    @Override
    public void tick() {
        if (ticksRemaining > 0) ticksRemaining--;

        if (ticksRemaining == 20) {
            smokeEffect(0.8F, 24, 0.7D, 1.0F, 1.5F);
        }

        if (ticksRemaining == 2) {
            smokeEffect(1.25F, 64, 2.5D, 1.5F, 1.0F);
        }
    }

    private void smokeEffect(float alpha, int count, double radius, float volume, float pitch) {
        dwarf.spawnColoredParticles(0.35F, 0.35F, 0.35F, alpha, count, radius);
        dwarf.level().playSound(null, dwarf.blockPosition(), SoundEvents.EVOKER_CAST_SPELL, SoundSource.NEUTRAL, volume, pitch);
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
            Map.entry(JolCraftItems.CONTRACT_BLACKSMITH.get(), JolCraftEntities.DWARF_BLACKSMITH.get()),
            Map.entry(JolCraftItems.CONTRACT_CHAMPION.get(), JolCraftEntities.DWARF_CHAMPION.get()),
            Map.entry(JolCraftItems.CONTRACT_SMELTER.get(), JolCraftEntities.DWARF_SMELTER.get())

            */
    );

    public void transformToProfession() {
        if (!dwarf.level().isClientSide) {
            ServerLevel serverLevel = (ServerLevel) dwarf.level();

            EntityType<? extends AbstractDwarfEntity> professionType = resolveProfessionType(itemstack);


            if (professionType != null) {
                Entity entity = professionType.create(
                        serverLevel,
                        null,
                        dwarf.blockPosition(),
                        EntitySpawnReason.CONVERSION,
                        false,
                        false
                );

                if (entity instanceof AbstractDwarfEntity newDwarf) {
                    newDwarf.moveTo(dwarf.getX(), dwarf.getY(), dwarf.getZ(), dwarf.getYRot(), dwarf.getXRot());
                    newDwarf.setData(AbstractBreedingEntity.BEARD_COLOR, dwarf.getData(AbstractBreedingEntity.BEARD_COLOR));
                    newDwarf.setData(AbstractBreedingEntity.EYE_COLOR, dwarf.getData(AbstractBreedingEntity.EYE_COLOR));
                    serverLevel.addFreshEntity(newDwarf);
                    dwarf.discard();
                }
            }
        }
    }
}
