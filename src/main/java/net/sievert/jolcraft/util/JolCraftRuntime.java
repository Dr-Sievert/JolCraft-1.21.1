package net.sievert.jolcraft.util;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class JolCraftRuntime {

    private JolCraftRuntime() {}

    // -----------------------------------------------------------------
    // State cache (value tracking)
    // -----------------------------------------------------------------
    public static final class StateCache<T> {
        private final Map<UUID, T> values = new HashMap<>();

        private static UUID id(ServerPlayer player) {
            return player.getUUID();
        }

        public void set(ServerPlayer player, T newValue) {
            values.put(id(player), newValue);
        }

        public boolean hasChanged(ServerPlayer player, T newValue) {
            return !Objects.equals(values.get(id(player)), newValue);
        }

        public T get(ServerPlayer player) {
            return values.get(id(player));
        }

        public T getOrDefault(ServerPlayer player, T fallback) {
            return values.getOrDefault(id(player), fallback);
        }

        public void clear(ServerPlayer player) {
            values.remove(id(player));
        }
    }

    // -----------------------------------------------------------------
    // Guard (re-entry / recursion protection)
    // -----------------------------------------------------------------
    public static final class Guard {
        private final Set<UUID> active = ConcurrentHashMap.newKeySet();

        private static UUID id(LivingEntity entity) {
            return entity.getUUID();
        }

        public boolean enter(LivingEntity entity) {
            return active.add(id(entity));
        }

        public void exit(LivingEntity entity) {
            active.remove(id(entity));
        }

        public boolean isActive(LivingEntity entity) {
            return active.contains(id(entity));
        }
    }
}