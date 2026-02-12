package net.sievert.jolcraft.data.language;

import net.sievert.jolcraft.data.id.JolCraftIds;

public final class JolCraftDictionary extends JolCraftIds {

    private JolCraftDictionary() {}

    /* ---------------------------------------------------------------------
     * Core generic words
     * ------------------------------------------------------------------ */

    public static final String ID = "id";
    public static final String KEY = "key";
    public static final String DATA = "data";
    public static final String AND = "and";
    public static final String ON = "on";
    public static final String TYPE = "type";
    public static final String FILL = "fill";
    public static final String COMPLETE = "complete";
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
    public static final String SMALL = "small";
    public static final String MEDIUM = "medium";
    public static final String LARGE = "large";
    public static final String KNOWS = "knows";
    public static final String FORGE = "forge";
    public static final String INCREASE = "increase";
    public static final String RESISTANCE = "resistance";
    public static final String UNBREAKING = "unbreaking";
    public static final String DAY = "day";
    public static final String NIGHT = "night";
    public static final String ATTACK = "attack";
    public static final String DAMAGE = "damage";
    public static final String MOVEMENT = "movement";
    public static final String SPEED = "speed";
    public static final String SLOW = "slow";
    public static final String CHEST = "chest";
    public static final String MAGIC = "magic";
    public static final String CUT = "cut";
    public static final String HOMESTEAD = "homestead";
    public static final String MEMORY = "memory";
    public static final String LOCKPICKING = "lockpicking";
    public static final String HASTE = "haste";
    public static final String CURSED = "cursed";
    public static final String WOUND = "wound";
    public static final String DELIRIUM = "delirium";
    public static final String CURSE = "curse";
    public static final String CORROSION = "corrosion";
    public static final String RADIANT = "radiant";
    public static final String JEI = "jei";
    public static final String PLUGIN = "plugin";
    public static final String INFO = "info";
    public static final String PAGE = "page";
    public static final String SPAWN = "spawn";
    public static final String PARTICLE = "particle";
    public static final String PLAY = "play";
    public static final String SOUND = "sound";
    public static final String SELECT = "select";
    public static final String SYNC = "sync";
    public static final String HOP = "hop";
    public static final String SALVAGE = "salvage";
    public static final String EGG = "egg";
    public static final String CREATURE = "creature";
    public static final String MONSTER = "monster";
    public static final String INK = "ink";
    public static final String QUILL = "quill";
    public static final String DURABILITY = "durability";
    public static final String ENCHANTABLE = "enchantable";

    public static final String GROUP = "group";
    public static final String TAG = "tag";

    public static final String MIN = "min";
    public static final String MAX = "max";
    public static final String COUNT = "count";

    public static final String ADD = "add";
    public static final String WITH = "with";
    public static final String HAS = "has";
    public static final String USE = "use";

    public static final String VALID = "valid";
    public static final String STATE = "state";
    public static final String PERSISTENT = "persistent";
    public static final String SELECTION = "selection";

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

    public static final String RGB = "rgb";

    /* ---------------------------------------------------------------------
     * Loot / recipes / advancements
     * ------------------------------------------------------------------ */

    public static final String LOOT = "loot";
    public static final String SEED = "seed";
    public static final String TABLE = "table";

    public static final String RECIPE = "recipe";
    public static final String ADVANCEMENT = "advancement";

    public static final String CRAFTING = "crafting";
    public static final String INPUT = "input";
    public static final String OUTPUT = "output";
    public static final String TOOL = "tool";

    public static final String RESULT = "result";
    public static final String INGREDIENT = "ingredient";
    public static final String COMPONENT = "component";

    /* ---------------------------------------------------------------------
     * Map / structure / worldgen
     * ------------------------------------------------------------------ */

    public static final String MAP = "map";
    public static final String DISPLAY = "display";
    public static final String NAME = "name";
    public static final String DECORATION = "decoration";

    public static final String DESTINATION = "destination";
    public static final String STRUCTURE = "structure";
    public static final String DISCOVERED = "discovered";

    /* ---------------------------------------------------------------------
     * Trades / offers
     * ------------------------------------------------------------------ */

    public static final String TRADE = "trade";
    public static final String POOL = "pool";
    public static final String OFFER = "offer";
    public static final String PROVIDER = "provider";

    public static final String PRICE = "price";
    public static final String MULTIPLIER = "multiplier";
    public static final String VILLAGER = "villager";

    public static final String ENCHANTMENT = "enchantment";
    public static final String STACK = "stack";
    public static final String MODIFIER = "modifier";
    public static final String PATCH = "patch";

    public static final String COST = "cost";

    public static final String RESTOCK = "restock";
    public static final String REROLL = "reroll";

    /* ---------------------------------------------------------------------
     * Brewing / processing
     * ------------------------------------------------------------------ */

    public static final String BREW = "brew";
    public static final String BUBBLE = "bubble";
    public static final String TICK = "tick";

    public static final String EXACT = "exact";
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
    public static final String EFFECT = "effect";

    /* ---------------------------------------------------------------------
     * Dwarves / reputation / endorsements
     * ------------------------------------------------------------------ */

    public static final String DWARF = "dwarf";
    public static final String DWARVEN = "dwarven";

    public static final String PROFESSION = "profession";
    public static final String REPUTATION = "reputation";

    public static final String ENDORSEMENT = "endorsement";

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
    public static final String CHAMPION = "champion";
    public static final String BLACKSMITH = "blacksmith";
    public static final String SMELTER = "smelter";

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

    /* ---------------------------------------------------------------------
     * Materials
     * ------------------------------------------------------------------ */

    public static final String DEEPSLATE = "deepslate";
    public static final String GEODE = "geode";
    public static final String MITHRIL = "mithril";
    public static final String DARKER = "darker";

    /* ---------------------------------------------------------------------
     * Armor / equipment / trims
     * ------------------------------------------------------------------ */

    public static final String ARMOR = "armor";
    public static final String TOUGHNESS = "toughness";

    public static final String EQUIPMENT = "equipment";
    public static final String ASSET = "asset";

    public static final String HELMET = "helmet";
    public static final String CHESTPLATE = "chestplate";
    public static final String LEGGINGS = "leggings";
    public static final String BOOTS = "boots";

    public static final String TRIM = "trim";
    public static final String SMITHING = "smithing";
    public static final String MATERIAL = "material";
    public static final String TEMPLATE = "template";
    public static final String PATTERN = "pattern";

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
    public static final String IN = "in";
    public static final String CAUSE = "cause";
    public static final String FORCED = "forced";

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

    public static final String LAPIDARY = "lapidary";
    public static final String BENCH = "bench";
    public static final String FERMENTING = "fermenting";
    public static final String CAULDRON = "cauldron";

    public static final String STRONGBOX = "strongbox";
    public static final String HEARTH = "hearth";

    public static final String MANAGED = "managed";
    public static final String LIGHT = "light";
    public static final String DUMMY = "dummy";

    public static final String VERDANT = "verdant";
    public static final String SOIL = "soil";
    public static final String FARMLAND = "farmland";

    public static final String CROP = "crop";
    public static final String ORE = "ore";

    public static final String POTTED = "potted";
    public static final String DUSKCAP = "duskcap";
    public static final String FESTERLING = "festerling";

    public static final String BARLEY = "barley";
    public static final String MALT = "malt";

    public static final String MUFFHORN = "muffhorn";
    public static final String FUR = "fur";
    public static final String MILK = "milk";
    public static final String BUCKET = "bucket";

    public static final String BULB = "bulb";
    public static final String TOP = "top";
    public static final String BOTTOM = "bottom";

    public static final String ASGARNIAN = "asgarnian";
    public static final String DUSKHOLD = "duskhold";
    public static final String KRANDONIAN = "krandonian";
    public static final String YANILLIAN = "yanillian";

    public static final String COMPASS = "compass";
    public static final String DIAL = "dial";
    public static final String MORTAR = "mortar";

    // Tools / items / materials
    public static final String WARHAMMER = "warhammer";
    public static final String SPANNER = "spanner";
    public static final String HAMMER = "hammer";
    public static final String CHISEL = "chisel";
    public static final String PESTLE = "pestle";

    public static final String GEM = "gem";
    public static final String UNCUT = "uncut";
    public static final String DUST = "dust";

    public static final String CONTRACT = "contract";
    public static final String REPAIR = "repair";

    // Salvage / misc categories
    public static final String GLOBAL = "global";
    public static final String GENERAL = "general";
    public static final String TEXTILE = "textile";
    public static final String REDSTONE = "redstone";
    public static final String IRON = "iron";
    public static final String GOLD = "gold";

    // World / tags
    public static final String PLANTABLE = "plantable";

    // Biomes
    public static final String MOUNTAIN = "mountain";
    public static final String HILL = "hill";

    // Core item words
    public static final String DEV = "dev";
    public static final String COIN = "coin";
    public static final String POUCH = "pouch";
    public static final String LEXICON = "lexicon";
    public static final String LOCKPICK = "lockpick";
    public static final String EMPTY = "empty";

    // Materials / crafting parts
    public static final String IMPURE = "impure";
    public static final String PURE = "pure";
    public static final String INGOT = "ingot";
    public static final String NUGGET = "nugget";
    public static final String CHAINWEAVE = "chainweave";

    public static final String SWORD = "sword";
    public static final String PICKAXE = "pickaxe";
    public static final String SHOVEL = "shovel";
    public static final String AXE = "axe";
    public static final String HOE = "hoe";

    public static final String PLATE = "plate";
    public static final String ROD = "rod";


    // Bounty / contract / paper
    public static final String PARCHMENT = "parchment";
    public static final String BLANK = "blank";
    public static final String WRITTEN = "written";
    public static final String SIGNED = "signed";
    public static final String GUILD = "guild";
    public static final String SIGIL = "sigil";


    // Quill fill states
    public static final String HALF = "half";
    public static final String FULL = "full";

    // Brewing / food
    public static final String YEAST = "yeast";
    public static final String GLASS = "glass";
    public static final String MUG = "mug";

    // Tome rarities / identification
    public static final String UNIDENTIFIED = "unidentified";
    public static final String COMMON = "common";
    public static final String UNCOMMON = "uncommon";
    public static final String RARE = "rare";
    public static final String EPIC = "epic";
    public static final String LEGENDARY = "legendary";

    // Metals/tools
    public static final String COPPER = "copper";

    // Scrap / broken junk
    public static final String SCRAP = "scrap";
    public static final String HEAP = "heap";
    public static final String BROKEN = "broken";
    public static final String AMULET = "amulet";
    public static final String BELT = "belt";
    public static final String EXPIRED = "expired";
    public static final String POTION = "potion";
    public static final String MOULD = "mould";
    public static final String OLD = "old";
    public static final String FABRIC = "fabric";
    public static final String RUSTY = "rusty";
    public static final String TONG = "tong";
    public static final String GEAR = "gear";
    public static final String HEAD = "head";

    /* ---------------------------------------------------------------------
     * Gems
     * ------------------------------------------------------------------ */

    public static final String AEGISCORE = "aegiscore";
    public static final String ASHFANG = "ashfang";
    public static final String DEEPMARROW = "deepmarrow";
    public static final String EARTHBLOOD = "earthblood";
    public static final String EMBERGLASS = "emberglass";
    public static final String FROSTVEIN = "frostvein";
    public static final String GRIMSTONE = "grimstone";
    public static final String IRONHEART = "ironheart";
    public static final String LUMIERE = "lumiere";
    public static final String MOONSHARD = "moonshard";
    public static final String RUSTAGATE = "rustagate";
    public static final String SKYBURROW = "skyburrow";
    public static final String SUNGLEAM = "sungleam";
    public static final String VERDANITE = "verdanite";
    public static final String WOECRYSTAL = "woecrystal";

    // Structure / worldgen fields
    public static final String START = "start";
    public static final String JIGSAW = "jigsaw";
    public static final String SIZE = "size";
    public static final String HEIGHT = "height";
    public static final String PROJECT = "project";
    public static final String TO = "to";
    public static final String HEIGHTMAP = "heightmap";
    public static final String DISTANCE = "distance";
    public static final String FROM = "from";
    public static final String CENTER = "center";
    public static final String PADDING = "padding";
    public static final String LIQUID = "liquid";
    public static final String SETTING = "setting";

    // Worldgen / structures
    public static final String TRAIL = "trail";
    public static final String RUIN = "ruin";

}