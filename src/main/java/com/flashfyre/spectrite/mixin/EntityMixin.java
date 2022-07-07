package com.flashfyre.spectrite.mixin;

import com.flashfyre.spectrite.entity.SpectriteCompatibleEntity;
import com.flashfyre.spectrite.entity.SpectriteCompatibleMobEntity;
import com.flashfyre.spectrite.entity.SuperchromaticEntity;
import com.flashfyre.spectrite.util.SuperchromaticEntityUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin
{
    @Shadow
    public World world;

    @Shadow
    protected boolean firstUpdate;

    @Shadow
    public abstract SoundCategory getSoundCategory();

    @Inject(method = "refreshPositionAndAngles(DDDFF)V", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/entity/Entity;setPos(DDD)V", shift = At.Shift.AFTER))
    private void spectrite$injectRefreshPositionAndAnglesAfterSetPos(double x, double y, double z, float yaw, float pitch, CallbackInfo ci)
    {
        final Entity entity = (Entity) (Object) this;
        if (this.firstUpdate && entity instanceof SpectriteCompatibleMobEntity spectriteCompatibleMobEntity)
        {
            if (entity instanceof EnderDragonEntity)
                return;
            if (SuperchromaticEntityUtils.trySetMobSuperchromatic((MobEntity) entity) && entity instanceof Monster)
                spectriteCompatibleMobEntity.setSuperchromaticBossBar(new ServerBossBar(entity.getDisplayName(),
                        BossBar.Color.PURPLE, BossBar.Style.PROGRESS));
        }
    }

    @Inject(method = "getName", at = @At(value = "RETURN"), cancellable = true)
    private void spectrite$injectGetNameReturn(CallbackInfoReturnable<Text> cir)
    {
        final Entity entity = (Entity) ((Object) this);
        if (entity instanceof SpectriteCompatibleEntity spectriteCompatibleEntity && spectriteCompatibleEntity.isSuperchromatic()
                && !entity.hasCustomName() && !(entity instanceof SuperchromaticEntity))
            cir.setReturnValue(Text.translatable("entity.spectrite.superchromatic.generic", cir.getReturnValue()));
    }

    @Inject(method = "getDisplayName", at = @At(value = "RETURN"), cancellable = true)
    private void spectrite$injectGetDisplayNameReturn(CallbackInfoReturnable<Text> cir)
    {
        final Entity entity = (Entity) ((Object) this);
        if (entity instanceof SpectriteCompatibleEntity spectriteCompatibleEntity && spectriteCompatibleEntity.isSuperchromatic())
        {
            if (entity.getScoreboardTeam() == null || entity.getScoreboardTeam().getColor() == Formatting.RESET)
                //cir.setReturnValue(new SpectriteText(cir.getReturnValue(), false));
                cir.setReturnValue(cir.getReturnValue());
        }
    }

    @Inject(method = "playSound", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/World;playSound(Lnet/minecraft/entity/player/PlayerEntity;DDDLnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FF)V"),
            cancellable = true)

    private void spectrite$injectEntityPlaySound(SoundEvent sound, float volume, float pitch, CallbackInfo ci)
    {
        final Entity entity = (Entity) ((Object) this);
        if (entity instanceof SpectriteCompatibleEntity spectriteCompatibleEntity && spectriteCompatibleEntity.isSuperchromatic())
        {
            this.world.playSoundFromEntity(null, entity, sound, this.getSoundCategory(), volume, pitch);
            ci.cancel();
        }
    }

    @Inject(method = "readNbt", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;shouldSetPositionOnLoad()Z"))
    private void injectReadNbtShouldSetPositionOnLoadSetBossBar(NbtCompound nbt, CallbackInfo ci)
    {
        final Entity entity = (Entity) (Object) this;
        if (entity instanceof SpectriteCompatibleMobEntity spectriteCompatibleMobEntity
                && spectriteCompatibleMobEntity.isSuperchromatic() && entity instanceof Monster)
        {
            if (entity instanceof EnderDragonEntity)
                return;
            spectriteCompatibleMobEntity.setSuperchromaticBossBar(new ServerBossBar(entity.getDisplayName(),
                    BossBar.Color.PURPLE, BossBar.Style.PROGRESS));
        }
    }

    @Inject(method = "setCustomName", at = @At("TAIL"))
    private void injectSetCustomNameSetSuperchromaticBossBarName(Text name, CallbackInfo ci)
    {
        final Entity entity = (Entity) (Object) this;
        if (entity instanceof SpectriteCompatibleMobEntity && entity.hasCustomName() && ((SpectriteCompatibleMobEntity) entity).isSuperchromatic())
            ((SpectriteCompatibleMobEntity) entity).getSuperchromaticBossBar().setName(entity.getDisplayName());
    }

    @Inject(method = "onStoppedTrackingBy", at = @At("TAIL"))
    private void injectOnStoppedTrackingSuperchromaticBossBarRemovePlayer(ServerPlayerEntity player, CallbackInfo ci)
    {
        final Entity entity = (Entity) (Object) this;
        if (entity instanceof SpectriteCompatibleMobEntity && ((SpectriteCompatibleMobEntity) entity).getSuperchromaticBossBar() != null)
            ((SpectriteCompatibleMobEntity) entity).getSuperchromaticBossBar().removePlayer(player);
    }
}
