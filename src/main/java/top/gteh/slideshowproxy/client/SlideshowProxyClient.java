package top.gteh.slideshowproxy.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import org.teacon.slides.config.Config;
import top.gteh.slideshowproxy.SlideshowProxy;

public class SlideshowProxyClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, bContext) -> dispatcher.register(ClientCommandManager.literal(SlideshowProxy.MOD_ID)
                .then(ClientCommandManager.literal("reload")
                        .executes(context -> {
                            ClientConfig.load();
                            SlideshowProxy.reloadConfig(ClientConfig.remoteConfigUrl);
                            context.getSource().sendFeedback(Component.translatable("commands.slideshowproxy.reloaded"));
                            return 1;
                        }))
                .then(ClientCommandManager.literal("disableParentProxy")
                        .executes(context -> {
                            if (Config.isProxySwitch()) {
                                Config.setProxySwitch(false);
                                Config.saveToFile();
                            }
                            Config.refreshProperties();
                            context.getSource().sendFeedback(Component.translatable("commands.slideshowproxy.disable"));
                            return 1;
                        }))));
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            if (Config.isProxySwitch() && client.player != null) {
                client.player.sendSystemMessage(Component.translatable("gui.slideshowproxy.warn")
                        .withStyle(ChatFormatting.YELLOW)
                        .append(Component.translatable("gui.slideshowproxy.disable_slideshow_proxy")
                                .withStyle(style ->
                                        style.withColor(ChatFormatting.AQUA)
                                                .withBold(true)
                                                .withClickEvent(new ClickEvent(
                                                        ClickEvent.Action.RUN_COMMAND,
                                                        "/slideshowproxy disableParentProxy")
                                                )
                                )
                        )
                );
            }
        });
    }
}
