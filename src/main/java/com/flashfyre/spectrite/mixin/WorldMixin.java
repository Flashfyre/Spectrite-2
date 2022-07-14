package com.flashfyre.spectrite.mixin;

import com.flashfyre.spectrite.component.chunk.SuperchromaticChunkComponent;
import com.flashfyre.spectrite.component.world.SuperchromaticCompatibleWorldComponent;
import com.flashfyre.spectrite.world.Dimensions;
import com.flashfyre.spectrite.world.SuperchromaticCompatibleWorld;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(World.class)
public abstract class WorldMixin implements SuperchromaticCompatibleWorld
{
    private boolean clearSuperchromaticFlag;

    private boolean ignoreSyncStateFlag;

    @Shadow
    public abstract RegistryKey<World> getRegistryKey();

    @Override
    public boolean isClearSuperchromaticFlag()
    {
        return clearSuperchromaticFlag;
    }

    @Override
    public void setClearSuperchromaticFlag(boolean clearSuperchromaticFlag)
    {
        this.clearSuperchromaticFlag = clearSuperchromaticFlag;
    }

    @Override
    public void setIgnoreSyncStateFlag(boolean ignoreSyncStateFlag)
    {
        this.ignoreSyncStateFlag = ignoreSyncStateFlag;
    }

    @Inject(method = "setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;II)Z",
            at = @At("RETURN"))
    public void spectrite$injectSetBlockState(BlockPos pos, BlockState state, int flags, int maxUpdateDepth,
                                              CallbackInfoReturnable<Boolean> cir)
    {
        if (!isClient() && cir.getReturnValue())
        {
            if (!ignoreSyncStateFlag)
            {
                final boolean isOverworld = this.getRegistryKey() == World.OVERWORLD;
                if (isOverworld || this.getRegistryKey() == Dimensions.SUPERCHROMATIC)
                {
                    final ServerWorld otherWorld = ((ServerWorld) (Object) this).getServer().getWorld(isOverworld
                            ? Dimensions.SUPERCHROMATIC
                            : World.OVERWORLD);
                    final ChunkPos chunkPos = new ChunkPos(pos);
                    // todo: Update to handle chunk loading effiently
                    if (otherWorld.isChunkLoaded(chunkPos.toLong()) || true)
                    {
                        ((SuperchromaticCompatibleWorld) otherWorld).setIgnoreSyncStateFlag(true);
                        if (state.isAir())
                            otherWorld.breakBlock(pos, false);
                        else
                            otherWorld.setBlockState(pos, state);
                        ((SuperchromaticCompatibleWorld) otherWorld).setIgnoreSyncStateFlag(false);
                    }
                } else
                    SuperchromaticCompatibleWorldComponent.KEY.get(this).getSyncChunks().add(new ChunkPos(pos).getStartPos());
            }
            if (clearSuperchromaticFlag && !state.isAir())
            {
                final SuperchromaticChunkComponent superchromaticChunkComponent = SuperchromaticChunkComponent.KEY.get(getChunk(pos));
                superchromaticChunkComponent.getSuperchromaticBlocks().remove(pos);
            }
        }
    }
}
