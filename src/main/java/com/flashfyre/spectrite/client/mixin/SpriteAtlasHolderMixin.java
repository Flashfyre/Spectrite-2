package com.flashfyre.spectrite.client.mixin;

import com.flashfyre.spectrite.client.SpectriteClient;
import com.flashfyre.spectrite.client.resourcePack.SpectriteResourcePack;
import com.flashfyre.spectrite.client.util.SpectriteTextureUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.SpriteAtlasHolder;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.StatusEffectSpriteManager;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(SpriteAtlasHolder.class)
public class SpriteAtlasHolderMixin
{
    @Inject(method = "prepare", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/texture/SpriteAtlasTexture;stitch(Lnet/minecraft/resource/ResourceManager;Ljava/util/stream/Stream;Lnet/minecraft/util/profiler/Profiler;I)Lnet/minecraft/client/texture/SpriteAtlasTexture$Data;"))
    private void injectPrepareAddSuperchromaticStatusIcon(ResourceManager resourceManager, Profiler profiler, CallbackInfoReturnable<SpriteAtlasTexture.Data> cir)
    {
        final SpriteAtlasHolder spriteAtlasHolder = (SpriteAtlasHolder) (Object) this;
        if (spriteAtlasHolder instanceof StatusEffectSpriteManager)
        {
            final SpectriteResourcePack resourcePack = SpectriteClient.CLIENT_INSTANCE.resourcePack;
            final byte[] mcMetaBytes = SpectriteTextureUtils.getMcMetaBytes(resourceManager, null);
            final NativeImage superchromaticEffectTexture = SpectriteTextureUtils.getSpectriteBlockTexture(resourceManager,
                    "top", new Identifier("block/diamond_block"));

            resourcePack.putImageDynamic("superchromatic", "mob_effect", superchromaticEffectTexture);
            resourcePack.putMcMeta("superchromatic", "mob_effect", mcMetaBytes);

            // While we're at it, may as well add the advancements GUI background
            final NativeImage advancementBackgroundTexture = SpectriteTextureUtils.getSpectriteOreTexture(resourceManager,
                    "stone", "ns", new Identifier("block/diamond_ore"), true);
            resourcePack.putImageDynamic("background", "gui/advancements", advancementBackgroundTexture);
        }
    }
}
