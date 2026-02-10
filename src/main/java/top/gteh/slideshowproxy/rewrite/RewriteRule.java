package top.gteh.slideshowproxy.rewrite;

import java.util.List;
import java.util.regex.Pattern;

public class RewriteRule {
    public List<String> patterns;
    public String target;
    public String ua = "DEFAULT";
    public String referer = "DEFAULT";
    public transient List<Pattern> compiledPatterns;
}
