package net.sievert.jolcraft.param.custom.item;

import net.minecraft.world.item.Item;
import net.sievert.jolcraft.data.id.recipe.JolCraftParameterIds;
import net.sievert.jolcraft.param.base.identity.IdentitySelector;
import net.sievert.jolcraft.param.base.state.StateCarrier;
import net.sievert.jolcraft.param.base.state.StateEntry;
import net.sievert.jolcraft.param.base.state.quantity.IntParam;
import net.sievert.jolcraft.param.custom.item.component.DataComponentParam;

import java.util.*;

/**
 * Item param domain definitions.
 */
public final class ItemParam {

    private ItemParam() {}

    /**
     * Param describing an item by identity or tag.
     */
    public record Identity(
            IdentitySelector<Item> item
    ) {
        public Identity {
            Objects.requireNonNull(item, JolCraftParameterIds.ITEM);
        }
    }

    /**
     * Param describing an item stack.
     */
    public record Stack(
            Identity item,
            IntParam count,
            State state
    ) {
        public Stack {
            Objects.requireNonNull(item, JolCraftParameterIds.ITEM);
            count = count != null ? count : new IntParam(1);
            state = state != null ? state : new State(List.of());
        }

        public int amount() {
            return count.value();
        }

        /**
         * Param describing carried stack state.
         */
        public record State(
                List<DataComponentParam<?>> components
        ) implements StateCarrier {

            public State {
                components = components != null ? List.copyOf(components) : List.of();

                Set<Object> seen = new HashSet<>();

                for (DataComponentParam<?> component : components) {
                    Object identity = component.identity();

                    if (!seen.add(identity)) {
                        throw new IllegalArgumentException(
                                "Duplicate data component identity: " + identity
                        );
                    }
                }
            }

            @Override
            public List<? extends StateEntry<?, ?>> states() {
                List<StateEntry<?, ?>> states = new ArrayList<>();

                for (DataComponentParam<?> component : components) {
                    states.addAll(component.states());
                }

                return List.copyOf(states);
            }
        }
    }
}