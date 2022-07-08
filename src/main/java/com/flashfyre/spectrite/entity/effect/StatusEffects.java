package com.flashfyre.spectrite.entity.effect;

import com.flashfyre.spectrite.Spectrite;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.util.registry.Registry;

public class StatusEffects
{
    public static final StatusEffect SUPERCHROMATIC = new SuperchromaticStatusEffect(StatusEffectCategory.BENEFICIAL, false);
    public static final StatusEffect CHROMA_GUARD = new SuperchromaticStatusEffect(StatusEffectCategory.BENEFICIAL, true);

    public static void initStatusEffects()
    {
        Registry.register(Registry.STATUS_EFFECT, Spectrite.getId("superchromatic"), SUPERCHROMATIC);
        Registry.register(Registry.STATUS_EFFECT, Spectrite.getId("chroma_guard"), CHROMA_GUARD);
    }
}
