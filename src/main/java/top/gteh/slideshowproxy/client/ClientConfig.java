package top.gteh.slideshowproxy.client;

import net.fabricmc.loader.api.FabricLoader;
import top.gteh.slideshowproxy.SlideshowProxy;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ClientConfig {
    public static String remoteConfigUrl = "https://gitea.dusays.com/fenychn0206/lps-community-guide/raw/branch/main/docs/assets/gameresources/rewrite_config.json";

    public static void load() {
        Path path = FabricLoader.getInstance().getConfigDir().resolve(SlideshowProxy.MOD_ID + "_remote_config_url.txt");
        if (!Files.isRegularFile(path)) {
            try {
                Files.writeString(path, remoteConfigUrl);
            } catch (IOException e) {
                SlideshowProxy.LOGGER.warn("Failed to save Slide Show Proxy remote config URL");
            }
        }

        try {
            remoteConfigUrl = Files.readString(path);
        } catch (IOException e) {
            SlideshowProxy.LOGGER.warn("Failed to read Slide Show Proxy remote config URL");
        }
    }
}
