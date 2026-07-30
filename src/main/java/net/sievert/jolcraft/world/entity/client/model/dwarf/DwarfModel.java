package net.sievert.jolcraft.world.entity.client.model.dwarf;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.entity.dwarf.JolCraftDwarfIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.world.entity.JolCraftEntities;
import net.sievert.jolcraft.world.entity.client.util.dwarf.DwarfModelHelper;
import net.sievert.jolcraft.world.entity.client.util.dwarf.animation.DwarfAnimationHelper;
import net.sievert.jolcraft.world.entity.client.util.dwarf.animation.DwarfAnimations;
import net.sievert.jolcraft.world.entity.custom.dwarf.base.AbstractDwarfEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@OnlyIn(Dist.CLIENT)
public class DwarfModel<T extends AbstractDwarfEntity> extends AbstractHumanoidHierarchicalModel<T> {

    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(JolCraft.location(JolCraftDwarfIds.DWARF), JolCraftDictionary.MAIN);

    public final ModelPart right_arm;
    public final ModelPart left_arm;
    public final ModelPart right_leg;
    public final ModelPart left_leg;

    public final ModelPart bodywear;
    public final ModelPart legwear;
    public final ModelPart right_armwear;
    public final ModelPart left_armwear;
    public final ModelPart right_footwear;
    public final ModelPart left_footwear;

    public final ModelPart beard;
    public final ModelPart right_eyebrow;
    public final ModelPart left_eyebrow;
    public final ModelPart right_eye;
    public final ModelPart left_eye;

    @Nullable private final ModelPart shield;
    @Nullable private final ModelPart backpack;
    @Nullable private final ModelPart sack;
    @Nullable private final ModelPart merchantGlasses;
    @Nullable private final ModelPart historianGlasses;
    @Nullable private final ModelPart keeperHat;
    @Nullable private final ModelPart explorerHatExtra;

    @Nullable
    private EntityType<?> activeType;

    public DwarfModel(ModelPart root) {
        super(root);

        this.right_arm = this.rightArm;
        this.left_arm = this.leftArm;
        this.right_leg = this.rightLeg;
        this.left_leg = this.leftLeg;

        this.bodywear = this.body.getChild(DwarfModelHelper.PART_BODYWEAR);
        this.legwear = this.body.getChild(DwarfModelHelper.PART_LEGWEAR);
        this.right_armwear = this.right_arm.getChild(DwarfModelHelper.PART_RIGHT_ARMWEAR);
        this.left_armwear = this.left_arm.getChild(DwarfModelHelper.PART_LEFT_ARMWEAR);
        this.right_footwear = this.right_leg.getChild(DwarfModelHelper.PART_RIGHT_FOOTWEAR);
        this.left_footwear = this.left_leg.getChild(DwarfModelHelper.PART_LEFT_FOOTWEAR);

        this.beard = this.head.getChild(DwarfModelHelper.PART_BEARD);
        this.right_eyebrow = this.head.getChild(DwarfModelHelper.PART_RIGHT_EYEBROW);
        this.left_eyebrow = this.head.getChild(DwarfModelHelper.PART_LEFT_EYEBROW);
        this.right_eye = this.head.getChild(DwarfModelHelper.PART_RIGHT_EYE);
        this.left_eye = this.head.getChild(DwarfModelHelper.PART_LEFT_EYE);

        this.shield = tryGetChild(this.left_arm, DwarfModelHelper.PART_SHIELD);
        this.backpack = tryGetChild(this.body, DwarfModelHelper.PART_BACKPACK);
        this.sack = tryGetChild(this.body, DwarfModelHelper.PART_SACK);
        this.merchantGlasses = tryGetChild(this.head, DwarfModelHelper.PART_GLASSES_MERCHANT);
        this.historianGlasses = tryGetChild(this.head, DwarfModelHelper.PART_GLASSES_HISTORIAN);
        this.keeperHat = tryGetChild(this.head, DwarfModelHelper.PART_HAT_KEEPER);
        this.explorerHatExtra = tryGetChild(this.head, DwarfModelHelper.PART_HAT_EXPLORER_EXTRA);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        DwarfModelHelper.baseDwarfModel(root);
        DwarfModelHelper.addAllProfessionExtras(root);

        return LayerDefinition.create(mesh, 128, 128);
    }

    @Override
    public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.activeType = entity.getType();

        this.root.getAllParts().forEach(ModelPart::resetPose);
        this.applyHeadRotation(netHeadYaw, headPitch);

        this.animateWalk(DwarfAnimations.WALK, limbSwing, limbSwingAmount, 2.0F, 2.5F);
        DwarfAnimationHelper.animate(entity, this, ageInTicks);

        this.hat.visible = !entity.getItemBySlot(EquipmentSlot.HEAD).isEmpty();

        boolean hasChest = !entity.getItemBySlot(EquipmentSlot.CHEST).isEmpty();
        this.bodywear.visible = hasChest;
        this.right_armwear.visible = hasChest;
        this.left_armwear.visible = hasChest;

        this.legwear.visible = !entity.getItemBySlot(EquipmentSlot.LEGS).isEmpty();

        boolean hasBoots = !entity.getItemBySlot(EquipmentSlot.FEET).isEmpty();
        this.right_footwear.visible = hasBoots;
        this.left_footwear.visible = hasBoots;

        this.setProfessionExtrasVisible(false);

        if (this.activeType == JolCraftEntities.DWARF_GUARD.get()) {
            setVisible(this.shield, true);
            return;
        }

        if (this.activeType == JolCraftEntities.DWARF_EXPLORER.get()) {
            this.hat.visible = true;
            this.bodywear.visible = true;
            this.right_armwear.visible = true;
            this.left_armwear.visible = true;
            setVisible(this.explorerHatExtra, true);
            return;
        }

        if (this.activeType == JolCraftEntities.DWARF_KEEPER.get()) {
            DwarfModelHelper.visibleOuterLayer(this);
            this.hat.visible = false;
            setVisible(this.keeperHat, true);
            setVisible(this.sack, true);
            return;
        }

        if (this.activeType == JolCraftEntities.DWARF_MERCHANT.get()) {
            DwarfModelHelper.visibleOuterLayer(this);
            setVisible(this.merchantGlasses, true);
            return;
        }

        if (this.activeType == JolCraftEntities.DWARF_HISTORIAN.get()) {
            DwarfModelHelper.visibleOuterLayer(this);
            setVisible(this.historianGlasses, true);
            return;
        }

        if (this.activeType == JolCraftEntities.DWARF_MINER.get()) {
            DwarfModelHelper.visibleOuterLayer(this);
            setVisible(this.backpack, true);
            return;
        }

        if (this.activeType == JolCraftEntities.DWARF_ALCHEMIST.get()
                || this.activeType == JolCraftEntities.DWARF_ARCANIST.get()
                || this.activeType == JolCraftEntities.DWARF_ARTISAN.get()
                || this.activeType == JolCraftEntities.DWARF_BREWMASTER.get()
                || this.activeType == JolCraftEntities.DWARF_GUILDMASTER.get()
                || this.activeType == JolCraftEntities.DWARF_SCRAPPER.get()
                || this.activeType == JolCraftEntities.DWARF_PRIEST.get()
                || this.activeType == JolCraftEntities.DWARF_BLACKSMITH.get()
                || this.activeType == JolCraftEntities.DWARF_CHAMPION.get()
                || this.activeType == JolCraftEntities.DWARF_SMELTER.get()) {
            DwarfModelHelper.visibleOuterLayer(this);
        }
    }

    @Override
    public void translateToHand(@NotNull HumanoidArm side, @NotNull PoseStack poseStack) {
        this.root.translateAndRotate(poseStack);
        this.getArm(side).translateAndRotate(poseStack);

        float x = -0.05F;
        float y = -0.15F;
        float z = 0.05F;

        if (this.activeType != null) {
            if (this.activeType == JolCraftEntities.DWARF_MINER.get()
                    || this.activeType == JolCraftEntities.DWARF_GUARD.get()) {
                y = -0.03F;
                z = 0.13F;
            } else if (side == HumanoidArm.LEFT) {
                if (this.activeType == JolCraftEntities.DWARF_ARTISAN.get()) {
                    x = 0.05F;
                    y = -0.03F;
                    z = 0.0F;
                } else if (this.activeType == JolCraftEntities.DWARF_BREWMASTER.get()
                        || this.activeType == JolCraftEntities.DWARF_EXPLORER.get()) {
                    x = 0.1F;
                    z = 0.0F;
                } else if (this.activeType == JolCraftEntities.DWARF_KEEPER.get()) {
                    x = 0.05F;
                    y = -0.05F;
                }
            }
        }

        poseStack.translate(x, y, z);
    }

    @Override
    protected @NotNull ModelPart getArm(@NotNull HumanoidArm side) {
        return side == HumanoidArm.RIGHT ? this.right_arm : this.left_arm;
    }

    protected void applyHeadRotation(float headYaw, float headPitch) {
        headYaw = Mth.clamp(headYaw, -12.5F, 12.5F);
        headPitch = Mth.clamp(headPitch, -5.0F, 30.0F);

        this.head.yRot = headYaw * ((float) Math.PI / 180.0F);
        this.head.xRot = headPitch * ((float) Math.PI / 180.0F);
    }

    private void setProfessionExtrasVisible(boolean visible) {
        setVisible(this.shield, visible);
        setVisible(this.backpack, visible);
        setVisible(this.sack, visible);
        setVisible(this.merchantGlasses, visible);
        setVisible(this.historianGlasses, visible);
        setVisible(this.keeperHat, visible);
        setVisible(this.explorerHatExtra, visible);
    }

    private static void setVisible(@Nullable ModelPart part, boolean visible) {
        if (part != null) {
            part.visible = visible;
        }
    }

    private static @Nullable ModelPart tryGetChild(ModelPart parent, String name) {
        try {
            return parent.getChild(name);
        } catch (Exception ignored) {
            return null;
        }
    }
}