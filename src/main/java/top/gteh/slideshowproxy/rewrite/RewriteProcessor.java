package top.gteh.slideshowproxy.rewrite;

import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import org.teacon.slides.config.Config;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RewriteProcessor {
    public static Triple<String, String, String> resolve(String url, List<RewriteRule> rules, String default_ua, String default_referer) {
        if (!Config.isProxySwitch()) {
            for (RewriteRule rule : rules) {
                for (Pattern pattern : rule.compiledPatterns) {
                    Matcher m = pattern.matcher(url);
                    if (m.matches()) {
                        return new ImmutableTriple<>(replaceGroups(rule.target, m), getValue(rule.ua, default_ua), getValue(rule.referer, default_referer));
                    }
                }
            }
        }
        return new ImmutableTriple<>(url, default_ua, default_referer);
    }

    private static String getValue(String value, String default_value) {
        return switch (value) {
            case "DEFAULT" -> default_value;
            case "EMPTY" -> "";
            case "NULL" -> null;
            default -> value;
        };
    }

    private static String replaceGroups(String template, Matcher m) {
        String result = template;
        for (int i = 1; i <= m.groupCount(); i++) {
            String value = m.group(i);
            if (value != null) {
                result = result.replace("{$" + i + "}", value);
            }
        }
        return result;
    }
}
