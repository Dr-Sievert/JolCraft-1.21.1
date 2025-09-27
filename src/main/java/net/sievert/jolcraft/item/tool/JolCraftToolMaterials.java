package net.sievert.jolcraft.item.tool;


import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ToolMaterial;
import net.sievert.jolcraft.data.JolCraftTags;

public class JolCraftToolMaterials {

    public static final ToolMaterial DEEPSLATE = new ToolMaterial(BlockTags.INCORRECT_FOR_IRON_TOOL,
            1200, 6f, 2.5f, 15, JolCraftTags.Items.REPAIRS_DEEPSLATE);

    public static final ToolMaterial MITHRIL = new ToolMaterial(BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
            5000, 10f, 5f, 20, JolCraftTags.Items.REPAIRS_MITHRIL);

}