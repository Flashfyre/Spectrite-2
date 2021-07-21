package com.flashfyre.spectrite.client.mixin;

import com.flashfyre.spectrite.client.render.ClearCancelFramebuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.Framebuffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(Framebuffer.class)
public class FramebufferMixin implements ClearCancelFramebuffer
{
    private boolean spectrite$nextClearCancelled;

    @Override
    public void spectrite$cancelNextClear()
    {
        spectrite$nextClearCancelled = true;
    }

    @Inject(method = "clear", at = @At("HEAD"), cancellable = true)
    private void spectrite$injectClearCancel(CallbackInfo ci)
    {
        if (spectrite$nextClearCancelled)
        {
            spectrite$nextClearCancelled = false;
            ci.cancel();
        }
    }
}
