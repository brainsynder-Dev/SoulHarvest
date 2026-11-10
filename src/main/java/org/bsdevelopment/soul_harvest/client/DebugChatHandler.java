package org.bsdevelopment.soul_harvest.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.multiplayer.chat.GuiMessage;
import net.minecraft.network.chat.Component;

public class DebugChatHandler {
    private static GuiMessage lastDebugMessage;

    public static void showMessage(Component message) {
        ChatComponent chat = Minecraft.getInstance().gui.getChat();
        if (lastDebugMessage != null && chat.allMessages.remove(lastDebugMessage)) chat.refreshTrimmedMessages();

        chat.addClientSystemMessage(message);
        lastDebugMessage = chat.allMessages.isEmpty() || chat.allMessages.getFirst().content() != message ? null : chat.allMessages.getFirst();
    }
}
