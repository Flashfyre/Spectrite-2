package com.flashfyre.spectrite.entity.player;

import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.item.Item;
import net.minecraft.util.math.MathHelper;

public class SuperchromaticItemCooldownManager extends ItemCooldownManager
{
    private int startTick;
    private int endTick;
    private int tick;
    private boolean active;

    public boolean isCoolingDown()
    {
        return isCoolingDown(null);
    }

    @Override
    public float getCooldownProgress(Item item, float partialTicks)
    {
        if (active)
        {
            float f = endTick - startTick;
            float g = (float) endTick - ((float) this.tick + partialTicks);
            return MathHelper.clamp(g / f, 0.0f, 1.0f);
        }
        return 0.0f;
    }

    public float getCooldownProgress(float partialTicks)
    {
        return getCooldownProgress(null, partialTicks);
    }

    @Override
    public void update()
    {
        ++tick;
        if (active)
        {
            if (endTick > tick)
                return;
            active = false;
            this.onCooldownUpdate(null);
        }
    }

    @Override
    public void set(Item item, int duration)
    {
        active = true;
        startTick = this.tick;
        endTick = tick + duration;
        this.onCooldownUpdate(null, duration);
    }

    public void set(int duration)
    {
        set(null, duration);
    }

    @Override
    public void remove(Item item)
    {
        active = false;
        this.onCooldownUpdate(item);
    }

    public void remove()
    {
        remove(null);
    }
}
