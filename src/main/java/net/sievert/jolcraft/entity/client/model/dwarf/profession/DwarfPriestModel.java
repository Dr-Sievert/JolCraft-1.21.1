package net.sievert.jolcraft.entity.client.model.dwarf.profession;

import net.minecraft.client.model.geom.ModelPart;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.entity.client.model.dwarf.DwarfModel;
import net.sievert.jolcraft.entity.client.util.dwarf.DwarfModelHelper;
import net.sievert.jolcraft.entity.client.util.dwarf.DwarfRenderState;

@OnlyIn(Dist.CLIENT)
public class DwarfPriestModel extends DwarfModel {

    protected static String getModelLayerName() {
        return "dwarf_priest";
    }

    public DwarfPriestModel(ModelPart root) {super(root);}

    @Override
    public void setupAnim(DwarfRenderState state) {
        super.setupAnim(state);
        DwarfModelHelper.visibleOuterLayer(this);
    }
}
