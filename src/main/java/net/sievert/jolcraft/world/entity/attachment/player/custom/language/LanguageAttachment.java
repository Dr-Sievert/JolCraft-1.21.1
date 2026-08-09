package net.sievert.jolcraft.world.entity.attachment.player.custom.language;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.sievert.jolcraft.world.entity.attachment.base.JolCraftSyncedAttachment;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.util.JolCraftStrings;

public final class LanguageAttachment extends JolCraftSyncedAttachment<LanguageAttachment> {

    private static final String TAG_DWARVEN =
            JolCraftStrings.underscored(LanguageType.DWARVEN.getId(), JolCraftDictionary.LANGUAGE);
    private static final String TAG_ANCIENT_DWARVEN =
            JolCraftStrings.underscored(LanguageType.ANCIENT_DWARVEN.getId(), JolCraftDictionary.LANGUAGE);

    public static final Codec<LanguageAttachment> CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    Codec.BOOL.fieldOf(TAG_DWARVEN).forGetter(LanguageAttachment::hasDwarvenLanguage),
                    Codec.BOOL.fieldOf(TAG_ANCIENT_DWARVEN).forGetter(LanguageAttachment::hasAncientDwarvenLanguage)
            ).apply(instance, LanguageAttachment::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, LanguageAttachment> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.BOOL,
                    LanguageAttachment::hasDwarvenLanguage,
                    ByteBufCodecs.BOOL,
                    LanguageAttachment::hasAncientDwarvenLanguage,
                    LanguageAttachment::new
            );

    private final boolean dwarvenLanguage;
    private final boolean ancientDwarvenLanguage;

    public LanguageAttachment() {
        this(false, false);
    }

    public LanguageAttachment(boolean dwarvenLanguage, boolean ancientDwarvenLanguage) {
        this.dwarvenLanguage = dwarvenLanguage;
        this.ancientDwarvenLanguage = ancientDwarvenLanguage;
    }

    public boolean hasDwarvenLanguage() {
        return dwarvenLanguage;
    }

    public boolean hasAncientDwarvenLanguage() {
        return ancientDwarvenLanguage;
    }

    public boolean hasLanguage(LanguageType type) {
        return switch (type) {
            case DWARVEN -> hasDwarvenLanguage();
            case ANCIENT_DWARVEN -> hasAncientDwarvenLanguage();
        };
    }

    public LanguageAttachment withDwarvenLanguage(boolean value) {
        return this.dwarvenLanguage == value
                ? this
                : new LanguageAttachment(value, ancientDwarvenLanguage);
    }

    public LanguageAttachment withAncientDwarvenLanguage(boolean value) {
        return this.ancientDwarvenLanguage == value
                ? this
                : new LanguageAttachment(dwarvenLanguage, value);
    }

    public LanguageAttachment withLanguage(LanguageType type, boolean value) {
        return switch (type) {
            case DWARVEN -> withDwarvenLanguage(value);
            case ANCIENT_DWARVEN -> withAncientDwarvenLanguage(value);
        };
    }

    @Override
    public Codec<LanguageAttachment> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, LanguageAttachment> streamCodec() {
        return STREAM_CODEC;
    }
}