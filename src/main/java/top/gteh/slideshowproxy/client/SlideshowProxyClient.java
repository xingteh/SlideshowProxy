package top.gteh.slideshowproxy.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.network.chat.Component;
import top.gteh.slideshowproxy.SlideshowProxy;

public class SlideshowProxyClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, bContext) -> dispatcher.register(ClientCommandManager.literal(SlideshowProxy.MOD_ID)
                .then(ClientCommandManager.literal("reload")
                        .executes(context -> {
                            ClientConfig.load();
                            SlideshowProxy.reloadConfig(ClientConfig.remoteConfigUrl);
                            context.getSource().sendFeedback(Component.literal("Reloaded Slide Show Proxy rewrite config."));
                            return 1;
                        }))));
    }
}
