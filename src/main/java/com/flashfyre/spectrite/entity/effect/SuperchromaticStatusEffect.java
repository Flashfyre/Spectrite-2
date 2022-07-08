package com.flashfyre.spectrite.entity.effect;

import com.flashfyre.spectrite.util.SpectriteUtils;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class SuperchromaticStatusEffect extends StatusEffect
{
    private final boolean invert;

    public SuperchromaticStatusEffect(StatusEffectCategory category, boolean invert)
    {
        super(category, SpectriteUtils.getCurrentHueColor(invert));
        this.invert = invert;
    }

    public int getColor()
    {
        return SpectriteUtils.getCurrentHueColor(invert);
    }
}
