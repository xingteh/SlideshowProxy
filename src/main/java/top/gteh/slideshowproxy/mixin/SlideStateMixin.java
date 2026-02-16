package top.gteh.slideshowproxy.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import org.apache.http.conn.HttpHostConnectException;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.teacon.slides.renderer.SlideState;
import org.teacon.slides.slide.Slide;
import top.gteh.slideshowproxy.SlideshowProxy;

import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.function.Function;

@Mixin(value = SlideState.class, remap = false)
public class SlideStateMixin {
    @Shadow
    private Slide mSlide;

    @Shadow
    private SlideState.State mState;

    @Shadow
    private int mCounter;

    @Shadow
    @Final
    private static int RETRY_INTERVAL_SECONDS;

    @ModifyArg(
            method = "<init>",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/concurrent/CompletableFuture;exceptionally(Ljava/util/function/Function;)Ljava/util/concurrent/CompletableFuture;"
            ),
            index = 0
    )
    private Function<Throwable, Void> modifyExceptionHandler(Function<Throwable, Void> original) {
        return e -> {
            Throwable cause = e;
            while (cause.getCause() != null) {
                cause = cause.getCause();
            }

            boolean isNetworkTimeout = cause instanceof SocketTimeoutException ||
                    cause instanceof ConnectException ||
                    cause instanceof UnknownHostException ||
                    cause instanceof HttpHostConnectException ||
                    (cause instanceof SocketException &&
                            cause.getMessage() != null &&
                            (cause.getMessage().contains("reset") ||
                                    cause.getMessage().contains("timed out")));

            RenderSystem.recordRenderCall(() -> {
                assert mState == SlideState.State.LOADING;
                mSlide = isNetworkTimeout ? SlideshowProxy.DEFAULT_TIMEOUT : Slide.failed();
                mState = SlideState.State.FAILED;
                mCounter = RETRY_INTERVAL_SECONDS;
            });

            return null;
        };
    }
}
