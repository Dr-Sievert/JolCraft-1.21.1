package net.sievert.jolcraft.world.entity.client.render.dwarf;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.Util;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.data.id.directory.JolCraftDirectoryIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.util.client.JolCraftTextures;
import net.sievert.jolcraft.world.entity.JolCraftEntities;
import net.sievert.jolcraft.world.entity.client.model.dwarf.DwarfModel;
import net.sievert.jolcraft.world.entity.client.util.dwarf.layer.DwarfArmorLayer;
import net.sievert.jolcraft.world.entity.client.util.dwarf.layer.DwarfBeardLayer;
import net.sievert.jolcraft.world.entity.client.util.dwarf.layer.DwarfEyeLayer;
import net.sievert.jolcraft.world.entity.client.util.layer.EmissiveLayer;
import net.sievert.jolcraft.world.entity.custom.dwarf.base.AbstractBreedingEntity;
import net.sievert.jolcraft.world.entity.custom.dwarf.base.AbstractDwarfEntity;
import net.sievert.jolcraft.world.entity.custom.dwarf.variant.DwarfVariant;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class DwarfRenderer<T extends AbstractDwarfEntity> extends MobRenderer<T, DwarfModel<T>> {

    private static final String EMISSIVE_SUFFIX = "_" + JolCraftDictionary.EMISSIVE;

    private static final int LAYER_ARMOR = 1;
    private static final int LAYER_BEARD = 2;
    private static final int LAYER_EYE = 4;

    private record Profile(int layersMask, float extraScale, boolean hasEmissive) {}

    private static ResourceLocation dwarfTexture(@NotNull String name) {
        return JolCraftTextures.mod(JolCraftTextures.dwarf(name));
    }

    private static ResourceLocation dwarfProfessionTexture(@NotNull String name) {
        return JolCraftTextures.mod(JolCraftTextures.dwarf(JolCraftDirectoryIds.PROFESSION, name));
    }

    private static ResourceLocation dwarfProfessionEmissiveTexture(@NotNull String entityPath) {
        return dwarfProfessionTexture(entityPath + EMISSIVE_SUFFIX);
    }

    private static String dwarfVariantTextureName(@NotNull DwarfVariant variant) {
        return JolCraftStrings.underscored(JolCraftDictionary.DWARF, variant.getId());
    }

    private static final ResourceLocation FALLBACK_TEXTURE =
            dwarfTexture(dwarfVariantTextureName(DwarfVariant.GREY));

    private static final Map<DwarfVariant, ResourceLocation> LOCATION_BY_VARIANT =
            Util.make(Maps.newEnumMap(DwarfVariant.class), map -> {
                for (DwarfVariant variant : DwarfVariant.values()) {
                    map.put(variant, dwarfTexture(dwarfVariantTextureName(variant)));
                }
            });

    @Nullable
    private final ResourceLocation fixedTexture;

    private final float extraScale;

    public DwarfRenderer(EntityRendererProvider.Context context) {
        super(context, new DwarfModel<>(context.bakeLayer(DwarfModel.LAYER_LOCATION)), 0.4F);
        this.fixedTexture = null;
        this.extraScale = 1.0F;
        this.addLayers(context, LAYER_ARMOR | LAYER_BEARD | LAYER_EYE, null);
    }

    private DwarfRenderer(
            EntityRendererProvider.Context context,
            @NotNull ResourceLocation fixedTexture,
            float extraScale,
            int layersMask,
            @Nullable ResourceLocation emissiveTexture
    ) {
        super(context, new DwarfModel<>(context.bakeLayer(DwarfModel.LAYER_LOCATION)), 0.4F);
        this.fixedTexture = fixedTexture;
        this.extraScale = extraScale;
        this.addLayers(context, layersMask, emissiveTexture);
    }

    private void addLayers(
            @NotNull EntityRendererProvider.Context context,
            int layersMask,
            @Nullable ResourceLocation emissiveTexture
    ) {
        this.addLayer(new ItemInHandLayer<>(this, context.getItemInHandRenderer()));

        if ((layersMask & LAYER_ARMOR) != 0) {
            this.addLayer(new DwarfArmorLayer<>(this));
        }
        if ((layersMask & LAYER_BEARD) != 0) {
            this.addLayer(new DwarfBeardLayer<>(this));
        }
        if ((layersMask & LAYER_EYE) != 0) {
            this.addLayer(new DwarfEyeLayer<>(this));
        }
        if (emissiveTexture != null) {
            this.addLayer(new EmissiveLayer<>(this, emissiveTexture));
        }
    }

    public static <T extends AbstractDwarfEntity> DwarfRenderer<T> profession(
            EntityRendererProvider.Context context,
            @NotNull EntityType<?> type
    ) {
        return profession(context, type, 1.0F);
    }

    @SuppressWarnings("deprecation")
    public static <T extends AbstractDwarfEntity> DwarfRenderer<T> profession(
            EntityRendererProvider.Context context,
            @NotNull EntityType<?> type,
            float overrideScale
    ) {
        ResourceLocation id = type.builtInRegistryHolder().key().location();
        String path = id.getPath();

        Profile profile = profileFor(type, overrideScale);
        ResourceLocation emissive = profile.hasEmissive ? dwarfProfessionEmissiveTexture(path) : null;

        return new DwarfRenderer<>(
                context,
                dwarfProfessionTexture(path),
                profile.extraScale,
                profile.layersMask,
                emissive
        );
    }

    private static Profile profileFor(@NotNull EntityType<?> type, float scale) {
        if (type == JolCraftEntities.DWARF_GUILDMASTER.get()) {
            return new Profile(LAYER_EYE, scale, false);
        }
        if (type == JolCraftEntities.DWARF_ALCHEMIST.get()) {
            return new Profile(LAYER_BEARD, scale, false);
        }
        if (type == JolCraftEntities.DWARF_PRIEST.get() || type == JolCraftEntities.DWARF_ARCANIST.get()) {
            return new Profile(LAYER_BEARD, scale, true);
        }
        if (type == JolCraftEntities.DWARF_EXPLORER.get()) {
            return new Profile(LAYER_ARMOR | LAYER_BEARD | LAYER_EYE, scale, false);
        }
        if (type == JolCraftEntities.DWARF_GUARD.get()) {
            return new Profile(LAYER_ARMOR | LAYER_BEARD | LAYER_EYE, scale, false);
        }
        return new Profile(LAYER_BEARD | LAYER_EYE, scale, false);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull T entity) {
        if (this.fixedTexture != null) {
            return this.fixedTexture;
        }

        DwarfVariant variant = DwarfVariant.byId(entity.getData(AbstractBreedingEntity.VARIANT));
        ResourceLocation loc = LOCATION_BY_VARIANT.get(variant);
        return loc != null ? loc : FALLBACK_TEXTURE;
    }

    @Override
    protected void scale(@NotNull T entity, @NotNull PoseStack poseStack, float partialTickTime) {
        if (this.extraScale != 1.0F) {
            poseStack.scale(this.extraScale, this.extraScale, this.extraScale);
        }

        if (entity.isBaby()) {
            poseStack.scale(0.45F, 0.45F, 0.45F);
        } else {
            poseStack.scale(0.9F, 0.9F, 0.9F);
        }

        super.scale(entity, poseStack, partialTickTime);
    }
}