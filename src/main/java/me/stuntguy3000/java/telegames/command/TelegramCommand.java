package me.stuntguy3000.java.telegames.command;

import lombok.Getter;
import me.stuntguy3000.java.telegames.Telegames;
import me.stuntguy3000.java.telegames.hook.TelegramHook;
import pro.zackpollard.telegrambot.api.chat.Chat;
import pro.zackpollard.telegrambot.api.chat.message.send.SendableMessage;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;

public abstract class TelegramCommand {
    @Getter
    private final Telegames instance;
    @Getter
    private final String name;
    @Getter
    private final String description;

    public TelegramCommand(Telegames instance, String name, String description) {
        this.instance = instance;
        this.name = name;
        this.description = description;

        instance.getCommandHandler().registerCommand(this);
    }

    public abstract void processCommand(CommandMessageReceivedEvent event);

    public void respond(Chat chat, SendableMessage message) {
        chat.sendMessage(message, TelegramHook.getBot());
    }

    public void respond(Chat chat, String message) {
        chat.sendMessage(message, TelegramHook.getBot());
    }

    public void respond(Chat c, String s, Object... format) {
        String msg = String.format(s, format);
        respond(c, msg);
    }

    public String createBotFatherString() {
        return String.format("%s - %s", name, description);
    }
}
