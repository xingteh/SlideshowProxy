package top.gteh.slideshowproxy;

import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.gteh.slideshowproxy.rewrite.RewriteRule;
import top.gteh.slideshowproxy.rewrite.RewriteRulesLoader;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class SlideshowProxy implements ModInitializer {
    public static final String MOD_ID = "slideshowproxy";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    private static final String REWRITE_CONFIG_URL = "https://gitea.dusays.com/fenychn0206/lps-community-guide/raw/branch/main/docs/assets/gameresources/rewrite_config.json";
    public static volatile List<RewriteRule> REWRITE_RULES = Collections.emptyList();

    @Override
    public void onInitialize() {
        reloadConfig();
    }

    public static void reloadConfig() {
        CompletableFuture.runAsync(() -> {
            try {
                REWRITE_RULES = RewriteRulesLoader.load(REWRITE_CONFIG_URL);
                LOGGER.info("Loaded " + REWRITE_RULES.size() + " Slide Show Proxy rewrite rules");
            } catch (Exception e) {
                LOGGER.warn("Failed to load Slide Show Proxy rewrite rules:");
                e.printStackTrace();
            }
        });
    }
}
