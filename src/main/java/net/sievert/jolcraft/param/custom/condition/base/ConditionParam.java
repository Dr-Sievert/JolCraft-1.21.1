package net.sievert.jolcraft.param.custom.condition.base;

import net.sievert.jolcraft.param.base.ParamIdentity;
import net.sievert.jolcraft.param.base.ParamMatching;
import net.sievert.jolcraft.param.runtime.WorldContext;

public interface ConditionParam extends ParamMatching<WorldContext>, ParamIdentity {

    @Override
    String key();
}