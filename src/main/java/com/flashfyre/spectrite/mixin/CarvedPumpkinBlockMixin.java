package com.flashfyre.spectrite.mixin;

import com.flashfyre.spectrite.block.Blocks;
import com.flashfyre.spectrite.util.SpectriteEntityUtils;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CarvedPumpkinBlock;
import net.minecraft.block.Material;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.block.pattern.BlockPatternBuilder;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.predicate.block.BlockStatePredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.function.MaterialPredicate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Predicate;

@Mixin(CarvedPumpkinBlock.class)
public class CarvedPumpkinBlockMixin
{
    @Shadow
    @Final
    private static Predicate<BlockState> IS_GOLEM_HEAD_PREDICATE;

    @Nullable
    private BlockPattern superchromaticGolemDispenserPattern;

    @Nullable
    private BlockPattern superchromaticGolemPattern;

    @Inject(method = "canDispense", at = @At("RETURN"))
    private void injectCanDispenseCheckSuperchromaticGolem(WorldView world, BlockPos pos, CallbackInfoReturnable<Boolean> cir)
    {
        if (!cir.getReturnValue())
            cir.setReturnValue(this.getSuperchromaticGolemDispenserPattern().searchAround(world, pos) != null);
    }

    @Inject(method = "trySpawnEntity", at = @At("RETURN"))
    private void injectTrySpawnEntityCheckSuperchromaticGolem(World world, BlockPos pos, CallbackInfo ci)
    {
        final BlockPattern.Result result = this.getSuperchromaticGolemPattern().searchAround(world, pos);
        if (result == null)
            return;
        for (int i = 0; i < this.getSuperchromaticGolemPattern().getWidth(); ++i)
        {
            for (int cachedBlockPosition = 0; cachedBlockPosition < this.getSuperchromaticGolemPattern().getHeight(); ++cachedBlockPosition)
            {
                final CachedBlockPosition j = result.translate(i, cachedBlockPosition, 0);
                world.setBlockState(j.getBlockPos(), net.minecraft.block.Blocks.AIR.getDefaultState(), Block.NOTIFY_LISTENERS);
                world.syncWorldEvent(WorldEvents.BLOCK_BROKEN, j.getBlockPos(), Block.getRawIdFromState(j.getBlockState()));
            }
        }
        final BlockPos i = result.translate(1, 2, 0).getBlockPos();
        final IronGolemEntity golemEntity = EntityType.IRON_GOLEM.create(world);
        golemEntity.setPlayerCreated(true);
        golemEntity.refreshPositionAndAngles((double) i.getX() + 0.5, (double) i.getY() + 0.05, (double) i.getZ() + 0.5, 0.0f, 0.0f);
        SpectriteEntityUtils.trySetMobSuperchromatic(golemEntity);
        world.spawnEntity(golemEntity);
        for (ServerPlayerEntity serverPlayerEntity : world.getNonSpectatingEntities(ServerPlayerEntity.class, golemEntity.getBoundingBox().expand(5.0)))
        {
            Criteria.SUMMONED_ENTITY.trigger(serverPlayerEntity, golemEntity);
        }
        for (int j = 0; j < this.getSuperchromaticGolemPattern().getWidth(); ++j)
        {
            int var7_20 = 0;
            while (var7_20 < this.getSuperchromaticGolemPattern().getHeight())
            {
                final CachedBlockPosition cachedBlockPosition = result.translate(j, var7_20, 0);
                world.updateNeighbors(cachedBlockPosition.getBlockPos(), net.minecraft.block.Blocks.AIR);
                ++var7_20;
            }
        }
    }

    private BlockPattern getSuperchromaticGolemDispenserPattern()
    {
        if (this.superchromaticGolemDispenserPattern == null)
        {
            this.superchromaticGolemDispenserPattern = BlockPatternBuilder.start().aisle("~ ~", "###", "~#~")
                    .where('#', CachedBlockPosition.matchesBlockState(BlockStatePredicate.forBlock(Blocks.SPECTRITE_BLOCK)))
                    .where('~', CachedBlockPosition.matchesBlockState(MaterialPredicate.create(Material.AIR))).build();
        }
        return this.superchromaticGolemDispenserPattern;
    }

    private BlockPattern getSuperchromaticGolemPattern()
    {
        if (this.superchromaticGolemPattern == null)
        {
            this.superchromaticGolemPattern = BlockPatternBuilder.start().aisle("~^~", "###", "~#~")
                    .where('^', CachedBlockPosition.matchesBlockState(IS_GOLEM_HEAD_PREDICATE))
                    .where('#', CachedBlockPosition.matchesBlockState(BlockStatePredicate.forBlock(Blocks.SPECTRITE_BLOCK)))
                    .where('~', CachedBlockPosition.matchesBlockState(MaterialPredicate.create(Material.AIR))).build();
        }
        return this.superchromaticGolemPattern;
    }
}
