package top.gteh.slideshowproxy;

import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.gteh.slideshowproxy.client.ClientConfig;
import top.gteh.slideshowproxy.rewrite.RewriteRule;
import top.gteh.slideshowproxy.rewrite.RewriteRulesLoader;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class SlideshowProxy implements ModInitializer {
    public static final String MOD_ID = "slideshowproxy";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    public static volatile List<RewriteRule> REWRITE_RULES = Collections.emptyList();

    @Override
    public void onInitialize() {
        ClientConfig.load();
        reloadConfig(ClientConfig.remoteConfigUrl);
    }

    public static void reloadConfig(String url) {
        CompletableFuture.runAsync(() -> {
            try {
                REWRITE_RULES = RewriteRulesLoader.load(url);
                LOGGER.info("Loaded " + REWRITE_RULES.size() + " Slide Show Proxy rewrite rules");
            } catch (Exception e) {
                LOGGER.warn("Failed to load Slide Show Proxy rewrite rules:");
                e.printStackTrace();
            }
        });
    }
}
