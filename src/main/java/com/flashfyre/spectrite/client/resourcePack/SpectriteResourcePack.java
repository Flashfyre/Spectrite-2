package com.flashfyre.spectrite.client.resourcePack;

import com.flashfyre.spectrite.Spectrite;
import com.flashfyre.spectrite.client.etc.SpectriteTextureOverlayData;
import com.flashfyre.spectrite.client.mixin.NativeImageAccessor;
import com.flashfyre.spectrite.client.util.SpectriteEntityRenderUtils;
import com.flashfyre.spectrite.client.util.SpectriteTextureUtils;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Environment(EnvType.CLIENT)
public class SpectriteResourcePack implements ResourcePack
{
    private static final Set<String> namespaces = Sets.newHashSet(Spectrite.MODID);
    private final Object2ObjectMap<String, byte[]> resources = new Object2ObjectOpenHashMap<>();
    private final Spectrite mod;

    public SpectriteResourcePack(Spectrite mod)
    {
        this.mod = mod;
    }

    private static Identifier fromPath(String path)
    {
        final String[] split = path.split("/", 2);
        return new Identifier(split[0], split[1]);
    }

    public Identifier putImageDynamic(String name, String category, NativeImage image)
    {
        this.putImage("assets/" + mod.MODID + "/textures/" + category + "/" + name + ".png", image);
        return new Identifier(mod.MODID, category + "/" + name);
    }

    public void putImage(String location, NativeImage image)
    {
        final ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        try (WritableByteChannel out = Channels.newChannel(outStream))
        {
            ((NativeImageAccessor) (Object) image).invokeWrite(out);
            this.resources.put(location, outStream.toByteArray());
            if (true)
            {
                final File file = new File("debug/spectrite/" + location);
                file.getParentFile().mkdirs();

                try (FileChannel fc = FileChannel.open(file.toPath(), StandardOpenOption.CREATE, StandardOpenOption.WRITE))
                {
                    ((NativeImageAccessor) (Object) image).invokeWrite(fc);
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        } catch (IOException e)
        {
            mod.warn("Could not close output channel for texture " + location + ". Exception: " + e.getMessage());
        }
    }

    public void putMcMeta(String name, String category, byte[] mcMetaBytes)
    {
        writeFileBytes("assets/" + mod.MODID + "/textures/" + category + "/" + name + ".png.mcmeta", mcMetaBytes);

    }

    public void putBlockState(String name, byte[] blockStateBytes)
    {
        writeFileBytes("assets/" + mod.MODID + "/blockstates/" + name + ".json", blockStateBytes);
    }

    public void putBlockModel(String modelName, byte[] modelBytes)
    {
        writeFileBytes("assets/" + mod.MODID + "/models/block/" + modelName + ".json", modelBytes);
    }

    public void putItemModel(String modelName, byte[] modelBytes)
    {
        writeFileBytes("assets/" + mod.MODID + "/models/item/" + modelName + ".json", modelBytes);
    }

    private void writeFileBytes(String filePath, byte[] fileBytes)
    {
        this.resources.put(filePath, fileBytes);
        if (true)
        {
            var file = new File("debug/spectrite/" + filePath);
            file.getParentFile().mkdirs();

            try (FileChannel fc = FileChannel.open(file.toPath(), StandardOpenOption.CREATE, StandardOpenOption.WRITE))
            {
                fc.write(ByteBuffer.wrap(fileBytes));
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    @Nullable
    @Override
    public InputStream openRoot(String fileName) throws IOException
    {
        byte[] data;
        if ((data = this.resources.get(fileName)) != null)
        {
            return new ByteArrayInputStream(data);
        }
        throw new IOException("Generated resources pack has no data or alias for " + fileName);
    }

    @Override
    public InputStream open(ResourceType type, Identifier id) throws IOException
    {
        if (type == ResourceType.SERVER_DATA)
            throw new IOException("Reading server data from Spectrite resource pack");
        return this.openRoot(type.getDirectory() + "/" + id.getNamespace() + "/" + id.getPath());
    }

    @Override
    public Collection<Identifier> findResources(ResourceType type, String namespace, String prefix, int maxDepth, Predicate<String> pathFilter)
    {
        if (type == ResourceType.SERVER_DATA)
            return Collections.emptyList();

        final ResourceManager resourceManager = MinecraftClient.getInstance().getResourceManager();

        for (Map.Entry<Identifier, Map<String, Map<Identifier, Identifier>>> entry : SpectriteEntityRenderUtils.ENTITY_SPECTRITE_TEXTURE_CACHE.entrySet())
        {
            final Identifier entityId = entry.getKey();
            final Map<String, List<ModelPart>> entityModelPartCache = SpectriteEntityRenderUtils.ENTITY_MODEL_PART_CACHE.getOrDefault(entityId, null);
            final SpectriteTextureOverlayData overlayData = entityModelPartCache == null
                    ? SpectriteEntityRenderUtils.SPECTRITE_TEXTURE_OVERLAY_CACHE.getOrDefault(entityId, null)
                    : null;
            if (entityModelPartCache != null || overlayData != null)
            {
                for (Map.Entry<String, Map<Identifier, Identifier>> entityEntry : entry.getValue().entrySet())
                {
                    final String modelClassName = entityEntry.getKey();
                    if (overlayData != null || entityModelPartCache.containsKey(modelClassName))
                    {
                        for (Map.Entry<Identifier, Identifier> entityModelEntry : entityEntry.getValue().entrySet())
                        {
                            final NativeImage spectriteEntityTexture =
                                    overlayData == null
                                            ? SpectriteTextureUtils.getEntityTexture(
                                            resourceManager, entityId.toString(), modelClassName,
                                            entityModelEntry.getKey(), entityModelPartCache.get(modelClassName))
                                            : SpectriteTextureUtils.getEntityTexture(resourceManager,
                                            entityModelEntry.getKey(), overlayData);
                            putImage(
                                    "assets/" + Spectrite.MODID + "/" + entityModelEntry.getValue().getPath(),
                                    spectriteEntityTexture);
                        }
                    }
                }
            }
        }

        final String start = "assets/" + namespace + "/" + prefix;

        return this.resources.keySet().stream()
                .filter(s -> s.startsWith(start) && pathFilter.test(s))
                .map(SpectriteResourcePack::fromPath)
                .collect(Collectors.toList());
    }

    @Override
    public boolean contains(ResourceType type, Identifier id)
    {
        String path = type.getDirectory() + "/" + id.getNamespace() + "/" + id.getPath();

        return this.resources.containsKey(path);
    }

    @Override
    public Set<String> getNamespaces(ResourceType type)
    {
        return namespaces;
    }

    @Nullable
    @Override
    public <T> T parseMetadata(ResourceMetadataReader<T> metaReader) throws IOException
    {
        return null;
    }

    @Override
    public String getName()
    {
        return "Spectrite generated textures";
    }

    @Override
    public void close()
    {

    }
}
