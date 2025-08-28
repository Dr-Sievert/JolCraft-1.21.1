package net.sievert.jolcraft.network.proxy;

import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.sievert.jolcraft.data.JolCraftAttachments;
import net.sievert.jolcraft.data.custom.attachment.lang.DwarvenLanguageImpl;
import net.sievert.jolcraft.network.client.data.ClientLanguageData;

/**
 * Client proxy that uses local client cache for some attachments.
 */
@SuppressWarnings("unchecked")
public class JolCraftClientProxy implements JolCraftProxy {
    @Override
    public <T> T getAttachment(AttachmentType<T> type, Player player) {
        if (type == JolCraftAttachments.DWARVEN_LANGUAGE.get()) {
            DwarvenLanguageImpl lang = new DwarvenLanguageImpl();
            lang.setKnowsLanguage(ClientLanguageData.knowsLanguage());
            return (T) lang;
        }
        return player.getData(type);
    }
}
