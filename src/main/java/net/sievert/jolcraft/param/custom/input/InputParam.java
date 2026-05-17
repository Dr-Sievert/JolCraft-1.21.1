package net.sievert.jolcraft.param.custom.input;

import net.sievert.jolcraft.param.base.ParamContextMatching;
import net.sievert.jolcraft.param.base.ParamIdentity;

public interface InputParam<T> extends ParamContextMatching<T>, ParamIdentity {

    @Override
    String key();
}