package top.gteh.slideshowproxy.mixin;

import org.apache.commons.lang3.tuple.Triple;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.teacon.slides.cache.ImageCache;
import org.teacon.slides.http.client.cache.HttpCacheContext;
import top.gteh.slideshowproxy.rewrite.RewriteProcessor;
import top.gteh.slideshowproxy.SlideshowProxy;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.net.URI;

@Mixin(value = ImageCache.class, remap = false)
public abstract class ImageCacheMixin {
    @Shadow()
    @Final
    private CloseableHttpClient mHttpClient;

    @Shadow()
    @Final
    private static String DEFAULT_USER_AGENT;

    @Shadow()
    @Final
    private static String DEFAULT_REFERER;

    @Inject(method = "createResponse", at = @At("HEAD"), cancellable = true)
    private void createResponse(URI location, HttpCacheContext context, boolean online, CallbackInfoReturnable<CloseableHttpResponse> cir) throws IOException {
        Triple<String, String, String> url = RewriteProcessor.resolve(location.toASCIIString(), SlideshowProxy.REWRITE_RULES, DEFAULT_USER_AGENT, DEFAULT_REFERER);
        HttpGet request = new HttpGet(url.getLeft());
        if (url.getRight() != null) {
            request.addHeader("Referer", url.getRight());
        }
        if (url.getMiddle() != null) {
            request.addHeader("User-Agent", url.getMiddle());
        }
        request.addHeader("Accept", String.join(", ", ImageIO.getReaderMIMETypes()));
        if (!online) {
            request.addHeader("Cache-Control", "max-stale=2147483647");
            request.addHeader("Cache-Control", "only-if-cached");
        } else {
            request.addHeader("Cache-Control", "must-revalidate");
        }

        cir.setReturnValue(mHttpClient.execute(request, context));
    }
}
