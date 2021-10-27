package com.flashfyre.spectrite.client.sound;

import com.flashfyre.spectrite.util.SpectriteUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.sound.EntityTrackingSoundInstance;
import net.minecraft.entity.Entity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;

@Environment(EnvType.CLIENT)
public class SuperchromaticEntityTrackingSoundInstance extends EntityTrackingSoundInstance
{
    protected Entity entity;
    protected int playTime = 0;

    public SuperchromaticEntityTrackingSoundInstance(SoundEvent sound, SoundCategory category, float volume, float pitch, Entity entity)
    {
        super(sound, category, volume, pitch, entity);
        this.entity = entity;
    }

    @Override
    public void tick()
    {
        int spectriteFrame = SpectriteUtils.getCurrentHueFrame(entity.world);
        this.pitch = 1 + ((spectriteFrame < 18 ? (spectriteFrame - 9F) : (9F - (spectriteFrame - 18))) * 0.065F);
        this.playTime++;
    }

    @Override
    public boolean isDone()
    {
        return (!this.isRepeatable() && playTime >= 330) || (this.isRepeatable() && playTime >= 660);
    }
}
