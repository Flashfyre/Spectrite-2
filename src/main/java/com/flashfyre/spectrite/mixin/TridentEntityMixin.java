package com.flashfyre.spectrite.mixin;

import com.flashfyre.spectrite.entity.SpectriteCompatibleEntity;
import com.flashfyre.spectrite.entity.SpectriteCompatibleWeaponEntity;
import com.flashfyre.spectrite.item.SpectriteTridentItem;
import com.flashfyre.spectrite.util.SpectriteUtils;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TridentEntity.class)
public class TridentEntityMixin
{
    @Shadow
    private boolean dealtDamage;

    @Shadow
    private ItemStack tridentStack;

    @Inject(method = "<init>(Lnet/minecraft/world/World;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;)V",
            at = @At("TAIL"))
    private void spectrite$injectSetDamageTrackedData(World world, LivingEntity owner, ItemStack stack, CallbackInfo ci)
    {
        final TridentEntity tridentEntity = (TridentEntity) (Object) this;
        if (stack.getItem() instanceof SpectriteTridentItem)
        {
            ((SpectriteCompatibleEntity) tridentEntity).setSpectriteEntity(true);
            ((SpectriteCompatibleWeaponEntity) tridentEntity).setSpectriteDamage(
                    SpectriteUtils.getItemStackStDamage(stack));
            ((SpectriteCompatibleWeaponEntity) tridentEntity).setSpectriteCharged(
                    ((SpectriteTridentItem) stack.getItem()).isCharged(stack));
        }
    }

    @Inject(method = "onEntityHit", at = @At("HEAD"), cancellable = true)
    private void injectOnEntityHitSpectrite(EntityHitResult entityHitResult, CallbackInfo ci)
    {
        final TridentEntity tridentEntity = (TridentEntity) (Object) this;
        if (!((SpectriteCompatibleEntity) tridentEntity).isSpectriteEntity())
            return;

        ci.cancel();

        final Entity entity = entityHitResult.getEntity();
        final Entity entity2 = tridentEntity.getOwner();
        final LivingEntity livingEntity = entity instanceof LivingEntity ? (LivingEntity) entity : null;
        final LivingEntity livingEntity2 = entity2 instanceof LivingEntity ? (LivingEntity) entity2 : null;

        SoundEvent soundEvent = SoundEvents.ITEM_TRIDENT_HIT;

        float f = 8.0f;
        if (livingEntity != null)
            f += EnchantmentHelper.getAttackDamage(tridentStack, livingEntity.getGroup());

        final DamageSource damageSource = DamageSource.trident(tridentEntity, entity == null ? tridentEntity : entity);
        this.dealtDamage = true;

        final double targetX = entity.getX();
        final double targetY = entity.getBoundingBox().minY + entity.getHeight() / 2d;
        final double targetZ = entity.getZ();

        if (entity.damage(damageSource, f))
        {
            final boolean isEnderman = entity.getType() == EntityType.ENDERMAN;
            if (livingEntity != null && livingEntity2 != null)
            {
                final SpectriteTridentItem spectriteTridentItem = (SpectriteTridentItem) tridentStack.getItem();
                if ((!(livingEntity2 instanceof PlayerEntity player)
                        || player.getItemCooldownManager().getCooldownProgress(spectriteTridentItem, 0f) == 0f)
                        && spectriteTridentItem.isCharged(tridentStack)
                        && !spectriteTridentItem.isDepleted())
                {
                    int power = 4;
                    tridentStack.damage((int) Math.pow(power, 3f), livingEntity2, (e) ->
                    {
                    });

                    if (!tridentEntity.world.isClient)
                    {
                        final double tridentY = tridentEntity.getBoundingBox().minY + tridentEntity.getHeight() / 2d;
                        SpectriteUtils.newSpectriteExplosion(tridentEntity.world, tridentEntity, isEnderman ? null : livingEntity,
                                null, (tridentEntity.getX() + targetX) / 2d, (tridentY + targetY) / 2d,
                                (tridentEntity.getZ() + targetZ) / 2d,
                                power, false, Explosion.DestructionType.NONE);
                        if (livingEntity2 instanceof PlayerEntity playerEntity)
                            SpectriteUtils.tryActivateSpectriteChargeableItemCooldown(playerEntity, tridentStack);
                    }

                    spectriteTridentItem.setCharged(tridentStack, false);
                }
            }

            if (isEnderman)
                return;
        }

        if (livingEntity != null)
        {
            if (livingEntity2 != null)
            {
                EnchantmentHelper.onUserDamaged(livingEntity, livingEntity2);
                EnchantmentHelper.onTargetDamaged(livingEntity, livingEntity2);
            }

            ((PersistentProjectileEntityAccessor) tridentEntity).invokeOnHit(livingEntity);
        }

        tridentEntity.setVelocity(tridentEntity.getVelocity().multiply(-0.01D, -0.1D, -0.01D));
        float g = 1.0F;
        if (tridentEntity.world instanceof ServerWorld && tridentEntity.world.isThundering() && tridentEntity.hasChanneling())
        {
            final BlockPos blockPos = entity.getBlockPos();
            if (tridentEntity.world.isSkyVisible(blockPos))
            {
                final LightningEntity lightningEntity = EntityType.LIGHTNING_BOLT.create(tridentEntity.world);
                lightningEntity.refreshPositionAfterTeleport(Vec3d.ofBottomCenter(blockPos));
                lightningEntity.setChanneler(entity2 instanceof ServerPlayerEntity ? (ServerPlayerEntity) entity2 : null);
                tridentEntity.world.spawnEntity(lightningEntity);
                soundEvent = SoundEvents.ITEM_TRIDENT_THUNDER;
                g = 5.0F;
            }
        }

        tridentEntity.world.playSoundFromEntity(null, tridentEntity, soundEvent, tridentEntity.getSoundCategory(), g, 1.0f);
    }
}
