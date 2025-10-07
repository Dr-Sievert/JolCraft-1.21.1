package net.sievert.jolcraft.entity.client.model.dwarf.profession;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.entity.client.model.dwarf.DwarfModel;
import net.sievert.jolcraft.entity.client.util.dwarf.DwarfModelHelper;
import net.sievert.jolcraft.entity.client.util.dwarf.DwarfRenderState;

@OnlyIn(Dist.CLIENT)
public class DwarfHistorianModel extends DwarfModel {

    protected static String getModelLayerName() {
        return "dwarf_historian";
    }

    public final ModelPart glasses;

    public DwarfHistorianModel(ModelPart root) {
        super(root);
        this.glasses = this.head.getChild("glasses");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition root = meshdefinition.getRoot();

        DwarfModelHelper.baseDwarfModel(root);

        PartDefinition head = root.getChild("head");
        head.addOrReplaceChild("glasses",
                CubeListBuilder.create()
                        .texOffs(76, 10).addBox(-3.0F, -3.75F, -4.76F, 2.0F, 1.0F, 0.01F, new CubeDeformation(0.0F))
                        .texOffs(78, 10).addBox(1.0F, -3.75F, -4.76F, 2.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 2.0F, 2.0F)
        );

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void setupAnim(DwarfRenderState state) {
        super.setupAnim(state);
        DwarfModelHelper.visibleOuterLayer(this);
    }

}
