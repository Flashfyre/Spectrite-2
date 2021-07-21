package com.flashfyre.spectrite.client.render;

import com.flashfyre.spectrite.client.mixin.FramebufferAccessor;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import ladysnake.satin.api.managed.ManagedFramebuffer;
import ladysnake.satin.api.managed.ManagedShaderEffect;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexFormat;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;

@Environment(EnvType.CLIENT)
public class SpectriteChargedShader extends RenderLayer
{
    protected static final MinecraftClient client = MinecraftClient.getInstance();

    public SpectriteChargedShader(String name, VertexFormat vertexFormat, VertexFormat.DrawMode drawMode, int expectedBufferSize,
                                  boolean hasCrumbling, boolean translucent, Runnable startAction, Runnable endAction)
    {
        super(name, vertexFormat, drawMode, expectedBufferSize, hasCrumbling, translucent, startAction, endAction);
    }

    @Override
    public boolean isOutline()
    {
        return true;
    }

    protected static void assignDepthTexture(ManagedShaderEffect shader, ManagedFramebuffer spectriteChargedFramebuffer)
    {
        client.getFramebuffer().beginWrite(false);
        final int depthTexture = client.getFramebuffer().getDepthAttachment();
        if (depthTexture > -1)
        {
            spectriteChargedFramebuffer.beginWrite(false);
            GlStateManager._glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, depthTexture, 0);
        }
    }

    protected static void beginFbWrite(Framebuffer spectriteChargedFramebuffer)
    {
        if (spectriteChargedFramebuffer != null)
            spectriteChargedFramebuffer.beginWrite(false);
    }

    protected static void performFbCleanup()
    {
        final Framebuffer framebuffer = MinecraftClient.getInstance().getFramebuffer();
        framebuffer.beginWrite(false);
    }

    protected static void clear(Framebuffer spectriteChargedFramebuffer)
    {
        if (spectriteChargedFramebuffer != null)
        {
            spectriteChargedFramebuffer.beginWrite(false);
            final float[] clearColor = ((FramebufferAccessor) spectriteChargedFramebuffer).getClearColor();
            RenderSystem.clearColor(clearColor[0], clearColor[1], clearColor[2], clearColor[3]);
            RenderSystem.clear(GL11.GL_COLOR_BUFFER_BIT, MinecraftClient.IS_SYSTEM_MAC);
        }
    }
}
