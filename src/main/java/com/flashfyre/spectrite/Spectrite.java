package com.flashfyre.spectrite;

import com.flashfyre.spectrite.block.Blocks;
import com.flashfyre.spectrite.damageSource.DamageSources;
import com.flashfyre.spectrite.entity.effect.StatusEffects;
import com.flashfyre.spectrite.item.Items;
import com.flashfyre.spectrite.loot.LootTables;
import com.flashfyre.spectrite.loot.condition.LootConditionTypes;
import com.flashfyre.spectrite.particle.Particles;
import com.flashfyre.spectrite.soundEvent.SoundEvents;
import com.flashfyre.spectrite.world.ConfiguredFeatures;
import com.flashfyre.spectrite.world.PlacedFeatures;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Spectrite implements ModInitializer
{
    public static final String MODID = "spectrite";

    public static Spectrite INSTANCE;

    public final Logger logger = LogManager.getLogger(MODID);

    @Override
    public void onInitialize()
    {
        INSTANCE = this;

        AutoConfig.register(SpectriteConfig.class, JanksonConfigSerializer::new);

        SpectriteConfig.INSTANCE = AutoConfig.getConfigHolder(SpectriteConfig.class).getConfig();

        StatusEffects.initStatusEffects();
        Blocks.initBlocks();
        Items.initItems();
        ConfiguredFeatures.initConfiguredFeatures();
        PlacedFeatures.initPlacedFeatures();
        LootConditionTypes.initLootConditionTypes();
        LootTables.initLootTables();
        DamageSources.initDamageSources();
        SoundEvents.initSoundEvents();
        Particles.initParticles();
    }

    public static Identifier getId(String name)
    {
        return new Identifier(MODID, name);
    }

    public void log(String info)
    {
        this.logger.info("[Spectrite 2] " + info);
    }

    public void warn(String info)
    {
        logger.warn("[Spectrite 2] " + info);
    }

    public PlayerEntity getPlayer()
    {
        return null;
    }

    public void spawnChromaBlastEmitterParticle(World world, double posX, double posY, double posZ, double power)
    {
    }

    public void spawnChromaBlastParticle(double posX, double posY, double posZ, double xSpeed, double ySpeed, double zSpeed)
    {
    }
}
