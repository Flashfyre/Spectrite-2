package com.flashfyre.spectrite.entity.effect;

import com.flashfyre.spectrite.util.SpectriteUtils;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class SuperchromaticStatusEffect extends StatusEffect
{
    public SuperchromaticStatusEffect(StatusEffectCategory category)
    {
        super(category, SpectriteUtils.getCurrentHueColor(0));
    }

    public int getColor()
    {
        return SpectriteUtils.getCurrentHueColor(0);
    }
}
