package com.flashfyre.spectrite.client.mixin;

import com.flashfyre.spectrite.item.SpectriteChargeableItem;
import com.flashfyre.spectrite.item.SpectriteItem;
import com.flashfyre.spectrite.text.SpectriteText;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Environment(EnvType.CLIENT)
@Mixin(InGameHud.class)
public class InGameHudMixin
{
    @Shadow
    private ItemStack currentStack;

    @ModifyVariable(method = "renderHeldItemTooltip", at = @At(value = "STORE"))
    private MutableText spectrite$modifyVariableRenderHeldItemTooltipMutableText(MutableText mutableText)
    {
        if (!this.currentStack.isEmpty() && this.currentStack.getItem() instanceof SpectriteItem spectriteItem)
        {
            final boolean charged = spectriteItem instanceof SpectriteChargeableItem spectriteChargeableItem
                    && spectriteChargeableItem.isCharged(this.currentStack);
            return new SpectriteText(mutableText, charged);
        }

        return mutableText;
    }
}
