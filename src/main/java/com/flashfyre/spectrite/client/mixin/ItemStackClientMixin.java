package com.flashfyre.spectrite.client.mixin;

import com.flashfyre.spectrite.item.SpectriteChargeableItem;
import com.flashfyre.spectrite.item.SpectriteItem;
import com.flashfyre.spectrite.item.SpectriteToolItem;
import com.flashfyre.spectrite.item.SpectriteWeaponItem;
import com.flashfyre.spectrite.text.SpectriteText;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Environment(EnvType.CLIENT)
@Mixin(ItemStack.class)
public class ItemStackClientMixin
{
    @ModifyVariable(method = "getTooltip", at = @At(value = "STORE"))
    private MutableText spectrite$modifyVariableGetTooltipMutableText(MutableText mutableText)
    {
        final ItemStack stack = (ItemStack) (Object) this;
        if (stack.getItem() instanceof SpectriteItem spectriteItem)
        {
            final boolean charged = spectriteItem instanceof SpectriteChargeableItem spectriteChargeableItem
                    && spectriteChargeableItem.isCharged(stack);
            return new SpectriteText(mutableText, charged);
        }

        return mutableText;
    }

    @Inject(method = "getTooltip", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/item/ItemStack;hasNbt()Z", ordinal = 0), locals = LocalCapture.CAPTURE_FAILHARD)
    private void injectGetTooltipInsertPassiveChromaBlast(PlayerEntity player, TooltipContext context, CallbackInfoReturnable<List<Text>> cir, List<Text> list)
    {
        final ItemStack stack = (ItemStack) (Object) this;
        if (stack.getItem() instanceof SpectriteWeaponItem spectriteWeaponItem && spectriteWeaponItem.hasPassiveChromaBlast()
                && isSectionVisible(stack, ItemStack.TooltipSection.ENCHANTMENTS))
        {
            final MutableText chromaBlastText = new TranslatableText("item.spectrite.modifiers.chroma_blast");
            chromaBlastText.append(" ").append(new TranslatableText("enchantment.level." + spectriteWeaponItem.getChromaBlastLevel()));
            list.add(new SpectriteText(chromaBlastText, false));
        }
    }

    @Inject(method = "getTooltip", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/item/ItemStack;hasNbt()Z", ordinal = 1), locals = LocalCapture.CAPTURE_FAILHARD)
    private void injectGetTooltipInsertSpectriteInfo(PlayerEntity player, TooltipContext context, CallbackInfoReturnable<List<Text>> cir, List<Text> list)
    {
        final ItemStack stack = (ItemStack) (Object) this;
        if (stack.getItem() instanceof SpectriteChargeableItem spectriteChargeableItem)
        {
            final SpectriteToolItem spectriteToolItem = spectriteChargeableItem instanceof SpectriteToolItem
                    ? (SpectriteToolItem) spectriteChargeableItem
                    : null;
            final SpectriteWeaponItem spectriteWeaponItem = spectriteChargeableItem instanceof SpectriteWeaponItem
                    ? (SpectriteWeaponItem) spectriteChargeableItem
                    : null;
            if (spectriteWeaponItem != null || (spectriteToolItem != null && spectriteToolItem.getChargedEfficiencyMultiplier() > 1f)
                    && isSectionVisible(stack, ItemStack.TooltipSection.MODIFIERS))
            {
                if (stack.getAttributeModifiers(EquipmentSlot.MAINHAND).isEmpty())
                    list.add(LiteralText.EMPTY);
                list.add(new TranslatableText("item.spectrite.modifiers.charged").formatted(Formatting.GRAY));
                if (spectriteToolItem != null)
                    list.add(new LiteralText(" ").append(new TranslatableText("item.spectrite.modifiers.charged.tool_efficiency", (int) (spectriteToolItem.getChargedEfficiencyMultiplier() * 100f)).formatted(Formatting.BLUE)));
                if (spectriteWeaponItem != null)
                {
                    final int chromaBlastLevel = spectriteWeaponItem.getChromaBlastLevel() + (spectriteWeaponItem.hasPassiveChromaBlast() ? 1 : 0);
                    final MutableText chromaBlastText = new TranslatableText("item.spectrite.modifiers.chroma_blast");
                    chromaBlastText.append(" ").append(new TranslatableText("enchantment.level." + chromaBlastLevel));
                    list.add(new LiteralText(" ").append(new SpectriteText(chromaBlastText, false)));
                }
            }
        }
    }

    private static boolean isSectionVisible(ItemStack stack, ItemStack.TooltipSection tooltipSection)
    {
        return (((ItemStackClientAccessor) (Object) stack).invokeGetHideFlags() & tooltipSection.getFlag()) == 0;
    }
}
