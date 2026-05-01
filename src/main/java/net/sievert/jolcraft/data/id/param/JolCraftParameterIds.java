package net.sievert.jolcraft.data.id.param;

import net.sievert.jolcraft.data.id.JolCraftIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;

/**
 * Canonical JSON field names used by JolCraft's param/atomic recipe architecture.
 * Rule: JolCraftDictionary stays singular semantic words.
 * This class holds the actual schema keys (including plurals and composite keys)
 * so param codecs never rebuild strings and can’t drift.
 */
public final class JolCraftParameterIds extends JolCraftIds {

    private JolCraftParameterIds() {}

    public static final String CODEC = JolCraftDictionary.CODEC;
    public static final String STREAM_CODEC = join(JolCraftDictionary.STREAM, CODEC);

    // ---------------------------------------------------------------------
    // Quantity
    // ---------------------------------------------------------------------

    public static final String NONE = JolCraftDictionary.NONE;

    public static final String MIN = JolCraftDictionary.MIN;
    public static final String MAX = JolCraftDictionary.MAX;

    public static final String COUNT = JolCraftDictionary.COUNT;
    public static final String MIN_COUNT = join(MIN, COUNT);
    public static final String MAX_COUNT = join(MAX, COUNT);

    // ---------------------------------------------------------------------
    // Composition / grouping
    // ---------------------------------------------------------------------

    public static final String SELECTOR = JolCraftDictionary.SELECTOR;
    public static final String REQUIREMENTS = plural(JolCraftDictionary.REQUIREMENT);

    public static final String PRODUCER = JolCraftDictionary.PRODUCER;
    public static final String TRANSFORMS = plural(JolCraftDictionary.TRANSFORM);
    public static final String OUTPUT = JolCraftDictionary.OUTPUT;

    // ---------------------------------------------------------------------
    // Selector / identity atomics
    // ---------------------------------------------------------------------

    public static final String INPUT = JolCraftDictionary.INPUT;
    public static final String INGREDIENT = JolCraftDictionary.INGREDIENT;
    public static final String INGREDIENTS = plural(INGREDIENT);

    public static final String ITEM = JolCraftDictionary.ITEM;
    public static final String BLOCK = JolCraftDictionary.BLOCK;
    public static final String ENTITY = JolCraftDictionary.ENTITY;
    public static final String TAG = JolCraftDictionary.TAG;
    public static final String PLAYER = JolCraftDictionary.PLAYER;
    public static final String STRUCTURE = JolCraftDictionary.STRUCTURE;

    public static final String WEIGHT = JolCraftDictionary.WEIGHT;

    // ---------------------------------------------------------------------
    // Common param fields
    // ---------------------------------------------------------------------

    public static final String LEVEL = JolCraftDictionary.LEVEL;

    public static final String MIN_LEVEL = join(MIN, LEVEL);
    public static final String MAX_LEVEL = join(MAX, LEVEL);

    public static final String VALUE = JolCraftDictionary.VALUE;
    public static final String RESULT = JolCraftDictionary.RESULT;
    public static final String GROUP = JolCraftDictionary.GROUP;
    public static final String CATEGORY = JolCraftDictionary.CATEGORY;
    public static final String ENTRIES = JolCraftDictionary.ENTRIES;
    public static final String ENUM = JolCraftDictionary.ENUM;
    public static final String RESOURCE_LOCATION = join(JolCraftDictionary.RESOURCE, JolCraftDictionary.LOCATION);
    public static final String PROVIDER = JolCraftDictionary.PROVIDER;
    public static final String BASE = JolCraftDictionary.BASE;
    public static final String PARAMETER = JolCraftDictionary.PARAMETER;
    public static final String HOOKS = plural(JolCraftDictionary.HOOK);
    public static final String MODE = JolCraftDictionary.MODE;
    public static final String RANGE = JolCraftDictionary.RANGE;
    public static final String SOURCE = JolCraftDictionary.SOURCE;
    public static final String TARGET = JolCraftDictionary.TARGET;
    public static final String KEY = JolCraftDictionary.KEY;
    public static final String NAME = JolCraftDictionary.NAME;
    public static final String HOLDER = JolCraftDictionary.HOLDER;
    public static final String STATE = JolCraftDictionary.STATE;
    public static final String IDENTITY = JolCraftDictionary.IDENTITY;
    public static final String CONTEXT = JolCraftDictionary.CONTEXT;
    public static final String PATH = JolCraftDictionary.PATH;

    // ---------------------------------------------------------------------
    // Item requirements / transforms fields
    // ---------------------------------------------------------------------

    public static final String ENCHANTMENT = JolCraftDictionary.ENCHANTMENT;
    public static final String ENCHANTMENTS = plural(JolCraftDictionary.ENCHANTMENT);

    public static final String COMPONENT = JolCraftDictionary.COMPONENT;
    public static final String COMPONENTS = plural(COMPONENT);

    public static final String PREDICATES = plural(JolCraftDictionary.PREDICATE);
    public static final String KEEP = JolCraftDictionary.KEEP;
    public static final String REMOVE = JolCraftDictionary.REMOVE;

    // ---------------------------------------------------------------------
    // Entity requirements fields
    // ---------------------------------------------------------------------

    public static final String ATTRIBUTE = JolCraftDictionary.ATTRIBUTE;
    public static final String ATTRIBUTES = plural(ATTRIBUTE);
    public static final String OPERATOR = JolCraftDictionary.OPERATOR;

    public static final String EFFECT = JolCraftDictionary.EFFECT;
    public static final String AMPLIFIER = JolCraftDictionary.AMPLIFIER;
    public static final String MIN_AMPLIFIER = join(JolCraftDictionary.MIN, AMPLIFIER);

    public static final String BABY = JolCraftDictionary.BABY;

    public static final String EQUIPMENT = JolCraftDictionary.EQUIPMENT;
    public static final String SLOT = JolCraftDictionary.SLOT;
    public static final String SPAWN = JolCraftDictionary.SPAWN;

    // ---------------------------------------------------------------------
    // Conditions
    // ---------------------------------------------------------------------

    public static final String CONDITIONS = plural(JolCraftDictionary.CONDITION);
    public static final String CHANCE = JolCraftDictionary.CHANCE;
    public static final String INVERT = JolCraftDictionary.INVERT;

    public static final String RAIN = JolCraftDictionary.RAIN;
    public static final String THUNDER = JolCraftDictionary.THUNDER;
    public static final String BIOME = JolCraftDictionary.BIOME;

    // ---------------------------------------------------------------------
    // Output / pool
    // ---------------------------------------------------------------------

    public static final String POOL = JolCraftDictionary.POOL;
    public static final String POOLS = plural(POOL);
    public static final String ROLLS = plural(JolCraftDictionary.ROLL);

    // ---------------------------------------------------------------------
    // Misc common keys that tend to recur
    // ---------------------------------------------------------------------

    public static final String TYPE = JolCraftDictionary.TYPE;
    public static final String ID = JolCraftDictionary.ID;

    // ---------------------------------------------------------------------
    // Runtime
    // ---------------------------------------------------------------------

    public static final String VOLUME = JolCraftDictionary.VOLUME;
    public static final String PITCH = JolCraftDictionary.PITCH;

    public static final String PARTICLE = JolCraftDictionary.PARTICLE;
    public static final String POSITION = JolCraftDictionary.POSITION;
    public static final String SPEED = JolCraftDictionary.SPEED;

    public static final String DURATION = JolCraftDictionary.DURATION;

    public static final String TEXT = JolCraftDictionary.TEXT;
    public static final String STYLE = JolCraftDictionary.STYLE;
    public static final String OVERLAY = JolCraftDictionary.OVERLAY;
}