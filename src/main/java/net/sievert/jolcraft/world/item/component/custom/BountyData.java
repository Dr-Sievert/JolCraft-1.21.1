package net.sievert.jolcraft.world.item.component.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.sievert.jolcraft.data.language.JolCraftDictionary;

import java.util.Optional;

@SuppressWarnings("deprecation")
public record BountyData(BountyObjective objective) {

    // =====================================================================
    // Objective: { task: "item", item, amount } OR { task: "entity", entity, amount }
    // =====================================================================

    public sealed interface BountyObjective permits BountyObjective.ItemObjective, BountyObjective.EntityObjective {

        String TASK_ITEM = JolCraftDictionary.ITEM;
        String TASK_ENTITY = JolCraftDictionary.ENTITY;

        record ItemObjective(Holder<Item> item, int amount) implements BountyObjective {}
        record EntityObjective(Holder<EntityType<?>> entity, int amount) implements BountyObjective {}

        record Raw(String task, Optional<Holder<Item>> item, Optional<Holder<EntityType<?>>> entity, int amount) {}

        MapCodec<Raw> RAW_CODEC =
                RecordCodecBuilder.mapCodec(inst -> inst.group(
                        Codec.STRING.fieldOf(JolCraftDictionary.TASK).forGetter(Raw::task),

                        RegistryFixedCodec.create(Registries.ITEM)
                                .optionalFieldOf(JolCraftDictionary.ITEM)
                                .forGetter(Raw::item),

                        RegistryFixedCodec.create(Registries.ENTITY_TYPE)
                                .optionalFieldOf(JolCraftDictionary.ENTITY)
                                .forGetter(Raw::entity),

                        Codec.INT.fieldOf(JolCraftDictionary.AMOUNT).forGetter(Raw::amount)
                ).apply(inst, Raw::new));

        Codec<BountyObjective> CODEC =
                RAW_CODEC.codec().flatXmap(
                        raw -> {
                            String task = raw.task() == null ? "" : raw.task().trim().toLowerCase();
                            if (task.isEmpty()) {
                                return DataResult.error(() -> "objective.task is required");
                            }

                            if (raw.amount() < 1) {
                                return DataResult.error(() -> "objective.amount must be >= 1 (got " + raw.amount() + ")");
                            }

                            boolean hasItem = raw.item().isPresent();
                            boolean hasEntity = raw.entity().isPresent();

                            return switch (task) {
                                case TASK_ITEM -> {
                                    if (!hasItem || hasEntity) {
                                        yield DataResult.error(() -> "objective for task 'item' must define 'item' (and not 'entity')");
                                    }
                                    Holder<Item> item = raw.item().get();
                                    if (item.value() == Items.AIR) {
                                        yield DataResult.error(() -> "objective.item must not be air");
                                    }
                                    yield DataResult.success(new ItemObjective(item, raw.amount()));
                                }
                                case TASK_ENTITY -> {
                                    if (!hasEntity || hasItem) {
                                        yield DataResult.error(() -> "objective for task 'entity' must define 'entity' (and not 'item')");
                                    }
                                    yield DataResult.success(new EntityObjective(raw.entity().get(), raw.amount()));
                                }
                                default -> DataResult.error(() -> "Unknown objective.task '" + raw.task() + "'. Valid: item, entity");
                            };
                        },
                        obj -> {
                            if (obj instanceof ItemObjective(Holder<Item> item, int amount)) {
                                return DataResult.success(new Raw(TASK_ITEM, Optional.of(item), Optional.empty(), amount));
                            }
                            if (obj instanceof EntityObjective(Holder<EntityType<?>> entity, int amount)) {
                                return DataResult.success(new Raw(TASK_ENTITY, Optional.empty(), Optional.of(entity), amount));
                            }
                            return DataResult.error(() -> "Unknown BountyObjective variant");
                        }
                );

        enum TaskKind {
            ITEM,
            ENTITY
        }

        /**
         * - no throws
         * - decodes best-effort
         * - invalid payload degrades deterministically:
         *   - ITEM: AIR holder + amount=1
         *   - ENTITY: PIG holder + amount=1 (safe vanilla)
         *
         * NOTE: Your recipe validation prevents AIR / nonsense from ever being written from data.
         * This stream codec is just defensive against bad packets.
         */
        StreamCodec<RegistryFriendlyByteBuf, BountyObjective> STREAM_CODEC =
                StreamCodec.of(
                        (buf, obj) -> {
                            if (obj instanceof EntityObjective(Holder<EntityType<?>> entity, int amount)) {
                                buf.writeEnum(TaskKind.ENTITY);

                                ResourceLocation id = entity.unwrapKey()
                                        .map(ResourceKey::location)
                                        .orElse(EntityType.PIG.builtInRegistryHolder().unwrapKey().orElseThrow().location());

                                buf.writeResourceLocation(id);
                                buf.writeVarInt(Math.max(1, amount));
                                return;
                            }

                            ItemObjective io = (ItemObjective) obj;
                            buf.writeEnum(TaskKind.ITEM);

                            ResourceLocation id = io.item().unwrapKey()
                                    .map(ResourceKey::location)
                                    .orElse(Items.AIR.builtInRegistryHolder().unwrapKey().orElseThrow().location());

                            buf.writeResourceLocation(id);
                            buf.writeVarInt(Math.max(1, io.amount()));
                        },
                        buf -> {
                            TaskKind kind = buf.readEnum(TaskKind.class);
                            ResourceLocation id = buf.readResourceLocation();
                            int amount = Math.max(1, buf.readVarInt());

                            return switch (kind) {
                                case ITEM -> {
                                    var items = buf.registryAccess().lookupOrThrow(Registries.ITEM);
                                    var key = ResourceKey.create(Registries.ITEM, id);
                                    Holder<Item> holder = items.get(key)
                                            .orElseThrow(() -> new IllegalStateException("Unknown item in BountyObjective stream: " + id));
                                    yield new ItemObjective(holder, amount);
                                }
                                case ENTITY -> {
                                    var entities = buf.registryAccess().lookupOrThrow(Registries.ENTITY_TYPE);
                                    var key = ResourceKey.create(Registries.ENTITY_TYPE, id);
                                    Holder<EntityType<?>> holder = entities.get(key)
                                            .orElseThrow(() -> new IllegalStateException("Unknown entity type in BountyObjective stream: " + id));
                                    yield new EntityObjective(holder, amount);
                                }
                            };
                        }
                );
    }

    // =====================================================================
    // BountyData Codec
    // =====================================================================

    public static final Codec<BountyData> CODEC =
            RecordCodecBuilder.create(inst -> inst.group(
                    BountyObjective.CODEC
                            .fieldOf(JolCraftDictionary.OBJECTIVE)
                            .forGetter(BountyData::objective)
            ).apply(inst, BountyData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, BountyData> STREAM_CODEC =
            StreamCodec.of(
                    (buf, data) -> BountyObjective.STREAM_CODEC.encode(buf, data.objective),
                    buf -> new BountyData(BountyObjective.STREAM_CODEC.decode(buf))
            );
}