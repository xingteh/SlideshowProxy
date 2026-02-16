package top.gteh.slideshowproxy;

import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.gteh.slideshowproxy.client.ClientConfig;
import top.gteh.slideshowproxy.rewrite.RewriteRule;
import top.gteh.slideshowproxy.rewrite.RewriteRulesLoader;
import top.gteh.slideshowproxy.util.CustomIconSlide;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class SlideshowProxy implements ModInitializer {
    public static final String MOD_ID = "slideshowproxy";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    public static volatile List<RewriteRule> REWRITE_RULES = Collections.emptyList();
    public static final CustomIconSlide DEFAULT_TIMEOUT = new CustomIconSlide(new ResourceLocation(MOD_ID, "textures/gui/slide_icon_timeout.png"));

    @Override
    public void onInitialize() {
        ClientConfig.load();
        reloadConfig(ClientConfig.remoteConfigUrl);
    }

    public static void reloadConfig(String url) {
        CompletableFuture.runAsync(() -> {
            try {
                REWRITE_RULES = RewriteRulesLoader.load(url);
                LOGGER.info("Loaded {} Slide Show Proxy rewrite rules", REWRITE_RULES.size());
            } catch (Exception e) {
                LOGGER.warn("Failed to load Slide Show Proxy rewrite rules:");
                e.printStackTrace();
            }
        });
    }
}
