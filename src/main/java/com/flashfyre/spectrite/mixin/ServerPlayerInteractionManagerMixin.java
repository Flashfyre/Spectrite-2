package com.flashfyre.spectrite.mixin;

import com.flashfyre.spectrite.entity.player.SuperchromaticCooldownPlayerEntity;
import com.flashfyre.spectrite.util.SuperchromaticItemUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerInteractionManager.class)
public class ServerPlayerInteractionManagerMixin
{
    @Inject(method = "interactItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getCount()I"), cancellable = true)
    private void injectInteractItemCheckSuperchromaticItemCooldown(ServerPlayerEntity player, World world, ItemStack stack, Hand hand, CallbackInfoReturnable<ActionResult> cir)
    {
        if (((SuperchromaticCooldownPlayerEntity) player).getSuperchromaticItemCooldownManager().isCoolingDown()
                && SuperchromaticItemUtils.isSuperchromatic(stack))
            cir.setReturnValue(ActionResult.PASS);
    }
}
