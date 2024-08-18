package com.xbot.ui.client.mixin

import com.xbot.ui.client.screen.ExampleScreen
import net.minecraft.client.MinecraftClient
import net.minecraft.client.RunArgs
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.screen.TitleScreen
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Suppress("NonJavaMixin")
@Mixin(MinecraftClient::class)
class FreshUIMixin {
    @Inject(method = ["setScreen"], at = [At("HEAD")], cancellable = true)
    private fun setScreen(screen: Screen, callbackInfo: CallbackInfo) {
        if (screen is TitleScreen) {
            MinecraftClient.getInstance().setScreen(ExampleScreen())
            callbackInfo.cancel()
        }
    }
}
