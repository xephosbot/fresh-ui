package com.xbot.ui.client

import com.xbot.ui.client.utils.GuiAtlasManager
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.resource.ResourceManagerHelper
import net.minecraft.client.MinecraftClient
import net.minecraft.resource.ResourceType

object FreshUIClient : ClientModInitializer {

    lateinit var guiAtlasManager: GuiAtlasManager
        private set

    override fun onInitializeClient() {
        val textureManager = MinecraftClient.getInstance().textureManager
        if (textureManager != null) {
            guiAtlasManager = GuiAtlasManager(textureManager)
            ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(guiAtlasManager)
        }
    }
}

const val MOD_ID = "fresh-ui"
