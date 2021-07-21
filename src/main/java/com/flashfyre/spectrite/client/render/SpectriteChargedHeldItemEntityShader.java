package com.flashfyre.spectrite.client.render;

import com.flashfyre.spectrite.Spectrite;
import ladysnake.satin.api.managed.ManagedFramebuffer;
import ladysnake.satin.api.managed.ManagedShaderEffect;
import ladysnake.satin.api.managed.ShaderEffectManager;
import ladysnake.satin.api.managed.uniform.Uniform1f;
import ladysnake.satin.impl.RenderLayerSupplier;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.util.Identifier;

public class SpectriteChargedHeldItemEntityShader extends SpectriteChargedShader
{
    private static final Identifier spectriteChargedEntityId = Spectrite.getId("shaders/post/spectrite_charged_held_item_entity.json");
    public static final ManagedShaderEffect EFFECT = ShaderEffectManager.getInstance()
            .manage(spectriteChargedEntityId, SpectriteChargedHeldItemEntityShader::assignDepthTexture);
    private static final ManagedFramebuffer spectriteChargedFramebuffer = EFFECT.getTarget("final");
    public static final RenderLayerSupplier SUPPLIER = RenderLayerSupplier.framebuffer(
            "spectrite:spectrite_charged",
            SpectriteChargedHeldItemEntityShader::beginFbWrite,
            SpectriteChargedHeldItemEntityShader::performFbCleanup
    );
    public static final Uniform1f STIME = EFFECT.findUniform1f("STime");

    public SpectriteChargedHeldItemEntityShader(String name, VertexFormat vertexFormat, VertexFormat.DrawMode drawMode, int expectedBufferSize,
                                                boolean hasCrumbling, boolean translucent, Runnable startAction, Runnable endAction)
    {
        super(name, vertexFormat, drawMode, expectedBufferSize, hasCrumbling, translucent, startAction, endAction);
    }

    protected static void assignDepthTexture(ManagedShaderEffect shader)
    {
        assignDepthTexture(shader, spectriteChargedFramebuffer);
    }

    public static void beginFbWrite()
    {
        beginFbWrite(spectriteChargedFramebuffer.getFramebuffer());
    }

    public static void clear()
    {
        clear(spectriteChargedFramebuffer.getFramebuffer());
    }
}
