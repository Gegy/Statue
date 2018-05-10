package net.gegy1000.statue.client.model.game;

import net.gegy1000.statue.client.ClientProxy;
import net.ilexiconn.llibrary.client.model.qubble.QubbleCuboid;
import net.ilexiconn.llibrary.client.model.qubble.QubbleModel;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.PositionTextureVertex;
import net.minecraft.client.model.TextureOffset;
import net.minecraft.client.model.TexturedQuad;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ProgressManager;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public enum GameModelLoader {
    INSTANCE;

    private final Map<String, QubbleModel> models = new HashMap<>();
    private final Map<String, ResourceLocation> textures = new HashMap<>();

    private Field textureQuadsField;
    private Method getEntityTextureMethod;

    public void load() {
        if (!Loader.isModLoaded("qubble")) {
            for (Field field : ModelBox.class.getDeclaredFields()) {
                if (field.getType() == TexturedQuad[].class) {
                    field.setAccessible(true);
                    this.textureQuadsField = field;
                    break;
                }
            }
            for (Method method : Render.class.getDeclaredMethods()) {
                if (method.getReturnType().equals(ResourceLocation.class) && Modifier.isAbstract(method.getModifiers()) && method.getParameterTypes().length == 1) {
                    method.setAccessible(true);
                    this.getEntityTextureMethod = method;
                    break;
                }
            }
            Map<Class<? extends Entity>, Render<? extends Entity>> map = ClientProxy.MINECRAFT.getRenderManager().entityRenderMap;
            ProgressManager.ProgressBar bar = ProgressManager.push("Parsing entity models", map.size());
            for (Map.Entry<Class<? extends Entity>, Render<? extends Entity>> entry : map.entrySet()) {
                Render<? extends Entity> renderer = entry.getValue();
                String entityName = entry.getKey().getSimpleName().replaceAll("Entity", "");
                bar.step(entityName);
                Entity entity = null;
                try {
                    entity = entry.getKey().getConstructor(World.class).newInstance(new DummyWorld());
                    entityName = entity.getName();
                    if (entityName.startsWith("entity.") && entityName.endsWith(".name")) {
                        entityName = entityName.split("entity.")[1].split(".name")[0];
                    }
                } catch (Exception e) {
                }
                for (Field field : this.getAllFields(renderer.getClass())) {
                    try {
                        if (ModelBase.class.isAssignableFrom(field.getType())) {
                            field.setAccessible(true);
                            QubbleModel model = this.parseModel((ModelBase) field.get(renderer), entry.getKey(), entityName);
                            if (model.getCuboids().size() > 0) {
                                this.models.put(entityName, model);
                            }
                        } else if (ResourceLocation[].class.isAssignableFrom(field.getType()) && !this.textures.containsKey(entityName)) {
                            field.setAccessible(true);
                            ResourceLocation[] textures = (ResourceLocation[]) field.get(renderer);
                            if (textures.length > 0) {
                                ResourceLocation texture = textures[0];
                                if (!texture.toString().contains("shadow")) {
                                    this.textures.put(entityName, texture);
                                }
                            }
                        } else if (ResourceLocation.class.isAssignableFrom(field.getType()) && !this.textures.containsKey(entityName)) {
                            field.setAccessible(true);
                            ResourceLocation texture = (ResourceLocation) field.get(renderer);
                            if (texture != null && !texture.toString().contains("shadow")) {
                                this.textures.put(entityName, texture);
                            }
                        }
                    } catch (Exception e) {
                        System.err.println("Failed to load model from " + renderer.getClass() + "#" + field.getName());
                        e.printStackTrace();
                    }
                }
                if (entity != null) {
                    try {
                        ResourceLocation texture = (ResourceLocation) this.getEntityTextureMethod.invoke(renderer, entity);
                        if (texture != null) {
                            this.textures.put(entityName, texture);
                        }
                    } catch (Exception e) {
                    }
                }
            }
            ProgressManager.pop(bar);
        }
    }

    private List<Field> getAllFields(Class<?> clazz) {
        List<Field> fields = new LinkedList<>();
        Collections.addAll(fields, clazz.getDeclaredFields());
        if (clazz.getSuperclass() != null && clazz.getSuperclass() != Object.class) {
            fields.addAll(this.getAllFields(clazz.getSuperclass()));
        }
        return fields;
    }

    private QubbleModel parseModel(ModelBase model, Class<?> clazz, String name) {
        QubbleModel qubbleModel = QubbleModel.create(name, "Unknown", model.textureWidth, model.textureHeight);
        if (clazz != null && Entity.class.isAssignableFrom(clazz)) {
            try {
                Entity entity = (Entity) clazz.getConstructor(World.class).newInstance(new DummyWorld());
                try {
                    model.setRotationAngles(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F, entity);
                } catch (Exception e) {
                }
                if (entity instanceof EntityLivingBase) {
                    try {
                        model.setLivingAnimations((EntityLivingBase) entity, 0.0F, 0.0F, 0.0F);
                    } catch (Exception e) {
                    }
                }
                try {
                    model.render(entity, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
                } catch (Exception e) {
                }
            } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            }
        }
        Map<String, ModelRenderer> cuboidsWithNames = this.getCuboidsWithNames(model);
        for (Map.Entry<String, ModelRenderer> entry : cuboidsWithNames.entrySet()) {
            ModelRenderer modelRenderer = entry.getValue();
            if (modelRenderer != null && modelRenderer.cubeList != null) {
                qubbleModel.setTextureWidth((int) modelRenderer.textureWidth);
                qubbleModel.setTextureHeight((int) modelRenderer.textureHeight);
                break;
            }
        }
        for (Map.Entry<String, ModelRenderer> entry : cuboidsWithNames.entrySet()) {
            qubbleModel.getCuboids().addAll(this.parseModelRenderer(model, qubbleModel, entry.getKey(), entry.getValue(), null));
        }
        return qubbleModel;
    }

    private List<QubbleCuboid> parseModelRenderer(ModelBase model, QubbleModel qubbleModel, String name, ModelRenderer modelRenderer, QubbleCuboid parent) {
        List<QubbleCuboid> cubes = new ArrayList<>();
        int boxIndex = 0;
        if (modelRenderer != null && modelRenderer.cubeList != null) {
            for (ModelBox box : modelRenderer.cubeList) {
                float textureWidth = qubbleModel.getTextureWidth();
                float textureHeight = qubbleModel.getTextureHeight();
                if (modelRenderer.textureWidth != 64 || modelRenderer.textureHeight != 32) {
                    textureWidth = modelRenderer.textureWidth;
                    textureHeight = modelRenderer.textureHeight;
                }
                QubbleCuboid cube = QubbleCuboid.create((modelRenderer.boxName != null ? modelRenderer.boxName : name) + (boxIndex != 0 ? box.boxName != null ? box.boxName : "_" + boxIndex : ""));
                cube.setPosition(modelRenderer.rotationPointX, modelRenderer.rotationPointY, modelRenderer.rotationPointZ);
                cube.setRotation((float) Math.toDegrees(modelRenderer.rotateAngleX), (float) Math.toDegrees(modelRenderer.rotateAngleY), (float) Math.toDegrees(modelRenderer.rotateAngleZ));
                cube.setOffset(box.posX1, box.posY1, box.posZ1);
                cube.setDimensions((int) Math.abs(box.posX2 - box.posX1), (int) Math.abs(box.posY2 - box.posY1), (int) Math.abs(box.posZ2 - box.posZ1));
                TextureOffset textureOffset = model.getTextureOffset(box.boxName);
                if (textureOffset != null) {
                    cube.setTexture(textureOffset.textureOffsetX, textureOffset.textureOffsetY);
                } else {
                    TexturedQuad[] quads = this.getTexturedQuads(box);
                    if (quads != null) {
                        PositionTextureVertex[] vertices = quads[1].vertexPositions;
                        cube.setTextureMirrored((vertices[2].vector3D.y - vertices[0].vector3D.y - cube.getDimensionY()) / 2.0F < 0.0F);
                        if (vertices[cube.isTextureMirrored() ? 2 : 1].texturePositionY > vertices[cube.isTextureMirrored() ? 1 : 2].texturePositionY) {
                            cube.setTextureMirrored(!cube.isTextureMirrored());
                        }
                        cube.setTexture((int) (vertices[cube.isTextureMirrored() ? 2 : 1].texturePositionX * textureWidth), (int) ((vertices[cube.isTextureMirrored() ? 2 : 1].texturePositionY * textureHeight) - cube.getDimensionZ()));
                    }
                }
                boxIndex++;
                cubes.add(cube);
            }
            if (cubes.size() > 0 && modelRenderer.childModels != null) {
                int i = 0;
                for (ModelRenderer child : modelRenderer.childModels) {
                    this.parseModelRenderer(model, qubbleModel, child.boxName != null ? child.boxName : name + "_" + i, child, cubes.get(0));
                    i++;
                }
            }
        }
        if (parent != null) {
            parent.getChildren().addAll(cubes);
        }
        return cubes;
    }

    private Map<String, ModelRenderer> getCuboidsWithNames(ModelBase model) {
        Map<String, ModelRenderer> cuboids = new HashMap<>();
        for (Field field : this.getAllFields(model.getClass())) {
            try {
                if (ModelRenderer.class.isAssignableFrom(field.getType())) {
                    field.setAccessible(true);
                    ModelRenderer modelRenderer = (ModelRenderer) field.get(model);
                    if (modelRenderer != null) {
                        cuboids.put(field.getName(), modelRenderer);
                    }
                } else if (ModelRenderer[].class.isAssignableFrom(field.getType())) {
                    field.setAccessible(true);
                    ModelRenderer[] boxes = (ModelRenderer[]) field.get(model);
                    if (boxes != null) {
                        for (int i = 0; i < boxes.length; i++) {
                            cuboids.put(field.getName() + "_" + i, boxes[i]);
                        }
                    }
                } else if (List.class.isAssignableFrom(field.getType())) {
                    if (field.getDeclaringClass() != ModelBase.class) {
                        field.setAccessible(true);
                        List boxes = (List) field.get(model);
                        if (boxes != null) {
                            for (int i = 0; i < boxes.size(); i++) {
                                Object obj = boxes.get(i);
                                if (obj instanceof ModelRenderer) {
                                    cuboids.put(field.getName() + "_" + i, (ModelRenderer) obj);
                                }
                            }
                        }
                    }
                }
            } catch (IllegalAccessException e) {
            }
        }
        return cuboids;
    }

    private TexturedQuad[] getTexturedQuads(ModelBox box) {
        try {
            return (TexturedQuad[]) this.textureQuadsField.get(box);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Map<String, QubbleModel> getModels() {
        if (!Loader.isModLoaded("qubble")) {
            return this.models;
        }
        try {
            Class<?> qubbleProxy = Class.forName("net.ilexiconn.qubble.client.ClientProxy");
            return (Map<String, QubbleModel>) qubbleProxy.getDeclaredField("GAME_MODELS").get(null);
        } catch (Exception e) {
            return this.models;
        }
    }

    public Map<String, ResourceLocation> getTextures() {
        if (!Loader.isModLoaded("qubble")) {
            return this.textures;
        }
        try {
            Class<?> qubbleProxy = Class.forName("net.ilexiconn.qubble.client.ClientProxy");
            return (Map<String, ResourceLocation>) qubbleProxy.getDeclaredField("GAME_TEXTURES").get(null);
        } catch (Exception e) {
            return this.textures;
        }
    }
}
