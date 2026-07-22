package net.sievert.jolcraft.data.id.model;

import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.util.JolCraftStrings;

public final class JolCraftModelPartIds {

    private JolCraftModelPartIds() {}

    public static final class Creature {

        private Creature() {}

        // Core
        public static final String BODY = JolCraftDictionary.BODY;
        public static final String HEAD = JolCraftDictionary.HEAD;

        public static final class Humanoid {

            private Humanoid() {}

            // -----------------------------------------------------------------
            // Limbs
            // -----------------------------------------------------------------

            public static final String RIGHT_ARM =
                    JolCraftStrings.underscored(JolCraftDictionary.RIGHT, JolCraftDictionary.ARM);

            public static final String LEFT_ARM =
                    JolCraftStrings.underscored(JolCraftDictionary.LEFT, JolCraftDictionary.ARM);

            public static final String RIGHT_LEG =
                    JolCraftStrings.underscored(JolCraftDictionary.RIGHT, JolCraftDictionary.LEG);

            public static final String LEFT_LEG =
                    JolCraftStrings.underscored(JolCraftDictionary.LEFT, JolCraftDictionary.LEG);

            // -----------------------------------------------------------------
            // Wear
            // -----------------------------------------------------------------

            public static final String BODYWEAR =
                    JolCraftStrings.underscored(Creature.BODY, JolCraftDictionary.WEAR);

            public static final String LEGWEAR =
                    JolCraftStrings.underscored(JolCraftDictionary.LEG, JolCraftDictionary.WEAR);

            public static final String RIGHT_ARMWEAR =
                    JolCraftStrings.underscored(RIGHT_ARM, JolCraftDictionary.WEAR);

            public static final String LEFT_ARMWEAR =
                    JolCraftStrings.underscored(LEFT_ARM, JolCraftDictionary.WEAR);

            public static final String RIGHT_FOOTWEAR =
                    JolCraftStrings.underscored(
                            JolCraftStrings.underscored(JolCraftDictionary.RIGHT, JolCraftDictionary.FOOT),
                            JolCraftDictionary.WEAR
                    );

            public static final String LEFT_FOOTWEAR =
                    JolCraftStrings.underscored(
                            JolCraftStrings.underscored(JolCraftDictionary.LEFT, JolCraftDictionary.FOOT),
                            JolCraftDictionary.WEAR
                    );

            // -----------------------------------------------------------------
            // Head extras
            // -----------------------------------------------------------------

            public static final String HAT = JolCraftDictionary.HAT;

            public static final String RIGHT_EYE =
                    JolCraftStrings.underscored(JolCraftDictionary.RIGHT, JolCraftDictionary.EYE);

            public static final String LEFT_EYE =
                    JolCraftStrings.underscored(JolCraftDictionary.LEFT, JolCraftDictionary.EYE);

            public static final String RIGHT_EYEBROW =
                    JolCraftStrings.underscored(RIGHT_EYE, JolCraftDictionary.BROW);

            public static final String LEFT_EYEBROW =
                    JolCraftStrings.underscored(LEFT_EYE, JolCraftDictionary.BROW);

            public static final class Dwarf {

                private Dwarf() {}

                // -----------------------------------------------------------------
                // Dwarf-specific
                // -----------------------------------------------------------------

                public static final String BEARD = JolCraftDictionary.BEARD;

                public static final String SHIELD = JolCraftDictionary.SHIELD;
                public static final String BACKPACK = JolCraftDictionary.BACKPACK;
                public static final String SACK = JolCraftDictionary.SACK;

                public static final String GLASSES_MERCHANT =
                        JolCraftStrings.underscored(
                                JolCraftDictionary.GLASSES,
                                JolCraftDictionary.MERCHANT
                        );

                public static final String GLASSES_HISTORIAN =
                        JolCraftStrings.underscored(
                                JolCraftDictionary.GLASSES,
                                JolCraftDictionary.HISTORIAN
                        );

                public static final String HAT_KEEPER =
                        JolCraftStrings.underscored(
                                JolCraftDictionary.HAT,
                                JolCraftDictionary.KEEPER
                        );

                public static final String HAT_EXPLORER_EXTRA =
                        JolCraftStrings.underscored(
                                JolCraftDictionary.HAT,
                                JolCraftDictionary.EXPLORER,
                                JolCraftDictionary.EXTRA
                        );
            }
        }
    }
}