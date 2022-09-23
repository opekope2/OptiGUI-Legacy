package opekope2.optigui.interfaces;

import java.util.Properties;
import java.util.Set;

import net.minecraft.util.Identifier;

public interface TextureRemapper {
    public Set<Identifier> remap(Properties properties);
}
