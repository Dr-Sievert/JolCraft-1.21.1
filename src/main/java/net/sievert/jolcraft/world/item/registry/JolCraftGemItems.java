package net.sievert.jolcraft.world.item.registry;

import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.neoforged.neoforge.registries.DeferredItem;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.item.JolCraftItemIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.world.item.custom.gem.CutGemItem;
import net.sievert.jolcraft.world.item.custom.gem.UncutGemItem;
import net.sievert.jolcraft.world.item.material.trim.JolCraftTrimAttributes;
import net.sievert.jolcraft.world.item.material.trim.JolCraftTrimMaterials;
import net.sievert.jolcraft.world.item.registry.util.JolCraftItemRegistryHelper;

public final class JolCraftGemItems {

    private JolCraftGemItems() {}

    public static final GemSet AEGISCORE = registerGem(
            JolCraftItemIds.AEGISCORE,
            JolCraftItemIds.AEGISCORE_CUT,
            JolCraftItemIds.AEGISCORE_DUST,
            JolCraftTrimMaterials.Attribute.AEGISCORE
    );

    public static final GemSet ASHFANG = registerGem(
            JolCraftItemIds.ASHFANG,
            JolCraftItemIds.ASHFANG_CUT,
            JolCraftItemIds.ASHFANG_DUST,
            JolCraftTrimMaterials.Attribute.ASHFANG
    );

    public static final GemSet DEEPMARROW = registerGem(
            JolCraftItemIds.DEEPMARROW,
            JolCraftItemIds.DEEPMARROW_CUT,
            JolCraftItemIds.DEEPMARROW_DUST,
            JolCraftTrimMaterials.Attribute.DEEPMARROW
    );

    public static final GemSet EARTHBLOOD = registerGem(
            JolCraftItemIds.EARTHBLOOD,
            JolCraftItemIds.EARTHBLOOD_CUT,
            JolCraftItemIds.EARTHBLOOD_DUST,
            JolCraftTrimMaterials.Attribute.EARTHBLOOD
    );

    public static final GemSet EMBERGLASS = registerGem(
            JolCraftItemIds.EMBERGLASS,
            JolCraftItemIds.EMBERGLASS_CUT,
            JolCraftItemIds.EMBERGLASS_DUST,
            JolCraftTrimMaterials.Attribute.EMBERGLASS
    );

    public static final GemSet FROSTVEIN = registerGem(
            JolCraftItemIds.FROSTVEIN,
            JolCraftItemIds.FROSTVEIN_CUT,
            JolCraftItemIds.FROSTVEIN_DUST,
            JolCraftTrimMaterials.Attribute.FROSTVEIN
    );

    public static final GemSet GRIMSTONE = registerGem(
            JolCraftItemIds.GRIMSTONE,
            JolCraftItemIds.GRIMSTONE_CUT,
            JolCraftItemIds.GRIMSTONE_DUST,
            JolCraftTrimMaterials.Attribute.GRIMSTONE
    );

    public static final GemSet IRONHEART = registerGem(
            JolCraftItemIds.IRONHEART,
            JolCraftItemIds.IRONHEART_CUT,
            JolCraftItemIds.IRONHEART_DUST,
            JolCraftTrimMaterials.Attribute.IRONHEART
    );

    public static final GemSet LUMIERE = registerGem(
            JolCraftItemIds.LUMIERE,
            JolCraftItemIds.LUMIERE_CUT,
            JolCraftItemIds.LUMIERE_DUST,
            JolCraftTrimMaterials.Attribute.LUMIERE
    );

    public static final GemSet MOONSHARD = registerGem(
            JolCraftItemIds.MOONSHARD,
            JolCraftItemIds.MOONSHARD_CUT,
            JolCraftItemIds.MOONSHARD_DUST,
            JolCraftTrimMaterials.Attribute.MOONSHARD
    );

    public static final GemSet RUSTAGATE = registerGem(
            JolCraftItemIds.RUSTAGATE,
            JolCraftItemIds.RUSTAGATE_CUT,
            JolCraftItemIds.RUSTAGATE_DUST,
            JolCraftTrimMaterials.Attribute.RUSTAGATE
    );

    public static final GemSet SKYBURROW = registerGem(
            JolCraftItemIds.SKYBURROW,
            JolCraftItemIds.SKYBURROW_CUT,
            JolCraftItemIds.SKYBURROW_DUST,
            JolCraftTrimMaterials.Attribute.SKYBURROW
    );

    public static final GemSet SUNGLEAM = registerGem(
            JolCraftItemIds.SUNGLEAM,
            JolCraftItemIds.SUNGLEAM_CUT,
            JolCraftItemIds.SUNGLEAM_DUST,
            JolCraftTrimMaterials.Attribute.SUNGLEAM
    );

    public static final GemSet VERDANITE = registerGem(
            JolCraftItemIds.VERDANITE,
            JolCraftItemIds.VERDANITE_CUT,
            JolCraftItemIds.VERDANITE_DUST,
            JolCraftTrimMaterials.Attribute.VERDANITE
    );

    public static final GemSet WOECRYSTAL = registerGem(
            JolCraftItemIds.WOECRYSTAL,
            JolCraftItemIds.WOECRYSTAL_CUT,
            JolCraftItemIds.WOECRYSTAL_DUST,
            JolCraftTrimMaterials.Attribute.WOECRYSTAL
    );

    private static GemSet registerGem(
            String uncutId,
            String cutId,
            String dustId,
            JolCraftTrimMaterials.Attribute trimMaterialAttribute
    ) {
        JolCraftTrimAttributes.TrimAttribute trimAttribute = JolCraftTrimAttributes.getTrimAttribute(trimMaterialAttribute);

        ItemAttributeModifiers modifiers = buildGemAttribute(uncutId, trimAttribute);

        return new GemSet(
                JolCraftItemRegistryHelper.registerItem(
                        uncutId,
                        props -> new UncutGemItem(
                                props.component(DataComponents.ATTRIBUTE_MODIFIERS, modifiers)
                        )
                ),
                JolCraftItemRegistryHelper.registerItem(
                        cutId,
                        props -> new CutGemItem(
                                props.component(DataComponents.ATTRIBUTE_MODIFIERS, modifiers)
                        )
                ),
                JolCraftItemRegistryHelper.registerSimpleItem(dustId)
        );
    }

    private static ItemAttributeModifiers buildGemAttribute(
            String gemId,
            JolCraftTrimAttributes.TrimAttribute trimAttribute
    ) {
        ResourceLocation id = JolCraft.location(
                JolCraftStrings.underscored(JolCraftDictionary.GEM, gemId)
        );

        return ItemAttributeModifiers.builder()
                .add(
                        trimAttribute.attribute(),
                        new AttributeModifier(id, trimAttribute.amount(), trimAttribute.operation()),
                        EquipmentSlotGroup.ARMOR
                )
                .build();
    }

    public record GemSet(
            DeferredItem<Item> uncut,
            DeferredItem<Item> cut,
            DeferredItem<Item> dust
    ) {}
}