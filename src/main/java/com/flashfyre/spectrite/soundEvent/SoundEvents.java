package com.flashfyre.spectrite.soundEvent;

import com.flashfyre.spectrite.Spectrite;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class SoundEvents
{
    public static Identifier CHARGE_ID = getSoundEventId("charge");
    public static SoundEvent CHARGE;

    public static Identifier UNCHARGE_ID = getSoundEventId("uncharge");
    public static SoundEvent UNCHARGE;

    public static Identifier EXPLOSION_ID = getSoundEventId("explosion");
    public static SoundEvent EXPLOSION;

    public static Identifier FATALITY_ID = getSoundEventId("fatality");
    public static SoundEvent FATALITY;

    public static Identifier PRE_EXPLOSION_ID = getSoundEventId("preexplosion");
    public static SoundEvent PRE_EXPLOSION;

    public static void initSoundEvents()
    {
        CHARGE = registerSoundEvent(CHARGE_ID);
        UNCHARGE = registerSoundEvent(UNCHARGE_ID);
        EXPLOSION = registerSoundEvent(EXPLOSION_ID);
        FATALITY = registerSoundEvent(FATALITY_ID);
        PRE_EXPLOSION = registerSoundEvent(PRE_EXPLOSION_ID);
    }

    private static Identifier getSoundEventId(String name)
    {
        return Spectrite.getId(name);
    }

    private static SoundEvent registerSoundEvent(Identifier id)
    {
        SoundEvent ret = new SoundEvent(id);
        Registry.register(Registry.SOUND_EVENT, id, ret);
        return ret;
    }
}
