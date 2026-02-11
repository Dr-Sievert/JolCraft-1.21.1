package net.sievert.jolcraft.data.attachment;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.attachment.custom.hearth.HearthImpl;
import net.sievert.jolcraft.data.attachment.custom.compass.DiscoveredStructuresImpl;
import net.sievert.jolcraft.data.attachment.custom.language.ancient.AncientDwarvenLanguageImpl;
import net.sievert.jolcraft.data.attachment.custom.language.DwarvenLanguageImpl;
import net.sievert.jolcraft.data.attachment.custom.player.AttributesAttachment;
import net.sievert.jolcraft.data.attachment.custom.reputation.DwarvenReputationImpl;
import net.sievert.jolcraft.data.attachment.custom.lore.DwarfLoreUnlockImpl;
import net.sievert.jolcraft.data.id.attachment.JolCraftAttachmentIds;

import java.util.function.Supplier;

public class JolCraftAttachments {

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, JolCraft.MOD_ID);

    public static final Supplier<AttachmentType<AttributesAttachment>> ATTRIBUTES =
            ATTACHMENT_TYPES.register(JolCraftAttachmentIds.ATTRIBUTES, () ->
                    AttachmentType.builder(AttributesAttachment::new).build()
            );

    public static final Supplier<AttachmentType<DwarvenLanguageImpl>> DWARVEN_LANGUAGE =
            ATTACHMENT_TYPES.register(JolCraftAttachmentIds.DWARVEN_LANGUAGE, () ->
                    AttachmentType.serializable(DwarvenLanguageImpl::new)
                            .copyOnDeath()
                            .build()
            );

    public static final Supplier<AttachmentType<AncientDwarvenLanguageImpl>> ANCIENT_DWARVEN_LANGUAGE =
            ATTACHMENT_TYPES.register(JolCraftAttachmentIds.ANCIENT_DWARVEN_LANGUAGE, () ->
                    AttachmentType.serializable(AncientDwarvenLanguageImpl::new)
                            .copyOnDeath()
                            .build()
            );

    public static final Supplier<AttachmentType<DwarvenReputationImpl>> DWARVEN_REPUTATION =
            ATTACHMENT_TYPES.register(JolCraftAttachmentIds.DWARVEN_REPUTATION, () ->
                    AttachmentType.serializable(DwarvenReputationImpl::new)
                            .copyOnDeath()
                            .build()
            );

    public static final Supplier<AttachmentType<DwarfLoreUnlockImpl>> DWARF_TOME_UNLOCK =
            ATTACHMENT_TYPES.register(JolCraftAttachmentIds.DWARF_TOME_UNLOCK, () ->
                    AttachmentType.serializable(DwarfLoreUnlockImpl::new)
                            .copyOnDeath()
                            .build()
            );

    public static final Supplier<AttachmentType<HearthImpl>> HEARTH =
            ATTACHMENT_TYPES.register(JolCraftAttachmentIds.HEARTH, () ->
                    AttachmentType.serializable(HearthImpl::new)
                            .copyOnDeath()
                            .build()
            );

    public static final Supplier<AttachmentType<DiscoveredStructuresImpl>> DISCOVERED_STRUCTURES =
            ATTACHMENT_TYPES.register(JolCraftAttachmentIds.DISCOVERED_STRUCTURES, () ->
                    AttachmentType.serializable(DiscoveredStructuresImpl::new)
                            .copyOnDeath()
                            .build()
            );


    public static void register(IEventBus bus) {
        ATTACHMENT_TYPES.register(bus);
    }
}
