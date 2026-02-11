package net.sievert.jolcraft.data.key;

import net.sievert.jolcraft.data.id.JolCraftIds;

public final class JolCraftDictionary extends JolCraftIds {

    private JolCraftDictionary() {}

    /* ---------------------------------------------------------------------
     * Core generic words
     * ------------------------------------------------------------------ */

    public static final String ID = "id";
    public static final String KEY = "key";
    public static final String ASSET = "asset";
    public static final String TEXTURE = "texture";
    public static final String TYPE = "type";
    public static final String LEVEL = "level";
    public static final String TIER = "tier";
    public static final String ORDER = "order";
    public static final String WEIGHT = "weight";
    public static final String AMOUNT = "amount";
    public static final String COLOR = "color";
    public static final String DEFAULT = "default";
    public static final String MAIN = "main";
    public static final String NONE = "none";
    public static final String UNKNOWN = "unknown";

    /* ---------------------------------------------------------------------
     * Data / serialization fields
     * ------------------------------------------------------------------ */

    public static final String PLAYER = "player";

    public static final String POSITION = "position";
    public static final String DIMENSION = "dimension";

    public static final String DURATION = "duration";
    public static final String AMPLIFIER = "amplifier";

    public static final String SCORE = "score";
    public static final String XP = "xp";
    public static final String EXPERIENCE = "experience";

    public static final String MIN = "min";
    public static final String MAX = "max";
    public static final String COUNT = "count";
    public static final String MIN_COUNT = join(MIN, COUNT);
    public static final String MAX_COUNT = join(MAX, COUNT);

    public static final String HAS = "has";
    public static final String USE = "use";
    public static final String ADD = "add";
    public static final String WITH = "with";

    public static final String VALID = "valid";
    public static final String STATES = "states";
    public static final String VALID_STATES = join(VALID, STATES);

    public static final String RGB = "rgb";

    /* ---------------------------------------------------------------------
     * Loot / recipes / advancements
     * ------------------------------------------------------------------ */

    public static final String LOOT = "loot";
    public static final String SEED = "seed";
    public static final String TABLE = "table";
    public static final String LOOT_SEED = join(LOOT, SEED);
    public static final String LOOT_TABLE = join(LOOT, TABLE);

    public static final String RECIPE = "recipe";
    public static final String RECIPES = "recipes";
    public static final String ADVANCEMENT = "advancement";

    public static final String CRAFTING = "crafting";
    public static final String INPUT = "input";
    public static final String OUTPUT = "output";
    public static final String TOOL = "tool";

    public static final String RESULT = "result";
    public static final String TAG = "tag";
    public static final String RESULT_TAG = join(RESULT, TAG);

    public static final String INGREDIENT = "ingredient";
    public static final String COMPONENTS = "components";

    /* ---------------------------------------------------------------------
     * Map / structure / worldgen
     * ------------------------------------------------------------------ */

    public static final String MAP = "map";
    public static final String DISPLAY = "display";
    public static final String NAME = "name";
    public static final String MAP_DISPLAY_NAME = join(MAP, DISPLAY, NAME);

    public static final String DECORATION = "decoration";
    public static final String MAP_DECORATION_TYPE = join(MAP, DECORATION, TYPE);

    public static final String DESTINATION = "destination";
    public static final String STRUCTURE = "structure";
    public static final String DESTINATION_STRUCTURE_TAG = join(DESTINATION, STRUCTURE, TAG);

    /* ---------------------------------------------------------------------
     * Trades / offers
     * ------------------------------------------------------------------ */

    public static final String TRADE = "trade";
    public static final String POOL = "pool";
    public static final String OFFERS = "offers";
    public static final String PROVIDER = "provider";

    public static final String MAX_USES = join(MAX, "uses");
    public static final String VILLAGER_XP = join("villager", XP);
    public static final String PRICE_MULTIPLIER = join("price", "multiplier");

    public static final String ENCHANTMENT = "enchantment";
    public static final String ENCHANTMENT_PROVIDER = join(ENCHANTMENT, PROVIDER);

    public static final String STACK = "stack";
    public static final String MODIFIER = "modifier";
    public static final String STACK_MODIFIER = join(STACK, MODIFIER);

    public static final String PATCH = "patch";
    public static final String RESULT_PATCH = join(RESULT, PATCH);

    public static final String COST = "cost";
    public static final String A = "a";
    public static final String B = "b";
    public static final String COST_A = join(COST, A);
    public static final String COST_B = join(COST, B);

    /* ---------------------------------------------------------------------
     * Brewing / processing
     * ------------------------------------------------------------------ */

    public static final String BREW = "brew";
    public static final String BUBBLE = "bubble";
    public static final String TICKS = "ticks";
    public static final String BREW_TICKS = join(BREW, TICKS);
    public static final String BUBBLE_TICKS = join(BUBBLE, TICKS);

    public static final String EXACT = "exact";
    public static final String EXACT_LEVEL = join(EXACT, LEVEL);

    public static final String FINALIZE = "finalize";
    public static final String EXTRACT = "extract";

    /* ---------------------------------------------------------------------
     * Language / UI
     * ------------------------------------------------------------------ */

    public static final String LANGUAGE = "language";
    public static final String TAB = "tab";
    public static final String TOOLTIP = "tooltip";
    public static final String MENU = "menu";
    public static final String CONTAINER = "container";

    /* ---------------------------------------------------------------------
     * Dwarves / reputation / endorsements
     * ------------------------------------------------------------------ */

    public static final String DWARF = "dwarf";
    public static final String DWARVEN = "dwarven";

    public static final String PROFESSION = "profession";
    public static final String REPUTATION = "reputation";

    public static final String ENDORSEMENT = "endorsement";
    public static final String ENDORSEMENTS = "endorsements";

    public static final String KNOWS = "knows";

    // Professions
    public static final String ALCHEMIST = "alchemist";
    public static final String ARCANIST = "arcanist";
    public static final String ARTISAN = "artisan";
    public static final String BREWMASTER = "brewmaster";
    public static final String EXPLORER = "explorer";
    public static final String GUARD = "guard";
    public static final String GUILDMASTER = "guildmaster";
    public static final String HISTORIAN = "historian";
    public static final String KEEPER = "keeper";
    public static final String MERCHANT = "merchant";
    public static final String MINER = "miner";
    public static final String PRIEST = "priest";
    public static final String SCRAPPER = "scrapper";

    /* ---------------------------------------------------------------------
     * Locks / access
     * ------------------------------------------------------------------ */

    public static final String LOCK = "lock";
    public static final String LOCKED = "locked";
    public static final String UNLOCK = "unlock";
    public static final String UNLOCKED = "unlocked";

    /* ---------------------------------------------------------------------
     * Bounties / crates
     * ------------------------------------------------------------------ */

    public static final String BOUNTY = "bounty";
    public static final String CRATE = "crate";
    public static final String BOUNTY_CRATE = join(BOUNTY, CRATE);
    public static final String TARGET = "target";
    public static final String CHANCE = "chance";

    /* ---------------------------------------------------------------------
     * Content domains
     * ------------------------------------------------------------------ */

    public static final String ITEM = "item";
    public static final String BLOCK = "block";
    public static final String ENTITY = "entity";
    public static final String STAT = "stat";
    public static final String ATTRIBUTE = "attribute";
    public static final String ATTRIBUTES = "attributes";

    public static final String ITEM_GROUP = join(ITEM, "group");
    public static final String JEI = "jei";

    /* ---------------------------------------------------------------------
     * Armor / equipment / trims
     * ------------------------------------------------------------------ */

    public static final String ARMOR = "armor";
    public static final String ARMOR_TOUGHNESS = join(ARMOR, "toughness");
    public static final String EQUIPMENT = "equipment";
    public static final String EQUIPMENT_ASSET = join(EQUIPMENT, ASSET);

    public static final String HELMET = "helmet";
    public static final String CHESTPLATE = "chestplate";
    public static final String LEGGINGS = "leggings";
    public static final String BOOTS = "boots";

    public static final String TRIM = "trim";
    public static final String MATERIAL = "material";
    public static final String PATTERN = "pattern";
    public static final String TRIM_MATERIAL = join(TRIM, MATERIAL);
    public static final String TRIM_PATTERN = join(TRIM, PATTERN);

    /* ---------------------------------------------------------------------
     * Entity properties / variants
     * ------------------------------------------------------------------ */

    public static final String PROPERTIES = "properties";

    public static final String AGE = "age";
    public static final String BABY = "baby";

    public static final String VARIANT = "variant";
    public static final String BEARD = "beard";
    public static final String EYE = "eye";

    public static final String LOVE = "love";
    public static final String IN_LOVE = join("in", LOVE);
    public static final String CAUSE = "cause";
    public static final String LOVE_CAUSE = join(LOVE, CAUSE);

    public static final String FORCED = "forced";
    public static final String FORCED_AGE = join(FORCED, AGE);

    public static final String OWNER = "owner";

    /* ---------------------------------------------------------------------
     * Lore / tomes / ancient
     * ------------------------------------------------------------------ */

    public static final String TABLET = "tablet";
    public static final String LORE = "lore";
    public static final String TOME = "tome";
    public static final String ANCIENT = "ancient";

    /* ---------------------------------------------------------------------
     * Stations / blocks
     * ------------------------------------------------------------------ */

    public static final String LAPIDARY_BENCH = join("lapidary", "bench");
    public static final String FERMENTING_CAULDRON = join("fermenting", "cauldron");
    public static final String STRONGBOX = "strongbox";
    public static final String HEARTH = "hearth";

    public static final String DEEPSLATE = "deepslate";
    public static final String COMPASS = "compass";
    public static final String DEEPSLATE_COMPASS = join(DEEPSLATE, COMPASS);
    public static final String DIAL = "dial";
    public static final String DEEPSLATE_COMPASS_DIAL = join(DEEPSLATE_COMPASS, DIAL);

    /* ---------------------------------------------------------------------
     * Attributes / effects / combat / movement
     * ------------------------------------------------------------------ */

    public static final String INCREASE = "increase";
    public static final String RESISTANCE = "resistance";

    public static final String SLOW = "slow";
    public static final String MAGIC = "magic";

    public static final String ATTACK = "attack";
    public static final String DAMAGE = "damage";

    public static final String MOVEMENT = "movement";
    public static final String SPEED = "speed";

    public static final String DAY = "day";
    public static final String NIGHT = "night";

    public static final String UNBREAKING = "unbreaking";
    public static final String CHEST = "chest";
    public static final String CROP = "crop";
    public static final String EFFECT = "effect";

    /* ---------------------------------------------------------------------
     * Sizes
     * ------------------------------------------------------------------ */

    public static final String SMALL = "small";
    public static final String MEDIUM = "medium";
    public static final String LARGE = "large";

    /* ---------------------------------------------------------------------
     * Materials
     * ------------------------------------------------------------------ */

    public static final String GEODE = "geode";
    public static final String MITHRIL = "mithril";
    public static final String DARKER = "darker";
}