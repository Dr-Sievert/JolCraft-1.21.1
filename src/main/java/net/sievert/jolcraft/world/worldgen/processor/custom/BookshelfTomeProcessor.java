package net.sievert.jolcraft.world.worldgen.processor.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.worldgen.processor.JolCraftProcessors;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@SuppressWarnings("deprecation")
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BookshelfTomeProcessor extends StructureProcessor {

    /**
     * Chance for each individual normal book to be replaced.
     * A shelf containing four normal books performs four independent rolls.
     */
    private static final float REPLACEMENT_CHANCE = 0.05F;

    /**
     * Relative item weights after a book passes its replacement roll:
     *
     * Unidentified:            75%
     * Ancient unidentified:   20%
     * Legendary unidentified:  4%
     * Ancient lexicon:          1%
     */
    private static final int UNIDENTIFIED_WEIGHT = 75;
    private static final int ANCIENT_UNIDENTIFIED_WEIGHT = 20;
    private static final int LEGENDARY_UNIDENTIFIED_WEIGHT = 4;
    private static final int ANCIENT_LEXICON_WEIGHT = 1;

    private static final int TOTAL_WEIGHT = UNIDENTIFIED_WEIGHT + ANCIENT_UNIDENTIFIED_WEIGHT + LEGENDARY_UNIDENTIFIED_WEIGHT + ANCIENT_LEXICON_WEIGHT;

    private static final String ITEMS_TAG = "Items";
    private static final String SLOT_TAG = "Slot";

    public static final MapCodec<BookshelfTomeProcessor> CODEC =
            MapCodec.unit(BookshelfTomeProcessor::new);

    @Nullable
    @Override
    public StructureTemplate.StructureBlockInfo processBlock(
            LevelReader level,
            BlockPos offset,
            BlockPos pos,
            StructureTemplate.StructureBlockInfo originalBlockInfo,
            StructureTemplate.StructureBlockInfo processedBlockInfo,
            StructurePlaceSettings settings
    ) {
        if (!processedBlockInfo.state().is(Blocks.CHISELED_BOOKSHELF)) {
            return processedBlockInfo;
        }

        CompoundTag originalNbt = processedBlockInfo.nbt();

        if (originalNbt == null || !originalNbt.contains(ITEMS_TAG, Tag.TAG_LIST)) {
            return processedBlockInfo;
        }

        CompoundTag updatedNbt = originalNbt.copy();
        ListTag storedItems = updatedNbt.getList(ITEMS_TAG, Tag.TAG_COMPOUND);

        if (storedItems.isEmpty()) {
            return processedBlockInfo;
        }

        HolderLookup.Provider registries = level.registryAccess();
        RandomSource random = settings.getRandom(processedBlockInfo.pos());
        boolean changed = false;

        for (int i = 0; i < storedItems.size(); i++) {
            CompoundTag storedItemTag = storedItems.getCompound(i);
            ItemStack storedStack = ItemStack.parseOptional(registries, storedItemTag);

            if (!storedStack.is(Items.BOOK)) {
                continue;
            }

            if (random.nextFloat() >= REPLACEMENT_CHANCE) {
                continue;
            }

            byte slot = storedItemTag.getByte(SLOT_TAG);
            ItemStack replacement = selectBookshelfItem(random).getDefaultInstance();

            Tag savedTag = replacement.save(registries, new CompoundTag());

            if (!(savedTag instanceof CompoundTag replacementTag)) {
                continue;
            }

            replacementTag.putByte(SLOT_TAG, slot);
            storedItems.set(i, replacementTag);
            changed = true;
        }

        if (!changed) {
            return processedBlockInfo;
        }

        return new StructureTemplate.StructureBlockInfo(
                processedBlockInfo.pos(),
                processedBlockInfo.state(),
                updatedNbt
        );
    }

    private static Item selectBookshelfItem(RandomSource random) {
        int roll = random.nextInt(TOTAL_WEIGHT);

        if (roll < UNIDENTIFIED_WEIGHT) {
            return JolCraftItems.UNIDENTIFIED_DWARVEN_TOME.get();
        }

        roll -= UNIDENTIFIED_WEIGHT;

        if (roll < ANCIENT_UNIDENTIFIED_WEIGHT) {
            return JolCraftItems.UNIDENTIFIED_ANCIENT_DWARVEN_TOME.get();
        }

        roll -= ANCIENT_UNIDENTIFIED_WEIGHT;

        if (roll < LEGENDARY_UNIDENTIFIED_WEIGHT) {
            return JolCraftItems.UNIDENTIFIED_LEGENDARY_ANCIENT_DWARVEN_TOME.get();
        }

        return JolCraftItems.ANCIENT_DWARVEN_LEXICON.get();
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return JolCraftProcessors.BOOKSHELF_TOME.type().get();
    }
}