package com.flashfyre.spectrite.mixin;

import com.flashfyre.spectrite.component.chunk.SuperchromaticChunkComponent;
import com.flashfyre.spectrite.util.SuperchromaticEntityUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.CaveSpiderEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.MobSpawnerLogic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Optional;

@Mixin(MobSpawnerLogic.class)
public class MobSpawnerLogicMixin
{
    @Inject(method = "serverTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;refreshPositionAndAngles(DDDFF)V"),
            locals = LocalCapture.CAPTURE_FAILHARD)
    public void injectServerTickPlaySpawnEffectsSetEntitySuperchromatic(ServerWorld world, BlockPos pos, CallbackInfo ci,
                                                                        boolean bl, int i, NbtCompound nbtCompound,
                                                                        Optional optional, NbtList nbtList,
                                                                        int j, double d, double e, double f,
                                                                        Entity entity)
    {
        if (SuperchromaticChunkComponent.KEY.get(world.getChunk(pos)).getSuperchromaticBlocks().contains(pos))
        {
            SuperchromaticEntityUtils.setSuperchromatic(entity, true);
            if (entity instanceof CaveSpiderEntity)
                SuperchromaticEntityUtils.initSuperchromaticMobAttributes((MobEntity) entity);
        }
    }
}
