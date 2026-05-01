package net.sievert.jolcraft.world.player.attachment;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.world.player.attachment.custom.compass.DiscoveredStructuresAttachment;
import net.sievert.jolcraft.world.player.attachment.custom.hearth.HearthAttachment;
import net.sievert.jolcraft.world.player.attachment.custom.language.LanguageAttachment;
import net.sievert.jolcraft.world.player.attachment.custom.lore.DwarfLoreAttachment;
import net.sievert.jolcraft.world.player.attachment.custom.reputation.DwarvenReputationAttachment;
import net.sievert.jolcraft.data.id.attachment.JolCraftAttachmentIds;

import java.util.function.Supplier;

public final class JolCraftAttachments {

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, JolCraft.MOD_ID);


    public static final Supplier<AttachmentType<LanguageAttachment>> LANGUAGE =
            ATTACHMENT_TYPES.register(JolCraftAttachmentIds.LANGUAGE, () ->
                    AttachmentType.builder(LanguageAttachment::new)
                            .serialize(LanguageAttachment.CODEC)
                            .sync(LanguageAttachment.STREAM_CODEC)
                            .copyOnDeath()
                            .build()
            );

    public static final Supplier<AttachmentType<DwarvenReputationAttachment>> DWARVEN_REPUTATION =
            ATTACHMENT_TYPES.register(JolCraftAttachmentIds.DWARVEN_REPUTATION, () ->
                    AttachmentType.builder(DwarvenReputationAttachment::new)
                            .serialize(DwarvenReputationAttachment.CODEC)
                            .sync(DwarvenReputationAttachment.STREAM_CODEC)
                            .copyOnDeath()
                            .build()
            );

    public static final Supplier<AttachmentType<DwarfLoreAttachment>> DWARF_LORE =
            ATTACHMENT_TYPES.register(JolCraftAttachmentIds.DWARF_LORE, () ->
                    AttachmentType.builder(() -> new DwarfLoreAttachment())
                            .serialize(DwarfLoreAttachment.CODEC)
                            .sync(DwarfLoreAttachment.STREAM_CODEC)
                            .copyOnDeath()
                            .build()
            );

    public static final Supplier<AttachmentType<HearthAttachment>> HEARTH =
            ATTACHMENT_TYPES.register(JolCraftAttachmentIds.HEARTH, () ->
                    AttachmentType.builder(HearthAttachment::new)
                            .serialize(HearthAttachment.CODEC)
                            .copyOnDeath()
                            .build()
            );

    public static final Supplier<AttachmentType<DiscoveredStructuresAttachment>> DISCOVERED_STRUCTURES =
            ATTACHMENT_TYPES.register(JolCraftAttachmentIds.DISCOVERED_STRUCTURES, () ->
                    AttachmentType.builder(DiscoveredStructuresAttachment::new)
                            .serialize(DiscoveredStructuresAttachment.CODEC)
                            .sync(DiscoveredStructuresAttachment.STREAM_CODEC)
                            .copyOnDeath()
                            .build()
            );

    private JolCraftAttachments() {}

    public static void register(IEventBus bus) {
        ATTACHMENT_TYPES.register(bus);
    }
}