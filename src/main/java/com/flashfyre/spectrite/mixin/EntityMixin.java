package com.flashfyre.spectrite.mixin;

import com.flashfyre.spectrite.entity.SpectriteCompatibleEntity;
import com.flashfyre.spectrite.entity.SpectriteCompatibleMobEntity;
import com.flashfyre.spectrite.text.SpectriteText;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
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
    public abstract SoundCategory getSoundCategory();

    @Inject(method = "getName", at = @At(value = "RETURN"), cancellable = true)
    private void spectrite$injectGetNameReturn(CallbackInfoReturnable<Text> cir)
    {
        final Entity entity = (Entity) ((Object) this);
        if (entity instanceof SpectriteCompatibleEntity spectriteCompatibleEntity && spectriteCompatibleEntity.isSpectriteEntity()
                && !entity.hasCustomName())
            cir.setReturnValue(new TranslatableText("entity.spectrite.generic", cir.getReturnValue()));
    }

    @Inject(method = "getDisplayName", at = @At(value = "RETURN"), cancellable = true)
    private void spectrite$injectGetDisplayNameReturn(CallbackInfoReturnable<Text> cir)
    {
        final Entity entity = (Entity) ((Object) this);
        if (entity instanceof SpectriteCompatibleEntity spectriteCompatibleEntity && spectriteCompatibleEntity.isSpectriteEntity())
        {
            if (entity.getScoreboardTeam() == null || entity.getScoreboardTeam().getColor() == Formatting.RESET)
                cir.setReturnValue(new SpectriteText(cir.getReturnValue(), false));
        }
    }

    @Inject(method = "playSound", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/World;playSound(Lnet/minecraft/entity/player/PlayerEntity;DDDLnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FF)V"),
            cancellable = true)

    private void spectrite$injectEntityPlaySound(SoundEvent sound, float volume, float pitch, CallbackInfo ci)
    {
        final Entity entity = (Entity) ((Object) this);
        if (entity instanceof SpectriteCompatibleEntity spectriteCompatibleEntity && spectriteCompatibleEntity.isSpectriteEntity())
        {
            this.world.playSoundFromEntity(null, entity, sound, this.getSoundCategory(), volume, pitch);
            ci.cancel();
        }
    }

    @Inject(method = "setCustomName", at = @At("TAIL"))
    private void injectSetCustomNameSetSpectriteBossBarName(Text name, CallbackInfo ci)
    {
        final Entity entity = (Entity) (Object) this;
        if (entity instanceof SpectriteCompatibleMobEntity && entity.hasCustomName() && ((SpectriteCompatibleMobEntity) entity).isSpectriteEntity())
            ((SpectriteCompatibleMobEntity) entity).getSpectriteBossBar().setName(entity.getDisplayName());
    }

    @Inject(method = "onStoppedTrackingBy", at = @At("TAIL"))
    private void injectOnStoppedTrackingSpectriteBossBarRemovePlayer(ServerPlayerEntity player, CallbackInfo ci)
    {
        final Entity entity = (Entity) ((Object) this);
        if (entity instanceof SpectriteCompatibleMobEntity && ((SpectriteCompatibleMobEntity) entity).getSpectriteBossBar() != null)
            ((SpectriteCompatibleMobEntity) entity).getSpectriteBossBar().removePlayer(player);
    }
}
