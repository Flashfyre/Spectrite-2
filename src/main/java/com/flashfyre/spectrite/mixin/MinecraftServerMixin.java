package com.flashfyre.spectrite.mixin;

import com.flashfyre.spectrite.SpectriteConfig;
import com.flashfyre.spectrite.world.Dimensions;
import com.google.common.collect.ImmutableList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.border.WorldBorderListener;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.level.ServerWorldProperties;
import net.minecraft.world.level.UnmodifiableLevelProperties;
import net.minecraft.world.level.storage.LevelStorage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executor;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin
{
    @Shadow
    @Final
    private Executor workerExecutor;

    @Shadow
    @Final
    protected LevelStorage.Session session;

    @Shadow
    @Final
    private Map<RegistryKey<World>, ServerWorld> worlds;

    @Inject(method = "createWorlds", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/border/WorldBorder;load(Lnet/minecraft/world/border/WorldBorder$Properties;)V"),
            locals = LocalCapture.CAPTURE_FAILHARD)
    private void injectCreateWorldsAddSuperchromaticDimension(WorldGenerationProgressListener worldGenerationProgressListener, CallbackInfo ci,
                                                              ServerWorldProperties serverWorldProperties, GeneratorOptions generatorOptions,
                                                              boolean bl, long l, long m, List list, Registry registry, ChunkGenerator chunkGenerator,
                                                              DimensionOptions dimensionOptions, RegistryEntry registryEntry,
                                                              ServerWorld serverWorld, WorldBorder worldBorder)
    {
        if (!SpectriteConfig.isSuperchromaticDimension())
            return;
        
        final MinecraftServer minecraftServer = ((MinecraftServer) (Object) this);
        long superchromaticSeed;
        if (SpectriteConfig.isSuperchromaticDimensionSharesWorldSeed())
            superchromaticSeed = l;
        else
        {
            final Random random = new Random();
            random.setSeed(l);
            superchromaticSeed = random.nextLong();
        }
        final ChunkGenerator superchromaticChunkGenerator = GeneratorOptions.createOverworldGenerator(minecraftServer.getRegistryManager(), superchromaticSeed);
        final UnmodifiableLevelProperties unmodifiableLevelProperties = new UnmodifiableLevelProperties(minecraftServer.getSaveProperties(), serverWorldProperties);
        final ServerWorld superchromaticWorld = new ServerWorld(
                minecraftServer,
                this.workerExecutor,
                this.session,
                unmodifiableLevelProperties,
                Dimensions.SUPERCHROMATIC,
                minecraftServer.getRegistryManager().get(Registry.DIMENSION_TYPE_KEY).getOrCreateEntry(Dimensions.SUPERCHROMATIC_DIMENSION_TYPE),
                worldGenerationProgressListener,
                superchromaticChunkGenerator,
                bl,
                m,
                ImmutableList.of(),
                false
        );
        worldBorder.addListener(new WorldBorderListener.WorldBorderSyncer(superchromaticWorld.getWorldBorder()));
        this.worlds.put(Dimensions.SUPERCHROMATIC, superchromaticWorld);
    }
}
