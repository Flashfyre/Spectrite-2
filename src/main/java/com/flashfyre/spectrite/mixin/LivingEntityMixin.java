package com.flashfyre.spectrite.mixin;

import com.flashfyre.spectrite.entity.SpectriteCompatibleMobEntity;
import com.flashfyre.spectrite.entity.effect.StatusEffects;
import com.flashfyre.spectrite.item.SimpleSpectriteItem;
import com.flashfyre.spectrite.particle.Particles;
import com.flashfyre.spectrite.util.SpectriteEntityUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin
{
    @Inject(method = "damage", at = @At("RETURN"))
    private void injectDamageSpectriteBossBarAddPlayer(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir)
    {
        if (cir.getReturnValue())
        {
            final LivingEntity entity = (LivingEntity) ((Object) this);
            if (entity instanceof SpectriteCompatibleMobEntity && ((SpectriteCompatibleMobEntity) entity).getSuperchromaticBossBar() != null
                    && source.getSource() instanceof ServerPlayerEntity player)
                ((SpectriteCompatibleMobEntity) entity).getSuperchromaticBossBar().addPlayer(player);
        }
    }

    @Inject(method = "tickStatusEffects", at = @At("TAIL"))
    private void injectTickStatusEffectsSuperchromaticGlowParticle(CallbackInfo ci)
    {
        final LivingEntity livingEntity = (LivingEntity) (Object) this;

        if (livingEntity.hasStatusEffect(StatusEffects.SUPERCHROMATIC) && livingEntity.world.getTime() % 7 == 0)
            livingEntity.world.addParticle(Particles.SUPERCHROMATIC_GLOW,
                    livingEntity.getParticleX(0.6D), livingEntity.getRandomBodyY(), livingEntity.getParticleZ(0.6D),
                    0.0D, 0.0D, 0.0D);
    }

    @Inject(method = "damage", at = @At("RETURN"))
    private void injectDamageChromaBlast(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir)
    {
        final LivingEntity livingEntity = (LivingEntity) (Object) this;
        if (!livingEntity.world.isClient && source.getAttacker() instanceof LivingEntity attacker && !source.isProjectile())
            SpectriteEntityUtils.tryAddChromaBlast(attacker, livingEntity);
    }

    @Inject(method = "eatFood", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;decrement(I)V"), cancellable = true)
    private void injectEatFoodSuperchromaticFood(World world, ItemStack stack, CallbackInfoReturnable<ItemStack> cir)
    {
        final PlayerEntity playerEntity = (PlayerEntity) (Object) this;
        if (stack.getItem() instanceof SimpleSpectriteItem)
        {
            playerEntity.getItemCooldownManager().set(stack.getItem(), 1500);
            playerEntity.emitGameEvent(GameEvent.EAT);
            cir.setReturnValue(stack);
        }
    }
}
