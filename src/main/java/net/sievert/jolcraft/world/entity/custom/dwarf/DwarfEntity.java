package net.sievert.jolcraft.world.entity.custom.dwarf;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.sievert.jolcraft.world.entity.custom.dwarf.base.AbstractDwarfEntity;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class DwarfEntity extends AbstractDwarfEntity {

    public DwarfEntity(EntityType<? extends AbstractDwarfEntity> entityType, Level level) {
        super(entityType, level);
    }
}
