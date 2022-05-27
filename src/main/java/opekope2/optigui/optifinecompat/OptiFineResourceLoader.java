package opekope2.optigui.optifinecompat;

import java.io.IOException;
import java.util.Properties;

import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import opekope2.optigui.Replacer;

public final class OptiFineResourceLoader implements SimpleSynchronousResourceReloadListener {
    public static final Identifier ID = new Identifier("optigui", "optifine_resource_loader");

    @Override
    public Identifier getFabricId() {
        return ID;
    }

    @Override
    public void reload(ResourceManager manager) {
        Replacer.instance.clear();
        for (Identifier id : manager.findResources("optifine/gui/container", path -> path.endsWith(".properties"))) {
            try {
                ResourceLoadContext ctx = new ResourceLoadContext(manager, id);
                OptiFineProperties props = OptiFineProperties.parse(ctx);
                Replacer.instance.add(props);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static final class ResourceLoadContext {
        private ResourceManager resourceManager;
        private Identifier resourceId;
        private Properties properties;

        private ResourceLoadContext(ResourceManager resourceManager, Identifier resourceId) {
            this.resourceManager = resourceManager;
            this.resourceId = resourceId;
        }

        public Identifier findResource(Identifier id) {
            if (id == null) {
                return null;
            }

            if (resourceManager.containsResource(id)) {
                return id;
            }
            
            String namespace = id.getNamespace(), path = id.getPath();

            id = new Identifier(namespace, path + ".png");
            if (resourceManager.containsResource(id)) {
                return id;
            }

            return null;
        }

        public Identifier getResourceId() {
            return resourceId;
        }

        public Resource getResource() throws IOException {
            return resourceManager.getResource(resourceId);
        }

        public Properties getProperties() {
            return properties;
        }

        public void setProperties(Properties properties) {
            this.properties = properties;
        }
    }
}
