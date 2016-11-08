package net.gegy1000.statue.server.api;

import net.gegy1000.statue.server.provider.game.GameModelProvider;
import net.gegy1000.statue.server.provider.game.GameTextureProvider;
import net.gegy1000.statue.server.provider.qubble.QubbleModelProvider;
import net.gegy1000.statue.server.provider.qubble.QubbleTextureProvider;
import net.gegy1000.statue.server.provider.tabula.TabulaModelProvider;
import net.gegy1000.statue.server.provider.tabula.TabulaTextureProvider;

import java.util.ArrayList;
import java.util.List;

public class ProviderHandler {
    public static final QubbleModelProvider QUBBLE_MODEL_PROVIDER = new QubbleModelProvider();
    public static final QubbleTextureProvider QUBBLE_TEXTURE_PROVIDER = new QubbleTextureProvider();

    public static final TabulaModelProvider TABULA_MODEL_PROVIDER = new TabulaModelProvider();
    public static final TabulaTextureProvider TABULA_TEXTURE_PROVIDER = new TabulaTextureProvider();

    public static final GameModelProvider GAME_MODEL_PROVIDER = new GameModelProvider();
    public static final GameTextureProvider GAME_TEXTURE_PROVIDER = new GameTextureProvider();

    private static final List<ModelProvider<?, ? extends ImportFile>> MODEL_PROVIDERS = new ArrayList<>();
    private static final List<TextureProvider<?, ?>> TEXTURE_PROVIDERS = new ArrayList<>();

    public static void onPreInit() {
        ProviderHandler.register(QUBBLE_MODEL_PROVIDER);
        ProviderHandler.register(QUBBLE_TEXTURE_PROVIDER);

        ProviderHandler.register(TABULA_MODEL_PROVIDER);
        ProviderHandler.register(TABULA_TEXTURE_PROVIDER);

        ProviderHandler.register(GAME_MODEL_PROVIDER);
        ProviderHandler.register(GAME_TEXTURE_PROVIDER);
    }

    public static void register(ModelProvider<?, ? extends ImportFile> provider) {
        MODEL_PROVIDERS.add(provider);
    }

    public static void register(TextureProvider<?, ?> provider) {
        TEXTURE_PROVIDERS.add(provider);
    }

    public static int get(ModelProvider<?, ? extends ImportFile> provider) {
        return MODEL_PROVIDERS.indexOf(provider);
    }

    public static ModelProvider<?, ?> get(int id) {
        return id >= 0 && id < MODEL_PROVIDERS.size() ? MODEL_PROVIDERS.get(id) : null;
    }

    public static int get(TextureProvider<?, ?> provider) {
        return TEXTURE_PROVIDERS.indexOf(provider);
    }

    public static TextureProvider<?, ?> getTexture(int id) {
        return id >= 0 && id < TEXTURE_PROVIDERS.size() ? TEXTURE_PROVIDERS.get(id) : null;
    }

    public static List<ModelProvider<?, ? extends ImportFile>> getModelProviders() {
        return MODEL_PROVIDERS;
    }

    public static List<TextureProvider<?, ?>> getTextureProviders() {
        return TEXTURE_PROVIDERS;
    }
}
