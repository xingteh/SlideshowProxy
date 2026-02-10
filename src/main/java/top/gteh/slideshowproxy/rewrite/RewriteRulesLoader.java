package top.gteh.slideshowproxy.rewrite;

import com.google.gson.Gson;
import top.gteh.slideshowproxy.SlideshowProxy;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class RewriteRulesLoader {
    private static final Gson GSON = new Gson();
    private static final HttpClient CLIENT = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.ALWAYS)
            .build();

    public static List<RewriteRule> load(String url) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            SlideshowProxy.LOGGER.warn("Failed to load Slide Show Proxy rewrite rules");
            return List.of();
        }

        RewriteConfig config = GSON.fromJson(response.body(), RewriteConfig.class);

        if (config == null || config.rules == null) {
            SlideshowProxy.LOGGER.warn("Slide Show Proxy rewrite config is empty or invalid!");
            return List.of();
        }

        if (config.version > 1) {
            SlideshowProxy.LOGGER.warn("Can't load Slide Show Proxy rewrite config");
            return List.of();
        }

        List<RewriteRule> rules = config.rules;

        compileRules(rules);
        return rules;
    }

    private static void compileRules(List<RewriteRule> rules) {
        for (RewriteRule rule : rules) {
            rule.compiledPatterns = new ArrayList<>();
            for (String p : rule.patterns) {
                rule.compiledPatterns.add(Pattern.compile(p, Pattern.CASE_INSENSITIVE));
            }
        }
    }

    private class RewriteConfig {
        public int version;
        public List<RewriteRule> rules;
    }
}
