package com.flashfyre.spectrite.item;

import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.EntityBucketItem;
import net.minecraft.sound.SoundEvents;

public class SuperchromaticAxolotlBucketItem extends EntityBucketItem implements SpectriteItem
{
    public SuperchromaticAxolotlBucketItem(Fluid fluid, Settings settings)
    {
        super(EntityType.AXOLOTL, fluid, SoundEvents.ITEM_BUCKET_EMPTY_AXOLOTL, settings);
    }
}
