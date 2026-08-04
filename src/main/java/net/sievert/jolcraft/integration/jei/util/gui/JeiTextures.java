package net.sievert.jolcraft.integration.jei.util.gui;

import net.minecraft.resources.ResourceLocation;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.util.client.JolCraftTextures;

public final class JeiTextures {

    public static final ResourceLocation RECIPE_ARROW =
            JolCraftTextures.jeiRl(
                    JolCraftTextures.jei(
                            JolCraftStrings.underscored(
                                    JolCraftDictionary.RECIPE,
                                    JolCraftDictionary.ARROW
                            )
                    )
            );

    public static final ResourceLocation RECIPE_PLUS =
            JolCraftTextures.jeiRl(
                    JolCraftTextures.jei(
                            JolCraftStrings.underscored(
                                    JolCraftDictionary.RECIPE,
                                    JolCraftDictionary.PLUS,
                                    JolCraftDictionary.SIGN
                            )
                    )
            );

    public static final ResourceLocation RIGHT_CLICK =
            ResourceLocation.withDefaultNamespace(
                    "textures/gui/sprites/toast/right_click.png"
            );

    public static final ResourceLocation HAND_RIGHT =
            JolCraftTextures.modSprite(
                    "hand_right"
            );

    public static final ResourceLocation HAND_LEFT =
            JolCraftTextures.modSprite(
                    "hand_left"
            );

    public static final ResourceLocation UNSEEN_NOTIFICATION =
            ResourceLocation.withDefaultNamespace(
                    "icon/unseen_notification"
            );

    public static final ResourceLocation INFO =
            JolCraftTextures.jeiRl(
                    JolCraftTextures.jeiIcon(
                            JolCraftDictionary.INFO
                    )
            );

    public static final int INFO_SIZE = 16;
    public static final int UNSEEN_NOTIFICATION_SIZE = 10;

    public static final int ARROW_WIDTH = 22;
    public static final int ARROW_HEIGHT = 16;

    public static final int PLUS_WIDTH = 13;
    public static final int PLUS_HEIGHT = 13;

    public static final int RIGHT_CLICK_SIZE = 20;
    public static final int HAND_SIZE = 16;

    private JeiTextures() {
    }
}
