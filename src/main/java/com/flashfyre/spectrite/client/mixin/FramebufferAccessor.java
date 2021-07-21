package com.flashfyre.spectrite.client.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.Framebuffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Environment(EnvType.CLIENT)
@Mixin(Framebuffer.class)
public interface FramebufferAccessor
{
    @Accessor("clearColor")
    float[] getClearColor();
}
