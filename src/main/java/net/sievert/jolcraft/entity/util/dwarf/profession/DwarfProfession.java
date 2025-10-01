package net.sievert.jolcraft.entity.util.dwarf.profession;

public enum DwarfProfession {
    NONE("none"),
    ARTISAN("artisan"),
    BREWMASTER("brewmaster"),
    EXPLORER("explorer"),
    GUARD("guard"),
    HISTORIAN("historian"),
    KEEPER("keeper"),
    MERCHANT("merchant"),
    MINER("miner"),
    SCRAPPER("scrapper");

    private final String id;
    DwarfProfession(String id) { this.id = id; }
    public String getId() { return id; }

    public static DwarfProfession byId(String id) {
        for (DwarfProfession prof : values()) {
            if (prof.id.equals(id)) return prof;
        }
        return NONE;
    }
}