package net.sievert.jolcraft.datagen.client.language.subprovider;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.data.id.damage.JolCraftDamageTypeIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.datagen.base.JolCraftDataProvider;
import net.sievert.jolcraft.datagen.client.language.LanguageSubProvider;
import net.sievert.jolcraft.util.JolCraftStrings;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class DamageTypeLangSubProvider implements LanguageSubProvider {

    private static final String DEATH_ATTACK_PREFIX = JolCraftStrings.dotted(JolCraftDictionary.DEATH, JolCraftDictionary.ATTACK);

    @Override
    public @NotNull String id() {
        return JolCraftStrings.underscored(JolCraftDictionary.DAMAGE, JolCraftStrings.plural(JolCraftDictionary.TYPE));
    }

    @Override
    public @NotNull JolCraftDataProvider<Map<String, String>> parent() {
        return languageProvider();
    }

    @Override
    public void addTranslations(@NotNull Map<String, String> translations) {
        putDeathAttack(
                translations,
                JolCraftDamageTypeIds.VITALITY_CURSE,
                "%1$s succumbed to a vitality curse",
                "%1$s succumbed to a vitality curse while fighting %2$s"
        );

    }

    private void putDeathAttack(
            @NotNull Map<String, String> translations,
            @NotNull String id,
            @NotNull String baseMessage,
            @NotNull String playerMessage
    ) {
        put(translations, deathAttack(id), baseMessage);
        put(translations, deathAttackPlayer(id), playerMessage);
    }

    private @NotNull String deathAttack(@NotNull String id) {
        return JolCraftStrings.dotted(DEATH_ATTACK_PREFIX, id);
    }

    private @NotNull String deathAttackPlayer(@NotNull String id) {
        return JolCraftStrings.dotted(deathAttack(id), JolCraftDictionary.PLAYER);
    }
}
