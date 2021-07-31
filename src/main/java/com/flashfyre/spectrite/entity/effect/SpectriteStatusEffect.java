package com.flashfyre.spectrite.entity.effect;

import com.flashfyre.spectrite.util.SpectriteUtils;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectType;

public class SpectriteStatusEffect extends StatusEffect
{
    public SpectriteStatusEffect(StatusEffectType type)
    {
        super(type, SpectriteUtils.getCurrentSpectriteColor(0));
    }

    public int getColor()
    {
        return SpectriteUtils.getCurrentSpectriteColor(0);
    }
}
