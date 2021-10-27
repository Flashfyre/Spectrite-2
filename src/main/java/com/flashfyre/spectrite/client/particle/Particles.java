package com.flashfyre.spectrite.client.particle;


import com.flashfyre.spectrite.Spectrite;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.registry.Registry;

@Environment(EnvType.CLIENT)
public class Particles
{
    public static ParticleTextureSheet PARTICLE_SHEET_SPECTRITE = new ParticleTextureSheet()
    {
        public void begin(BufferBuilder bufferBuilder, TextureManager textureManager)
        {
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE);
            RenderSystem.depthMask(false);
            RenderSystem.setShaderTexture(0, SpriteAtlasTexture.PARTICLE_ATLAS_TEXTURE);
            bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR_LIGHT);
        }

        public void draw(Tessellator tessellator)
        {
            tessellator.draw();
        }

        public String toString()
        {
            return "PARTICLE_SHEET_SPECTRITE";
        }
    };

    public static DefaultParticleType SUPERCHROMATIC_GLOW;

    public static DefaultParticleType CHROMA_BLAST;

    public static void initParticles()
    {
        SUPERCHROMATIC_GLOW = registerParticleType("superchromatic_glow", true, SuperchromaticGlowParticle.Factory::new);
        CHROMA_BLAST = registerParticleType("chroma_blast", true, ChromaBlastParticle.Factory::new);
    }

    private static <T extends ParticleEffect> DefaultParticleType registerParticleType(String name, boolean alwaysShow,
                                                                                       ParticleFactoryRegistry.PendingParticleFactory<T> factory)

    {
        final T ret = (T) Registry.register(Registry.PARTICLE_TYPE, Spectrite.getId(name), new DefaultParticleType(alwaysShow));
        ParticleFactoryRegistry.getInstance().<T>register((ParticleType<T>) ret, factory);
        return (DefaultParticleType) ret;
    }
}
