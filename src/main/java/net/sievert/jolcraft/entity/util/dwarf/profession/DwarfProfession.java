package net.sievert.jolcraft.entity.util.dwarf.profession;

public enum DwarfProfession {
    NONE("none"),
    ALCHEMIST("alchemist"),
    ARCANIST("arcanist"),
    ARTISAN("artisan"),
    //BLACKSMITH("blacksmith"),
    BREWMASTER("brewmaster"),
    //CHAMPION("champion"),
    EXPLORER("explorer"),
    GUARD("guard"),
    GUILDMASTER("guildmaster"),
    HISTORIAN("historian"),
    KEEPER("keeper"),
    MERCHANT("merchant"),
    MINER("miner"),
    PRIEST("priest"),
    SCRAPPER("scrapper");
    //SMELTER("smelter");

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