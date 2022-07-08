package com.flashfyre.spectrite.client.mixin;

import com.flashfyre.spectrite.SpectriteConfig;
import com.flashfyre.spectrite.item.*;
import com.flashfyre.spectrite.text.SpectriteText;
import com.flashfyre.spectrite.util.SuperchromaticItemUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.text.MutableText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
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
                final MutableText chromaBlastText = Text.translatable("item.spectrite.modifiers.chroma_blast");
                chromaBlastText.append(" ").append(Text.translatable("enchantment.level." + spectriteWeaponItem.getChromaBlastLevel()));
                list.add(new SpectriteText(chromaBlastText, false));
            } else if (SuperchromaticItemUtils.isSuperchromatic(stack))
            {
                list.add(new SpectriteText(Text.translatable("item.spectrite.modifiers.superchromized"), false));
                if (SuperchromaticItemUtils.getSuperchromaticItemPassiveChromaBlastLevel(item) > 0)
                {
                    final MutableText chromaBlastText = Text.translatable("item.spectrite.modifiers.chroma_blast");
                    chromaBlastText.append(" ").append(Text.translatable("enchantment.level." + SuperchromaticItemUtils.getSuperchromaticItemPassiveChromaBlastLevel(item)));
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
        if (item instanceof SpectriteItem spectriteItem)
        {
            if (spectriteItem.hasCustomTooltip())
            {
                int lineCount = 0;
                String tooltipLineText;
                do
                {
                    final String translationKey = item.getTranslationKey() + ".tooltip.l" + ++lineCount;
                    final MutableText tooltipLine = Text.translatable(translationKey);
                    ((TranslatableTextContentAccessor) tooltipLine.getContent()).invokeUpdateTranslations();
                    final StringVisitable translation = ((TranslatableTextContentAccessor) tooltipLine.getContent()).getTranslations().stream().findFirst().orElse(null);
                    tooltipLineText = translation != null ? translation.getString() : null;
                    if (tooltipLineText == null || tooltipLineText.equals(translationKey))
                        break;
                    list.add(tooltipLine.formatted(Formatting.GREEN));
                } while (true);
            }
            if (item instanceof SpectriteDamageableItem spectriteDamageableItem)
            {
                final SpectriteToolItem spectriteToolItem = spectriteDamageableItem instanceof SpectriteToolItem
                        ? (SpectriteToolItem) spectriteDamageableItem
                        : null;
                final SpectriteWeaponItem spectriteWeaponItem = spectriteDamageableItem instanceof SpectriteWeaponItem
                        ? (SpectriteWeaponItem) spectriteDamageableItem
                        : null;
                if (isSectionVisible(stack, ItemStack.TooltipSection.MODIFIERS))
                {
                    if (spectriteWeaponItem != null || (spectriteToolItem != null && spectriteToolItem.getChargedEfficiencyMultiplier() > 1f))
                    {
                        if (stack.getAttributeModifiers(EquipmentSlot.MAINHAND).isEmpty())
                            list.add(Text.empty());
                        list.add(Text.translatable("item.spectrite.modifiers.charged").formatted(Formatting.GRAY));
                        if (spectriteToolItem != null)
                            list.add(Text.literal(" ").append(Text.translatable("item.spectrite.modifiers.charged.tool_efficiency",
                                    (int) (spectriteToolItem.getChargedEfficiencyMultiplier() * 100f)).formatted(Formatting.BLUE)));
                        if (spectriteWeaponItem != null)
                        {
                            final int chromaBlastLevel = spectriteWeaponItem.getChromaBlastLevel() + (spectriteWeaponItem.hasPassiveChromaBlast() ? 1 : 0);
                            final MutableText chromaBlastText = Text.translatable("item.spectrite.modifiers.chroma_blast");
                            chromaBlastText.append(" ").append(Text.translatable("enchantment.level." + chromaBlastLevel));
                            list.add(Text.literal(" ").append(new SpectriteText(chromaBlastText, false)));
                            if (chromaBlastLevel > 0)
                            {
                                final float cooldownSeconds = MathHelper.ceil(SuperchromaticItemUtils.getSuperchromaticOrChargeableSpectriteItemCooldownTicks(item) / 2f) / 10f;
                                if (cooldownSeconds > 0f)
                                    list.add(Text.literal(" ")
                                            .append(Text.translatable("item.spectrite.modifiers.charged_attack_cooldown", cooldownSeconds).formatted(Formatting.DARK_AQUA)));
                            }
                        }
                    } else if (spectriteDamageableItem instanceof SpectriteArmorItem spectriteArmorItem)
                    {
                        if (stack.getAttributeModifiers(spectriteArmorItem.getSlotType()).isEmpty())
                            list.add(LiteralText.EMPTY);
                        final MutableText superchromaticText = new TranslatableText("effect.spectrite.superchromatic");
                        list.add(new TranslatableText("item.spectrite.modifiers.superchromatic_set_bonus").formatted(Formatting.GRAY));
                        list.add(new LiteralText(" ").append(new SpectriteText(superchromaticText, false)));
                    } else if (item instanceof SpectriteShieldItem)
                    {
                        list.add(Text.empty());
                        list.add(Text.translatable("item.modifiers.offhand").formatted(Formatting.GRAY));
                        final MutableText chromaGuardText = Text.translatable("effect.spectrite.chroma_guard");
                        chromaGuardText.append(" ").append(Text.translatable("enchantment.level.1"));
                        list.add(Text.literal(" ").append(new SpectriteText(chromaGuardText, false)));
                    }
                }
            } else if (item.getGroup() == ItemGroup.FOOD || item instanceof SuperchromaticChorusFruitItem)
            {
                final float cooldownSeconds;
                final boolean isUnlimited;
                if (item instanceof SuperchromaticChorusFruitItem)
                {
                    cooldownSeconds = MathHelper.ceil(SpectriteConfig.getSuperchromaticChorusFruitCooldown() * 10f) / 10f;
                    isUnlimited = SpectriteConfig.getSuperchromaticChorusFruitUses() == 0;
                } else
                {
                    cooldownSeconds = MathHelper.ceil(SpectriteConfig.getSuperchromaticFoodCooldown() * 10f) / 10f;
                    isUnlimited = SpectriteConfig.getSuperchromaticFoodUses() == 0;
                }
                if (cooldownSeconds > 0f)
                    list.add(Text.translatable("item.spectrite.modifiers.cooldown", cooldownSeconds).formatted(Formatting.DARK_AQUA));
                if (isUnlimited)
                    list.add(Text.translatable("item.spectrite.modifiers.unlimited_use").formatted(Formatting.GRAY));
            } else if (item instanceof SuperchromaticEnderPearlItem)
            {
                if (SpectriteConfig.getSuperchromaticEnderPearlUses() == 0)
                    list.add(Text.translatable("item.spectrite.modifiers.unlimited_use").formatted(Formatting.GRAY));
            } else if (item instanceof SpectriteBombItem)
            {
                if (isSectionVisible(stack, ItemStack.TooltipSection.MODIFIERS))
                {
                    list.add(Text.empty());
                    list.add(Text.translatable("item.spectrite.modifiers.on_impact").formatted(Formatting.GRAY));
                    final MutableText chromaBlastText = Text.translatable("item.spectrite.modifiers.chroma_blast");
                    chromaBlastText.append(" ").append(Text.translatable("enchantment.level.5"));
                    list.add(Text.literal(" ").append(new SpectriteText(chromaBlastText, false)));
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
                        final boolean hasMainHandModifiers = !stack.getAttributeModifiers(EquipmentSlot.MAINHAND).isEmpty();
                        if (!hasMainHandModifiers)
                            list.add(Text.empty());
                        if (item instanceof ToolItem)
                        {
                            if (!hasMainHandModifiers)
                                list.add(Text.translatable("item.modifiers.mainhand").formatted(Formatting.GRAY));
                            list.add(Text.literal(" ").append(Text.translatable("item.spectrite.modifiers.charged.tool_efficiency", (int) 125f).formatted(Formatting.BLUE)));
                        }
                        list.add(Text.translatable("item.spectrite.modifiers.charged").formatted(Formatting.GRAY));
                        if (item instanceof ToolItem)
                            list.add(Text.literal(" ").append(Text.translatable("item.spectrite.modifiers.charged.tool_efficiency", (int) 175f).formatted(Formatting.BLUE)));

                        final int chromaBlastLevel = SuperchromaticItemUtils.getSuperchromaticItemChromaBlastLevel(item)
                                + SuperchromaticItemUtils.getSuperchromaticItemPassiveChromaBlastLevel(item);
                        if (chromaBlastLevel > 0)
                        {
                            final MutableText chromaBlastText = Text.translatable("item.spectrite.modifiers.chroma_blast");
                            chromaBlastText.append(" ").append(Text.translatable("enchantment.level." + chromaBlastLevel));
                            list.add(Text.literal(" ").append(new SpectriteText(chromaBlastText, false)));
                        }
                    } else if (item instanceof ArmorItem armorItem)
                    {
                        if (stack.getAttributeModifiers(armorItem.getSlotType()).isEmpty())
                            list.add(Text.empty());
                        final MutableText superchromaticText = Text.translatable("effect.spectrite.superchromatic");
                        list.add(Text.translatable("item.spectrite.modifiers.superchromatic_set_bonus").formatted(Formatting.GRAY));
                        list.add(Text.literal(" ").append(new SpectriteText(superchromaticText, false).getString()));
                    }
                    if (SuperchromaticItemUtils.isSuperchromaticChargeable(stack) && SuperchromaticItemUtils.getSuperchromaticItemChromaBlastLevel(item) > 0)
                    {
                        final float cooldownSeconds = MathHelper.ceil(SuperchromaticItemUtils.getSuperchromaticOrChargeableSpectriteItemCooldownTicks(item) / 2f) / 10f;
                        if (cooldownSeconds > 0f)
                            list.add(Text.literal(" ")
                                    .append(Text.translatable("item.spectrite.modifiers.charged_attack_cooldown", cooldownSeconds).formatted(Formatting.DARK_AQUA)));
                    }
                } else
                {
                    final int superchromaticPoints = SuperchromaticItemUtils.getSuperchromaticPoints(stack);
                    if (superchromaticPoints > 0)
                    {
                        final int superchromizedPercent = MathHelper.floor((superchromaticPoints / (float) SuperchromaticItemUtils.getRequiredSuperchromaticPoints(stack)) * 100f);
                        list.add(new SpectriteText(Text.translatable("item.spectrite.modifiers.percent_superchromized", superchromizedPercent), false));
                    }
                }
            }
        }
    }

    private static boolean isSectionVisible(ItemStack stack, ItemStack.TooltipSection tooltipSection)
    {
        return (((ItemStackClientAccessor) (Object) stack).invokeGetHideFlags() & tooltipSection.getFlag()) == 0;
    }
}
