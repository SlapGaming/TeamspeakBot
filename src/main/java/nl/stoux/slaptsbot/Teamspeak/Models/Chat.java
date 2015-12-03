package nl.stoux.slaptsbot.Teamspeak.Models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import nl.stoux.slaptsbot.Teamspeak.Commands.Base.BotCommand;

import java.util.ArrayList;
import java.util.Optional;

/**
 * Created by Stoux on 29/10/2015.
 */
public class Chat {

    private String uuid;
    private ArrayList<ChatMessage> messages;

    /** The last command used */
    @Getter @Setter
    private Optional<? extends BotCommand> lastCommand;

    public Chat(String uuid) {
        this.uuid = uuid;
        messages = new ArrayList<>();
    }

    /**
     * Add a chat message
     * @param fromBot
     * @param message
     */
    public void addChatMessage(boolean fromBot, String message) {
        messages.add(new ChatMessage(fromBot, System.currentTimeMillis(), message));
    }

    @AllArgsConstructor
    private class ChatMessage {
        private boolean fromBot;
        private long sendTime;
        private String message;
    }

}
