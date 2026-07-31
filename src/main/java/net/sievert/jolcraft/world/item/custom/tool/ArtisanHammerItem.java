package net.sievert.jolcraft.world.item.custom.tool;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tier;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ArtisanHammerItem extends ToolItem {

    public ArtisanHammerItem(Tier tier, Item.Properties properties) {
        super(tier, properties);
    }
}
