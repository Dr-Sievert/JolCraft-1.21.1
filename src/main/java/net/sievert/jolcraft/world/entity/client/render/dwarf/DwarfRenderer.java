package net.sievert.jolcraft.world.entity.client.render.dwarf;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.Util;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.world.entity.JolCraftEntities;
import net.sievert.jolcraft.world.entity.client.model.dwarf.DwarfModel;
import net.sievert.jolcraft.world.entity.client.util.dwarf.DwarfRenderState;
import net.sievert.jolcraft.world.entity.client.util.dwarf.animation.DwarfAnimationHelper;
import net.sievert.jolcraft.world.entity.client.util.dwarf.layer.DwarfArmorLayer;
import net.sievert.jolcraft.world.entity.client.util.dwarf.layer.DwarfBeardLayer;
import net.sievert.jolcraft.world.entity.client.util.dwarf.layer.DwarfEyeLayer;
import net.sievert.jolcraft.world.entity.client.util.layer.EmissiveLayer;
import net.sievert.jolcraft.world.entity.custom.dwarf.base.AbstractBreedingEntity;
import net.sievert.jolcraft.world.entity.custom.dwarf.base.AbstractDwarfEntity;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.action.DwarfActionHelper;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.variation.DwarfBeardColor;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.variation.DwarfEyeColor;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.variation.DwarfVariant;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class DwarfRenderer<T extends AbstractDwarfEntity> extends HumanoidMobRenderer<T, DwarfRenderState, DwarfModel> {

    private static final String DWARF_TEXTURE_ROOT = "textures/entity/dwarf/";
    private static final String DWARF_PROFESSION_TEXTURE_ROOT = DWARF_TEXTURE_ROOT + "profession/";
    private static final String EMISSIVE_SUFFIX = "_emissive";
    private static final String PNG = ".png";

    private static ResourceLocation dwarfTexture(@NotNull String name) {
        return JolCraft.location(DWARF_TEXTURE_ROOT + name + PNG);
    }

    private static ResourceLocation dwarfProfessionTexture(@NotNull String name) {
        return JolCraft.location(DWARF_PROFESSION_TEXTURE_ROOT + name + PNG);
    }

    private static ResourceLocation dwarfProfessionEmissiveTexture(@NotNull String entityPath) {
        return dwarfProfessionTexture(entityPath + EMISSIVE_SUFFIX);
    }

    private static final ResourceLocation FALLBACK_TEXTURE = dwarfTexture("dwarf_grey");

    private static final int LAYER_ARMOR = 1;
    private static final int LAYER_BEARD = 2;
    private static final int LAYER_EYE   = 4;

    private record Profile(int layersMask, float extraScale, boolean hasEmissive) {}

    @Nullable
    private final ResourceLocation fixedTexture;

    private final float extraScale;

    // === Base dwarf: variant texture ===
    public DwarfRenderer(EntityRendererProvider.Context context) {
        super(context, new DwarfModel(context.bakeLayer(DwarfModel.LAYER_LOCATION)), 0.4f);
        this.fixedTexture = null;
        this.extraScale = 1.0f;

        addLayers(LAYER_ARMOR | LAYER_BEARD | LAYER_EYE, null);
    }

    private DwarfRenderer(
            EntityRendererProvider.Context context,
            @NotNull ResourceLocation fixedTexture,
            float extraScale,
            int layersMask,
            @Nullable ResourceLocation emissiveTexture
    ) {
        super(context, new DwarfModel(context.bakeLayer(DwarfModel.LAYER_LOCATION)), 0.4f);
        this.fixedTexture = fixedTexture;
        this.extraScale = extraScale;

        addLayers(layersMask, emissiveTexture);
    }

    private void addLayers(int layersMask, @Nullable ResourceLocation emissiveTexture) {
        if ((layersMask & LAYER_ARMOR) != 0) this.addLayer(new DwarfArmorLayer(this));
        if ((layersMask & LAYER_BEARD) != 0) this.addLayer(new DwarfBeardLayer(this));
        if ((layersMask & LAYER_EYE) != 0)   this.addLayer(new DwarfEyeLayer(this));
        if (emissiveTexture != null)         this.addLayer(new EmissiveLayer<>(this, emissiveTexture));
    }

    // === Profession entry point: decide layers + emissive + scale based on entity getId path ===
    public static <T extends AbstractDwarfEntity> DwarfRenderer<T> profession(EntityRendererProvider.Context context, @NotNull EntityType<?> type) {
        return profession(context, type, 1.0f);
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
        if (type == JolCraftEntities.DWARF_PRIEST.get()|| type == JolCraftEntities.DWARF_ARCANIST.get()) {
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

    private static final Map<DwarfVariant, ResourceLocation> LOCATION_BY_VARIANT =
            Util.make(Maps.newEnumMap(DwarfVariant.class), map -> {
                map.put(DwarfVariant.GREY,   dwarfTexture("dwarf_grey"));
                map.put(DwarfVariant.BLUE,   dwarfTexture("dwarf_blue"));
                map.put(DwarfVariant.GREEN,  dwarfTexture("dwarf_green"));
                map.put(DwarfVariant.RED,    dwarfTexture("dwarf_red"));
                map.put(DwarfVariant.PURPLE, dwarfTexture("dwarf_purple"));
                map.put(DwarfVariant.WHITE,  dwarfTexture("dwarf_white"));
                map.put(DwarfVariant.YELLOW, dwarfTexture("dwarf_yellow"));
            });

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull DwarfRenderState entity) {
        if (fixedTexture != null) {
            return fixedTexture;
        }
        ResourceLocation loc = LOCATION_BY_VARIANT.get(entity.variant);
        return (loc != null) ? loc : FALLBACK_TEXTURE;
    }

    @Override
    public void render(
            @NotNull DwarfRenderState renderState,
            @NotNull PoseStack poseStack,
            @NotNull MultiBufferSource bufferSource,
            int packedLight
    ) {
        if (extraScale != 1.0f) {
            poseStack.scale(extraScale, extraScale, extraScale);
        }

        if (renderState.isBaby) {
            poseStack.scale(0.45f, 0.45f, 0.45f);
        } else {
            poseStack.scale(0.9f, 0.9f, 0.9f);
        }

        super.render(renderState, poseStack, bufferSource, packedLight);
    }

    @Override
    public @NotNull DwarfRenderState createRenderState() {
        return new DwarfRenderState();
    }

    @Override
    public void extractRenderState(@NotNull T entity, @NotNull DwarfRenderState reused, float partialTick) {
        super.extractRenderState(entity, reused, partialTick);

        DwarfRenderState persistent = DwarfRenderState.getOrCreate(entity);

        persistent.currentActionType = DwarfActionHelper.getCurrentActionType(entity);
        persistent.currentActionSubtype = DwarfActionHelper.getCurrentActionSubType(entity);
        DwarfAnimationHelper.updateAnimationStates(persistent, persistent.currentActionType, entity.tickCount);
        persistent.ageInTicks = entity.tickCount + partialTick;

        reused.currentActionType = persistent.currentActionType;
        reused.currentActionSubtype = persistent.currentActionSubtype;
        reused.ageInTicks = persistent.ageInTicks;

        reused.dwarf = entity;
        reused.variant = DwarfVariant.byId(entity.getData(AbstractBreedingEntity.VARIANT));
        reused.beard = DwarfBeardColor.byId(entity.getData(AbstractBreedingEntity.BEARD_COLOR));
        reused.eye = DwarfEyeColor.byId(entity.getData(AbstractBreedingEntity.EYE_COLOR));
        reused.useItemHand = entity.getUsedItemHand();
        reused.ticksUsingItem = entity.getTicksUsingItem();
        reused.isUsingItem = entity.isUsingItem();
        reused.headEquipment = entity.getItemBySlot(EquipmentSlot.HEAD);
        reused.chestEquipment = entity.getItemBySlot(EquipmentSlot.CHEST);
        reused.legsEquipment = entity.getItemBySlot(EquipmentSlot.LEGS);
        reused.feetEquipment = entity.getItemBySlot(EquipmentSlot.FEET);

        reused.animationStates.clear();
        reused.animationStates.putAll(persistent.animationStates);
    }
}