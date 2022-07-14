package com.flashfyre.spectrite.mixin;

import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.gen.chunk.NoiseChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(NoiseChunkGenerator.class)
public class NoiseChunkGeneratorMixin
{
    private static final long SUPERCHROMATIC_SEED = 1618033988749895l;

    private static long cachedSeed;

    @ModifyArgs(method = "<init>(Lnet/minecraft/util/registry/Registry;Lnet/minecraft/util/registry/Registry;Lnet/minecraft/world/biome/source/BiomeSource;JLnet/minecraft/util/registry/RegistryEntry;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/gen/chunk/NoiseChunkGenerator;<init>(Lnet/minecraft/util/registry/Registry;Lnet/minecraft/util/registry/Registry;Lnet/minecraft/world/biome/source/BiomeSource;Lnet/minecraft/world/biome/source/BiomeSource;JLnet/minecraft/util/registry/RegistryEntry;)V"))
    private static void modifyConstructorSeedForSuperchromatic(Args args)
    {
        final long seed = args.get(4);
        if (seed == SUPERCHROMATIC_SEED)
            args.set(4, cachedSeed);
        else if ("overworld".equals(((RegistryKey) ((RegistryEntry) args.get(5)).getKey().get()).getValue().getPath()))
            cachedSeed = seed;
    }
}
