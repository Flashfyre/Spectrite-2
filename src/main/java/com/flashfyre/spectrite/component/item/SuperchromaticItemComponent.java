package com.flashfyre.spectrite.component.item;

import com.flashfyre.spectrite.component.SuperchromaticComponent;
import com.flashfyre.spectrite.util.SuperchromaticItemUtils;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.item.ItemComponent;
import net.minecraft.item.ItemStack;

public class SuperchromaticItemComponent extends ItemComponent implements SuperchromaticComponent
{
    private final boolean chargeable;

    public SuperchromaticItemComponent(ItemStack stack, ComponentKey<?> key, boolean chargeable)
    {
        super(stack, key);
        this.chargeable = chargeable;
    }

    @Override
    public boolean isSuperchromatic()
    {
        final int superchromaticPoints = getSuperchromaticPoints();
        if (superchromaticPoints > 0)
            return superchromaticPoints >= SuperchromaticItemUtils.getRequiredSuperchromaticPoints(stack);
        return false;
    }

    // This tag serves no function purpose except the for superchromized item advancement
    @Override
    public void setSuperchromatic(boolean superchromatic)
    {
        putBoolean("superchromatic", superchromatic);
    }

    public boolean isChargeable()
    {
        return chargeable;
    }

    public boolean isCharged()
    {
        return chargeable && hasTag("charged") && getBoolean("charged");
    }

    public void setCharged(boolean charged)
    {
        if (chargeable)
            putBoolean("charged", charged);
    }

    public int getSuperchromaticPoints()
    {
        return hasTag("superchromatic_points") ? getInt("superchromatic_points") : 0;
    }

    public void setSuperchromaticPoints(int superchromaticPoints)
    {
        putInt("superchromatic_points", superchromaticPoints);
    }
}