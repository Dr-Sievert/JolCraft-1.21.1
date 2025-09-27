package net.sievert.jolcraft.network.client.data;

import net.minecraft.resources.ResourceLocation;

import java.util.*;

public class ClientReputationData {

    private static final Map<Integer, Boolean> endorsementAnimationStates = new HashMap<>();
    private static int tier = 0;
    private static Set<ResourceLocation> CLIENT_ENDORSEMENTS = Collections.emptySet();

    public static void setTier(int newTier) {
        tier = newTier;
    }

    public static int getTier() {
        return tier;
    }

    public static void setEndorsementAnimation(int entityId, boolean running) {
        endorsementAnimationStates.put(entityId, running);
    }

    public static boolean isEndorsementAnimationActive(int entityId) {
        return endorsementAnimationStates.getOrDefault(entityId, false);
    }

    public static void setEndorsements(Set<ResourceLocation> endorsements) {
        CLIENT_ENDORSEMENTS = Collections.unmodifiableSet(new HashSet<>(endorsements));
    }

    public static boolean hasEndorsement(ResourceLocation profId) {
        return CLIENT_ENDORSEMENTS.contains(profId);
    }

    public static int endorsementCount() {
        return CLIENT_ENDORSEMENTS.size();
    }

    public static Set<ResourceLocation> getAllEndorsements() {
        return CLIENT_ENDORSEMENTS;
    }

    public static void clear() {
        endorsementAnimationStates.clear();
        CLIENT_ENDORSEMENTS = Collections.emptySet();
    }
}
