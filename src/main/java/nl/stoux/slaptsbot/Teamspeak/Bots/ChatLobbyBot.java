package nl.stoux.slaptsbot.Teamspeak.Bots;

import com.github.theholywaffle.teamspeak3.api.TextMessageTargetMode;
import com.github.theholywaffle.teamspeak3.api.event.TS3EventType;
import com.github.theholywaffle.teamspeak3.api.event.TextMessageEvent;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Created by Stoux on 29/10/2015.
 */
public class ChatLobbyBot extends AbstractBot {

    /** List of available Lobby IDs */
    private final static Deque<Integer> availableIds = new ConcurrentLinkedDeque<>(Arrays.asList(1,2,3,4,5,6,7,8,9,10));

    /**
     * Create (and connect) a new ChatLobby
     * @return the lobby or empty if all lobby IDs are taken
     */
    public static Optional<ChatLobbyBot> newChatLobby() {
        if (availableIds.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(new ChatLobbyBot(availableIds.pop()));
        }
    }

    private ChatLobbyBot(int id) {
        super(createTS3Config("groupchat-user", "groupchat-pass"), "Chat Lobby " + id); //TODO: Add config
        api.registerEvent(TS3EventType.TEXT_PRIVATE);
        api.addTS3Listeners(this);
    }

    @Override
    public void onTextMessage(TextMessageEvent e) {
        if (e.getTargetMode() == TextMessageTargetMode.CLIENT) {
            //TODO: Do stuff
        }
    }

}
