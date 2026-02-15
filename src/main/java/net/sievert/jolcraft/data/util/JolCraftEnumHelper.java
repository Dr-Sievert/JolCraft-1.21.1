package net.sievert.jolcraft.data.util;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public final class JolCraftEnumHelper {

    private JolCraftEnumHelper() {}

    /* ======================================================================
     * String id enums
     * ====================================================================== */

    /** Contract: enum has a stable String id. Keep ids as constants to avoid <clinit> coupling. */
    public interface StringId {
        String getId();
    }

    private static final Map<Class<?>, Map<String, ?>> STRING_ID_CACHES = new ConcurrentHashMap<>();

    public static <E extends Enum<E> & StringId> E byStringId(Class<E> type, String id, E fallback) {
        Objects.requireNonNull(type);
        Objects.requireNonNull(fallback);

        if (id == null) return fallback;

        String key = id.trim();
        if (key.isEmpty()) return fallback;

        @SuppressWarnings("unchecked")
        Map<String, E> map =
                (Map<String, E>) STRING_ID_CACHES.computeIfAbsent(type, __ -> buildStringIdMap(type));

        return map.getOrDefault(key, fallback);
    }

    private static <E extends Enum<E> & StringId> Map<String, E> buildStringIdMap(Class<E> type) {
        E[] values = type.getEnumConstants();
        Map<String, E> map = new HashMap<>(values.length);

        for (E e : values) {
            String raw = e.getId();
            if (raw == null) continue;

            String key = raw.trim();
            if (key.isEmpty()) continue;

            E prev = map.put(key, e);
            if (prev != null) {
                throw new IllegalStateException(
                        "Duplicate id '" + key + "' in enum " + type.getName()
                                + ": " + prev.name() + " vs " + e.name()
                );
            }
        }

        return Map.copyOf(map);
    }

    /* ======================================================================
     * Int id enums
     * ====================================================================== */

    /** Contract: enum has a stable int id (used for modulo lookups). */
    public interface IntId {
        int getId();
    }

    private static final Map<Class<?>, Object> INT_ID_MODULO_CACHES = new ConcurrentHashMap<>();

    private record IntIdModuloCache<E extends Enum<E> & IntId>(E[] byIndex) {}

    public static <E extends Enum<E> & IntId> E byIntIdModulo(Class<E> type, int id) {
        Objects.requireNonNull(type);

        @SuppressWarnings("unchecked")
        IntIdModuloCache<E> cache = (IntIdModuloCache<E>) INT_ID_MODULO_CACHES.computeIfAbsent(
                type, __ -> buildIntIdModuloCache(type)
        );

        E[] arr = cache.byIndex();
        return arr[Math.floorMod(id, arr.length)];
    }

    private static <E extends Enum<E> & IntId> IntIdModuloCache<E> buildIntIdModuloCache(Class<E> type) {
        E[] values = type.getEnumConstants();

        @SuppressWarnings("unchecked")
        E[] sorted = Arrays.stream(values)
                .sorted(Comparator.comparingInt(IntId::getId))
                .toArray(size -> (E[]) java.lang.reflect.Array.newInstance(type, size));

        return new IntIdModuloCache<>(sorted);
    }

    /* ======================================================================
     * Int id enums (exact lookup)
     * ====================================================================== */

    private static final Map<Class<?>, Map<Integer, ?>> INT_ID_EXACT_CACHES = new ConcurrentHashMap<>();

    public static <E extends Enum<E> & IntId> E byIntIdExact(Class<E> type, int id, E fallback) {
        Objects.requireNonNull(type);
        Objects.requireNonNull(fallback);

        @SuppressWarnings("unchecked")
        Map<Integer, E> map =
                (Map<Integer, E>) INT_ID_EXACT_CACHES.computeIfAbsent(type, __ -> buildIntIdExactMap(type));

        return map.getOrDefault(id, fallback);
    }

    private static <E extends Enum<E> & IntId> Map<Integer, E> buildIntIdExactMap(Class<E> type) {
        E[] values = type.getEnumConstants();
        Map<Integer, E> map = new HashMap<>(values.length);

        for (E e : values) {
            int key = e.getId();
            E prev = map.put(key, e);
            if (prev != null) {
                throw new IllegalStateException(
                        "Duplicate int id '" + key + "' in enum " + type.getName()
                                + ": " + prev.name() + " vs " + e.name()
                );
            }
        }

        return Map.copyOf(map);
    }
}