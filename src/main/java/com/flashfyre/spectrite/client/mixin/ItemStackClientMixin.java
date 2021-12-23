package com.flashfyre.spectrite.client.mixin;

import com.flashfyre.spectrite.item.*;
import com.flashfyre.spectrite.text.SpectriteText;
import com.flashfyre.spectrite.util.SuperchromaticItemUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
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
    private void injectGetTooltipInsertSuperchromaticEnchantments(PlayerEntity player, TooltipContext context, CallbackInfoReturnable<List<Text>> cir, List<Text> list)
    {
        final ItemStack stack = (ItemStack) (Object) this;
        final Item item = stack.getItem();
        if (isSectionVisible(stack, ItemStack.TooltipSection.ENCHANTMENTS))
        {
            if (item instanceof SpectriteWeaponItem spectriteWeaponItem && spectriteWeaponItem.hasPassiveChromaBlast())
            {
                final MutableText chromaBlastText = new TranslatableText("item.spectrite.modifiers.chroma_blast");
                chromaBlastText.append(" ").append(new TranslatableText("enchantment.level." + spectriteWeaponItem.getChromaBlastLevel()));
                list.add(new SpectriteText(chromaBlastText, false));
            } else if (SuperchromaticItemUtils.isSuperchromatic(stack))
            {
                list.add(new SpectriteText(new TranslatableText("item.spectrite.modifiers.superchromized"), false));
                if (SuperchromaticItemUtils.getSuperchromaticItemPassiveChromaBlastLevel(item) > 0)
                {
                    final MutableText chromaBlastText = new TranslatableText("item.spectrite.modifiers.chroma_blast");
                    chromaBlastText.append(" ").append(new TranslatableText("enchantment.level." + SuperchromaticItemUtils.getSuperchromaticItemPassiveChromaBlastLevel(item)));
                    list.add(new SpectriteText(chromaBlastText, false));
                }
            }
        }
    }

    @Inject(method = "getTooltip", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/item/ItemStack;hasNbt()Z", ordinal = 1), locals = LocalCapture.CAPTURE_FAILHARD)
    private void injectGetTooltipInsertSpectriteInfo(PlayerEntity player, TooltipContext context, CallbackInfoReturnable<List<Text>> cir, List<Text> list)
    {
        final ItemStack stack = (ItemStack) (Object) this;
        final Item item = stack.getItem();
        if (item instanceof SpectriteDamageableItem spectriteItem)
        {
            final SpectriteToolItem spectriteToolItem = spectriteItem instanceof SpectriteToolItem
                    ? (SpectriteToolItem) spectriteItem
                    : null;
            final SpectriteWeaponItem spectriteWeaponItem = spectriteItem instanceof SpectriteWeaponItem
                    ? (SpectriteWeaponItem) spectriteItem
                    : null;
            if (isSectionVisible(stack, ItemStack.TooltipSection.MODIFIERS))
            {
                if (spectriteWeaponItem != null || (spectriteToolItem != null && spectriteToolItem.getChargedEfficiencyMultiplier() > 1f))
                {
                    if (stack.getAttributeModifiers(EquipmentSlot.MAINHAND).isEmpty())
                        list.add(LiteralText.EMPTY);
                    list.add(new TranslatableText("item.spectrite.modifiers.charged").formatted(Formatting.GRAY));
                    if (spectriteToolItem != null)
                        list.add(new LiteralText(" ").append(new TranslatableText("item.spectrite.modifiers.charged.tool_efficiency",
                                (int) (spectriteToolItem.getChargedEfficiencyMultiplier() * 100f)).formatted(Formatting.BLUE)));
                    if (spectriteWeaponItem != null)
                    {
                        final int chromaBlastLevel = spectriteWeaponItem.getChromaBlastLevel() + (spectriteWeaponItem.hasPassiveChromaBlast() ? 1 : 0);
                        final MutableText chromaBlastText = new TranslatableText("item.spectrite.modifiers.chroma_blast");
                        chromaBlastText.append(" ").append(new TranslatableText("enchantment.level." + chromaBlastLevel));
                        list.add(new LiteralText(" ").append(new SpectriteText(chromaBlastText, false)));
                    }
                } else if (spectriteItem instanceof SpectriteArmorItem spectriteArmorItem)
                {
                    if (stack.getAttributeModifiers(spectriteArmorItem.getSlotType()).isEmpty())
                        list.add(LiteralText.EMPTY);
                    final MutableText superchromaticText = new TranslatableText("effect.spectrite.superchromatic");
                    list.add(new TranslatableText("item.spectrite.modifiers.superchromatic_set_bonus").formatted(Formatting.GRAY));
                    list.add(new LiteralText(" ").append(new SpectriteText(superchromaticText, false)));
                }
            }
        } else if (SuperchromaticItemUtils.isSuperchromaticCompatible(stack))
        {
            if (isSectionVisible(stack, ItemStack.TooltipSection.MODIFIERS))
            {
                if (SuperchromaticItemUtils.isSuperchromatic(stack))
                {
                    if (item instanceof ToolItem || item instanceof BowItem)
                    {
                        if (stack.getAttributeModifiers(EquipmentSlot.MAINHAND).isEmpty())
                            list.add(LiteralText.EMPTY);
                        list.add(new TranslatableText("item.spectrite.modifiers.charged").formatted(Formatting.GRAY));
                        if (item instanceof ToolItem)
                            list.add(new LiteralText(" ").append(new TranslatableText("item.spectrite.modifiers.charged.tool_efficiency", (int) 150f).formatted(Formatting.BLUE)));

                        final int chromaBlastLevel = SuperchromaticItemUtils.getSuperchromaticItemChromaBlastLevel(item)
                                + SuperchromaticItemUtils.getSuperchromaticItemPassiveChromaBlastLevel(item);
                        if (chromaBlastLevel > 0)
                        {
                            final MutableText chromaBlastText = new TranslatableText("item.spectrite.modifiers.chroma_blast");
                            chromaBlastText.append(" ").append(new TranslatableText("enchantment.level." + chromaBlastLevel));
                            list.add(new LiteralText(" ").append(new SpectriteText(chromaBlastText, false)));
                        }
                    } else if (item instanceof ArmorItem armorItem)
                    {
                        if (stack.getAttributeModifiers(armorItem.getSlotType()).isEmpty())
                            list.add(LiteralText.EMPTY);
                        final MutableText superchromaticText = new TranslatableText("effect.spectrite.superchromatic");
                        list.add(new TranslatableText("item.spectrite.modifiers.superchromatic_set_bonus").formatted(Formatting.GRAY));
                        list.add(new LiteralText(" ").append(new SpectriteText(superchromaticText, false)));
                    }
                } else
                {
                    final int superchromaticPoints = SuperchromaticItemUtils.getSuperchromaticPoints(stack);
                    if (superchromaticPoints > 0)
                    {
                        final int superchromizedPercent = MathHelper.floor((superchromaticPoints / (float) SuperchromaticItemUtils.getRequiredSuperchromaticPoints(stack)) * 100f);
                        list.add(new SpectriteText(new TranslatableText("item.spectrite.modifiers.percent_superchromized", superchromizedPercent), false));
                    }
                }
            }
        } else if (stack.getItem() instanceof SpectriteBombItem)
        {
            list.add(new TranslatableText("item.spectrite.modifiers.on_impact").formatted(Formatting.GRAY));
            final MutableText chromaBlastText = new TranslatableText("item.spectrite.modifiers.chroma_blast");
            chromaBlastText.append(" ").append(new TranslatableText("enchantment.level.5"));
            list.add(new LiteralText(" ").append(new SpectriteText(chromaBlastText, false)));
        }
    }

    private static boolean isSectionVisible(ItemStack stack, ItemStack.TooltipSection tooltipSection)
    {
        return (((ItemStackClientAccessor) (Object) stack).invokeGetHideFlags() & tooltipSection.getFlag()) == 0;
    }
}
