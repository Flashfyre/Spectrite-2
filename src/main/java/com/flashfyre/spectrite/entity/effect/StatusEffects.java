package com.flashfyre.spectrite.entity.effect;

import com.flashfyre.spectrite.Spectrite;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.util.registry.Registry;

public class StatusEffects
{
    public static final StatusEffect SPECTRITE = new SpectriteStatusEffect(StatusEffectCategory.NEUTRAL);

    public static void initStatusEffects()
    {
        Registry.register(Registry.STATUS_EFFECT, Spectrite.getId("spectrite"), SPECTRITE);
    }
}
