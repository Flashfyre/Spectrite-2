package com.flashfyre.spectrite.mixin;

import com.flashfyre.spectrite.SpectriteConfig;
import com.flashfyre.spectrite.world.SuperchromaticCompatibleChunkRegion;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.random.CheckedRandom;
import net.minecraft.util.math.random.ChunkRandom;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(StructureStart.class)
public class StructureStartMixin
{
    private final ChunkRandom spectrite$random = new ChunkRandom(new CheckedRandom(0L));

    @Shadow
    @Final
    private ChunkPos pos;

    @Inject(method = "place", at = @At(value = "HEAD"))
    public void spectrite$injectPlace(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator,
                                      Random random, BlockBox chunkBox, ChunkPos chunkPos, CallbackInfo ci)
    {
        final float superchromaticStructureRate = SpectriteConfig.getSuperchromaticStructureRate();
        if (superchromaticStructureRate > 0f)
        {
            if (superchromaticStructureRate == 100f)
                ((SuperchromaticCompatibleChunkRegion) world).setSuperchromaticFlag(true);
            else
            {
                spectrite$random.setPopulationSeed(world.getSeed(), this.pos.getStartX(), this.pos.getStartZ());
                if (spectrite$random.nextFloat() * 100f < superchromaticStructureRate)
                    ((SuperchromaticCompatibleChunkRegion) world).setSuperchromaticFlag(true);
            }
        }
    }

    @Inject(method = "place", at = @At(value = "TAIL"))
    public void spectrite$injectPlaceReturn(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator,
                                            Random random, BlockBox chunkBox, ChunkPos chunkPos, CallbackInfo ci)
    {
        if (((SuperchromaticCompatibleChunkRegion) world).isSuperchromaticFlag())
            ((SuperchromaticCompatibleChunkRegion) world).setSuperchromaticFlag(false);
    }
}
