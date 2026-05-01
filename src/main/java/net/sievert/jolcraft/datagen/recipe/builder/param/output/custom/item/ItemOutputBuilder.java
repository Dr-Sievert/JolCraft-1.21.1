package net.sievert.jolcraft.datagen.recipe.builder.param.output.custom.item;

import net.minecraft.core.Holder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.saveddata.maps.MapDecorationType;
import net.sievert.jolcraft.data.id.param.JolCraftParameterIds;
import net.sievert.jolcraft.world.recipe.param.condition.Conditions;
import net.sievert.jolcraft.world.recipe.param.output.base.OutputParam;
import net.sievert.jolcraft.world.recipe.param.output.custom.item.ItemOutput;
import net.sievert.jolcraft.world.recipe.param.output.custom.item.ItemProducer;
import net.sievert.jolcraft.world.recipe.param.output.custom.item.ItemSpec;
import net.sievert.jolcraft.world.recipe.param.output.custom.item.transform.ItemTransforms;
import net.sievert.jolcraft.world.recipe.param.quantity.IntRange;
import net.sievert.jolcraft.datagen.recipe.builder.base.ParamBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.param.condition.ConditionsBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.param.output.custom.item.transform.ItemTransformsBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.param.output.hook.HookBuilder;
import net.sievert.jolcraft.world.recipe.param.output.hook.Hook;

import java.util.ArrayList;
import java.util.List;

public final class ItemOutputBuilder implements ParamBuilder<ItemOutput> {

    private ItemSpec result;
    private ItemTransforms transforms;
    private Conditions conditions;
    private final List<Hook> hooks = new ArrayList<>();

    private ItemOutputBuilder() {}

    public static ItemOutputBuilder create() {
        return new ItemOutputBuilder();
    }

    public ItemOutputBuilder result(ItemSpec result) {
        this.result = result;
        return this;
    }

    public ItemOutputBuilder result(ItemSpecBuilder builder) {
        this.result = builder != null ? builder.build() : null;
        return this;
    }

    public ItemOutputBuilder result(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            this.result = null;
            return this;
        }

        this.result = ItemSpec.of(stack).result().orElse(null);
        return this;
    }

    public ItemOutputBuilder result(Item item, int count) {
        if (item == null) {
            this.result = null;
            return this;
        }

        return result(ItemSpecBuilder.create()
                .producer(ItemProducer.item(item))
                .count(IntRange.fixed(count))
        );
    }

    public ItemOutputBuilder result(Item item) {
        if (item == null) {
            this.result = null;
            return this;
        }

        return result(ItemSpecBuilder.create()
                .producer(ItemProducer.item(item))
                .count(IntRange.ONE)
        );
    }

    public ItemOutputBuilder result(Item item, int min, int max) {
        if (item == null) {
            this.result = null;
            return this;
        }

        return result(ItemSpecBuilder.create()
                .producer(ItemProducer.item(item))
                .count(new IntRange(min, max))
        );
    }

    public ItemOutputBuilder result(Holder<Item> item, IntRange count) {
        if (item == null) {
            this.result = null;
            return this;
        }

        return result(ItemSpecBuilder.create()
                .producer(ItemProducer.item(item.value()))
                .count(count)
        );
    }

    public ItemOutputBuilder result(TagKey<Item> tag, IntRange count) {
        if (tag == null) {
            this.result = null;
            return this;
        }

        return result(ItemSpecBuilder.create()
                .producer(ItemProducer.tag(tag))
                .count(count)
        );
    }

    public ItemOutputBuilder resultMap(
            TagKey<Structure> structureTag,
            Holder<MapDecorationType> decoration,
            String displayNameKey,
            IntRange count
    ) {
        ItemProducer producer = ItemProducerBuilder.create()
                .map(structureTag, decoration, displayNameKey)
                .build();

        return result(ItemSpecBuilder.create()
                .producer(producer)
                .count(count)
        );
    }

    public ItemOutputBuilder transforms(ItemTransforms transforms) {
        this.transforms = transforms;
        return this;
    }

    public ItemOutputBuilder transforms(ItemTransformsBuilder builder) {
        this.transforms = builder != null ? builder.build() : null;
        return this;
    }

    public ItemOutputBuilder conditions(Conditions conditions) {
        this.conditions = conditions;
        return this;
    }

    public ItemOutputBuilder conditions(ConditionsBuilder builder) {
        this.conditions = builder != null ? builder.build() : null;
        return this;
    }

    public ItemOutputBuilder hook(Hook hook) {
        if (hook != null) {
            this.hooks.add(hook);
        }
        return this;
    }

    public ItemOutputBuilder hook(HookBuilder builder) {
        if (builder != null) {
            this.hooks.add(builder.build());
        }
        return this;
    }

    @Override
    public ItemOutput build() {
        if (result == null) {
            throw new IllegalStateException("Missing required field '" + JolCraftParameterIds.RESULT + "'");
        }

        ItemTransforms t = transforms != null ? transforms : ItemTransforms.EMPTY;
        return new ItemOutput(result, t);
    }

    public OutputParam buildParam() {
        OutputParam built = build();

        if (conditions != null && !conditions.isEmpty()) {
            built = built.withConditions(conditions);
        }
        if (!hooks.isEmpty()) {
            built = built.withHooks(List.copyOf(hooks));
        }

        return built;
    }
}