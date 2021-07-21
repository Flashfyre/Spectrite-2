package com.flashfyre.spectrite.client;

import com.flashfyre.spectrite.Spectrite;
import com.flashfyre.spectrite.block.entity.BlockEntities;
import com.flashfyre.spectrite.client.particle.Particles;
import com.flashfyre.spectrite.client.particle.SpectriteExplosionEmitterParticle;
import com.flashfyre.spectrite.client.render.*;
import com.flashfyre.spectrite.client.resourcePack.SpectriteResourcePack;
import com.flashfyre.spectrite.client.util.SpectriteClientUtils;
import com.flashfyre.spectrite.item.Items;
import com.flashfyre.spectrite.util.SpectriteUtils;
import ladysnake.satin.api.event.EntitiesPostRenderCallback;
import ladysnake.satin.api.event.EntitiesPreRenderCallback;
import ladysnake.satin.api.event.ShaderEffectRenderCallback;
import ladysnake.satin.api.managed.ManagedCoreShader;
import ladysnake.satin.api.managed.ShaderEffectManager;
import ladysnake.satin.api.managed.uniform.Uniform1f;
import ladysnake.satin.api.managed.uniform.Uniform2f;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.block.entity.ChestBlockEntityRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.AbstractMap;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class SpectriteClient extends Spectrite implements ClientModInitializer
{
    public static SpectriteClient CLIENT_INSTANCE;

    public SpectriteResourcePack resourcePack;

    private static final ManagedCoreShader[] hue = new ManagedCoreShader[5];
    private static final Uniform1f[] hueSTime = new Uniform1f[5];
    private static final Uniform1f[] hueSaturation = new Uniform1f[5];

    private static final ManagedCoreShader[] spectriteItem = new ManagedCoreShader[5];
    private static final Uniform1f[] spectriteItemSTime = new Uniform1f[5];
    private static final Uniform1f[] spectriteItemSaturation = new Uniform1f[5];

    private static final ManagedCoreShader[] spectriteItemCharged = new ManagedCoreShader[4];
    private static final Uniform1f[] spectriteItemChargedSTime = new Uniform1f[4];
    private static final Uniform1f[] spectriteItemChargedSaturation = new Uniform1f[4];
    private static final Uniform2f[] spectriteItemChargedTexSize = new Uniform2f[4];

    public static Map.Entry<Integer, Integer> BLOCKS_TEXTURE_SIZE = new AbstractMap.SimpleEntry<>(1, 1);
    public static Map.Entry<Integer, Integer> CHARGEABLE_SPECTRITE_ENTITY_TEXTURE_SIZE = new AbstractMap.SimpleEntry<>(1, 1);

    private static int ticks;

    static
    {
        final Identifier hueShaderId = new Identifier(MODID, "hue");
        final Identifier spectriteItemShaderId = new Identifier(MODID, "spectrite_item");
        final Identifier spectriteItemChargedShaderId = new Identifier(MODID, "spectrite_item_charged");

        for (int h = 0; h < spectriteItem.length; h++)
        {
            final ManagedCoreShader hueShader = ShaderEffectManager.getInstance().manageCoreShader(hueShaderId);
            hue[h] = hueShader;
            hueSTime[h] = hueShader.findUniform1f("STime");
            hueSaturation[h] = hueShader.findUniform1f("Saturation");

            final ManagedCoreShader spectriteItemShader = ShaderEffectManager.getInstance().manageCoreShader(spectriteItemShaderId);
            spectriteItem[h] = spectriteItemShader;
            spectriteItemSTime[h] = spectriteItemShader.findUniform1f("STime");
            spectriteItemSaturation[h] = spectriteItemShader.findUniform1f("Saturation");

            if (h < spectriteItemCharged.length)
            {
                final ManagedCoreShader spectriteItemChargedShader = ShaderEffectManager.getInstance().manageCoreShader(spectriteItemChargedShaderId);
                spectriteItemCharged[h] = spectriteItemChargedShader;
                spectriteItemChargedSTime[h] = spectriteItemChargedShader.findUniform1f("STime");
                spectriteItemChargedSaturation[h] = spectriteItemChargedShader.findUniform1f("Saturation");
                spectriteItemChargedTexSize[h] = spectriteItemChargedShader.findUniform2f("TexSize");
            }
        }
    }

    @Override
    public void onInitializeClient()
    {
        INSTANCE = this;
        CLIENT_INSTANCE = this;

        FabricLoader.getInstance().getModContainer(MODID).ifPresent(modContainer ->
                ResourceManagerHelper.registerBuiltinResourcePack(getId("default"), modContainer, ResourcePackActivationType.DEFAULT_ENABLED));

        ClientTickEvents.END_CLIENT_TICK.register(client -> ticks++);
        EntitiesPreRenderCallback.EVENT.register((camera, frustum, tickDelta) ->
        {

        });
        EntitiesPostRenderCallback.EVENT.register((camera, frustum, tickDelta) ->
        {
            MinecraftClient client = MinecraftClient.getInstance();
            ((ClearCancelFramebuffer) client.getFramebuffer()).spectrite$cancelNextClear();
            SpectriteChargedEntityShader.EFFECT.render(tickDelta);
            SpectriteChargedEntityShader.clear();
            client.getFramebuffer().beginWrite(false);
        });
        ShaderEffectRenderCallback.EVENT.register(tickDelta ->
        {
            final float sTimeValue = (ticks + tickDelta) * 0.05f;

            SpectriteChargedEntityShader.STIME.set(sTimeValue);
            SpectriteChargedHeldItemEntityShader.STIME.set(sTimeValue);

            for (int h = 0; h < hue.length; h++)
            {
                final float saturation = 1f - 0.25f * (float) h;
                hueSTime[h].set(sTimeValue);
                hueSaturation[h].set(saturation);

                spectriteItemSTime[h].set(sTimeValue);
                spectriteItemSaturation[h].set(saturation);

                if (h < spectriteItemCharged.length)
                {
                    spectriteItemChargedSTime[h].set(sTimeValue);
                    spectriteItemChargedSaturation[h].set(saturation);
                    spectriteItemChargedTexSize[h].set(BLOCKS_TEXTURE_SIZE.getKey(), BLOCKS_TEXTURE_SIZE.getValue());
                }
            }

            MinecraftClient client = MinecraftClient.getInstance();
            ((ClearCancelFramebuffer) client.getFramebuffer()).spectrite$cancelNextClear();
            SpectriteChargedHeldItemEntityShader.EFFECT.render(tickDelta);
            SpectriteChargedHeldItemEntityShader.clear();
            client.getFramebuffer().beginWrite(false);
        });

        BlockEntityRendererRegistry.INSTANCE.register(BlockEntities.SPECTRITE_CHEST, ChestBlockEntityRenderer::new);

        ClientSpriteRegistryCallback.event(TexturedRenderLayers.CHEST_ATLAS_TEXTURE).register((texture, registry) ->
                registry.register(getId("entity/chest/spectrite")));
        ClientSpriteRegistryCallback.event(TexturedRenderLayers.CHEST_ATLAS_TEXTURE).register((texture, registry) ->
                registry.register(getId("entity/chest/spectrite_left")));
        ClientSpriteRegistryCallback.event(TexturedRenderLayers.CHEST_ATLAS_TEXTURE).register((texture, registry) ->
                registry.register(getId("entity/chest/spectrite_right")));

        FabricModelPredicateProviderRegistry.register(Items.SPECTRITE_BOW, new Identifier("pulling"),
                (stack, world, entity, seed) -> entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1.0F : 0.0F);
        FabricModelPredicateProviderRegistry.register(Items.SPECTRITE_BOW, new Identifier("pull"),
                (stack, world, entity, seed) ->
                {
                    if (entity == null)
                    {
                        return 0.0F;
                    } else
                    {
                        return entity.getActiveItem() != stack ? 0.0F : (float) (stack.getMaxUseTime() - entity.getItemUseTimeLeft()) / 20.0F;
                    }
                });
        FabricModelPredicateProviderRegistry.register(Items.DEPLETED_SPECTRITE_BOW, new Identifier("pulling"),
                (stack, world, entity, seed) -> entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1.0F : 0.0F);
        FabricModelPredicateProviderRegistry.register(Items.DEPLETED_SPECTRITE_BOW, new Identifier("pull"),
                (stack, world, entity, seed) ->
                {
                    if (entity == null)
                    {
                        return 0.0F;
                    } else
                    {
                        return entity.getActiveItem() != stack ? 0.0F : (float) (stack.getMaxUseTime() - entity.getItemUseTimeLeft()) / 20.0F;
                    }
                });

        final Item spectriteShieldItem = Items.SPECTRITE_SHIELD;
        final Item depletedSpectriteShieldItem = Items.DEPLETED_SPECTRITE_SHIELD;
        final SpectriteShieldItemRenderer spectriteShieldItemRenderer = new SpectriteShieldItemRenderer();
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(spectriteShieldItemRenderer);
        BuiltinItemRendererRegistry.INSTANCE.register(spectriteShieldItem, spectriteShieldItemRenderer);
        BuiltinItemRendererRegistry.INSTANCE.register(depletedSpectriteShieldItem, spectriteShieldItemRenderer);
        FabricModelPredicateProviderRegistry.register(spectriteShieldItem, new Identifier("blocking"),
                (stack, world, entity, seed) -> entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1.0F : 0.0F);
        FabricModelPredicateProviderRegistry.register(depletedSpectriteShieldItem, new Identifier("blocking"),
                (stack, world, entity, seed) -> entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1.0F : 0.0F);

        final Item spectriteTridentItem = Items.SPECTRITE_TRIDENT;
        final Item depletedSpectriteTridentItem = Items.DEPLETED_SPECTRITE_TRIDENT;
        final SpectriteTridentItemRenderer spectriteTridentItemRenderer = new SpectriteTridentItemRenderer();
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(spectriteTridentItemRenderer);
        BuiltinItemRendererRegistry.INSTANCE.register(spectriteTridentItem, spectriteTridentItemRenderer);
        BuiltinItemRendererRegistry.INSTANCE.register(depletedSpectriteTridentItem, spectriteTridentItemRenderer);
        FabricModelPredicateProviderRegistry.register(spectriteTridentItem, new Identifier("throwing"),
                (stack, world, entity, seed) -> entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1.0F : 0.0F);
        FabricModelPredicateProviderRegistry.register(depletedSpectriteTridentItem, new Identifier("throwing"),
                (stack, world, entity, seed) -> entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1.0F : 0.0F);

        Particles.initParticles();

        ClientPlayNetworking.registerGlobalReceiver(getId("explosion"), SpectriteClientUtils::explodeOnClient);
    }

    @Override
    public PlayerEntity getPlayer()
    {
        return MinecraftClient.getInstance().player;
    }

    public RenderLayer getHueLayer(RenderLayer layer)
    {
        return getHueLayer(layer, 0, false);
    }

    public RenderLayer getHueLayer(RenderLayer layer, int damage, boolean charged)
    {
        if (layer == null)
            return null;

        assert (!charged || damage < 4);
        final RenderLayer hueRenderLayer = hue[damage].getRenderLayer(layer);
        return !charged ? hueRenderLayer : SpectriteChargedEntityShader.SUPPLIER.getRenderLayer(hueRenderLayer);
    }

    public RenderLayer getHeldItemEntityHueLayer(RenderLayer layer, int damage, boolean charged)
    {
        if (layer == null)
            return null;
        if (!charged)
            return getHueLayer(layer, damage, false);

        assert (damage < 4);
        final RenderLayer hueRenderLayer = hue[damage].getRenderLayer(layer);
        return SpectriteChargedHeldItemEntityShader.SUPPLIER.getRenderLayer(hueRenderLayer);
    }

    public RenderLayer getSpectriteItemLayer(RenderLayer layer, int damage, boolean charged)
    {
        if (layer == null)
            return null;
        assert (!charged || damage < 4);
        return (!charged ? spectriteItem[damage] : spectriteItemCharged[damage]).getRenderLayer(layer);
    }

    @Override
    public void spawnSpectriteExplosionEmitterParticle(World world, double posX, double posY, double posZ, double power)
    {
        MinecraftClient.getInstance().particleManager.addParticle(new SpectriteExplosionEmitterParticle((ClientWorld) world, posX, posY, posZ, power));
    }

    @Override
    public void spawnSpectriteExplosionParticle(double posX, double posY, double posZ, double xSpeed, double ySpeed, double zSpeed)
    {
        final Particle particle = MinecraftClient.getInstance().particleManager.addParticle(ParticleTypes.EXPLOSION, posX, posY, posZ, xSpeed, ySpeed, zSpeed);
        if (particle != null)
        {
            final float offsetLevel = 18F * (float) (Double.valueOf(Math.abs(posX + posZ) + posY) % 20D);
            final float[] c = SpectriteUtils.getCurrentSpectriteRGBColor(offsetLevel);
            particle.setColor(c[0], c[1], c[2]);
        }
    }
}
