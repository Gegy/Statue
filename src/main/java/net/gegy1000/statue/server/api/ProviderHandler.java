package net.gegy1000.statue.server.api;

import net.gegy1000.statue.server.provider.qubble.QubbleModelProvider;
import net.gegy1000.statue.server.provider.qubble.QubbleTextureProvider;
import net.gegy1000.statue.server.provider.tabula.TabulaModelProvider;
import net.gegy1000.statue.server.provider.tabula.TabulaTextureProvider;

import java.util.ArrayList;
import java.util.List;

public class ProviderHandler {
    private static final List<ModelProvider<?>> MODEL_PROVIDERS = new ArrayList<>();
    private static final List<TextureProvider<?>> TEXTURE_PROVIDERS = new ArrayList<>();

    public static void onPreInit() {
        ProviderHandler.register(new QubbleModelProvider());
        ProviderHandler.register(new QubbleTextureProvider());

        ProviderHandler.register(new TabulaModelProvider());
        ProviderHandler.register(new TabulaTextureProvider());
    }

    public static void register(ModelProvider<?> provider) {
        MODEL_PROVIDERS.add(provider);
    }

    public static void register(TextureProvider<?> provider) {
        TEXTURE_PROVIDERS.add(provider);
    }

    public static int get(ModelProvider<?> provider) {
        return MODEL_PROVIDERS.indexOf(provider);
    }

    public static ModelProvider<?> get(int id) {
        return id >= 0 && id < MODEL_PROVIDERS.size() ? MODEL_PROVIDERS.get(id) : null;
    }

    public static int get(TextureProvider<?> provider) {
        return TEXTURE_PROVIDERS.indexOf(provider);
    }

    public static TextureProvider<?> getTexture(int id) {
        return id >= 0 && id < TEXTURE_PROVIDERS.size() ? TEXTURE_PROVIDERS.get(id) : null;
    }

    public static List<ModelProvider<?>> getModelProviders() {
        return MODEL_PROVIDERS;
    }

    public static List<TextureProvider<?>> getTextureProviders() {
        return TEXTURE_PROVIDERS;
    }
}
