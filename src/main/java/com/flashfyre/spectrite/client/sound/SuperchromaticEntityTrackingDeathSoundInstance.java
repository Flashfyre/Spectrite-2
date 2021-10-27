package com.flashfyre.spectrite.client.sound;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;

@Environment(EnvType.CLIENT)
public class SuperchromaticEntityTrackingDeathSoundInstance extends SuperchromaticEntityTrackingSoundInstance
{
    private int hueFrame;

    public SuperchromaticEntityTrackingDeathSoundInstance(SoundEvent sound, SoundCategory category, float volume, float pitch, Entity entity)
    {
        super(sound, category, volume, pitch, entity);
        this.hueFrame = 9 + entity.world.random.nextInt(10);
    }

    @Override
    public void tick()
    {
        if (this.hueFrame != 0)
        {
            if (this.playTime % 2 == 0)
                this.hueFrame--;
            this.pitch = 1 + ((this.hueFrame < 18 ? (this.hueFrame - 9F) : (9F - (this.hueFrame - 18))) * 0.065F);
        }
        this.playTime++;
    }
}
