package net.sievert.jolcraft.network.client.data;

import net.sievert.jolcraft.entity.util.dwarf.profession.DwarfProfession;

import java.util.*;

/**
 * Tracks client-side dwarven reputation and endorsement data.
 */
public class ClientReputationData {

    private static final Map<Integer, Boolean> endorsementAnimationStates = new HashMap<>();
    private static int tier = 0;
    private static Set<DwarfProfession> CLIENT_ENDORSEMENTS = Collections.emptySet();

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

    /**
     * Sets the current set of client-side profession endorsements.
     * The set should be unmodifiable after assignment.
     */
    public static void setEndorsements(Set<DwarfProfession> endorsements) {
        CLIENT_ENDORSEMENTS = Collections.unmodifiableSet(EnumSet.copyOf(endorsements));
    }

    /**
     * Checks if the client has endorsement for the given profession.
     */
    public static boolean hasEndorsement(DwarfProfession profession) {
        return CLIENT_ENDORSEMENTS.contains(profession);
    }

    /**
     * Returns the number of professions endorsed by the client.
     */
    public static int endorsementCount() {
        return CLIENT_ENDORSEMENTS.size();
    }

    /**
     * Returns an unmodifiable set of all client endorsements.
     */
    public static Set<DwarfProfession> getAllEndorsements() {
        return CLIENT_ENDORSEMENTS;
    }

    public static void clear() {
        endorsementAnimationStates.clear();
        CLIENT_ENDORSEMENTS = Collections.emptySet();
        tier = 0;
    }
}
