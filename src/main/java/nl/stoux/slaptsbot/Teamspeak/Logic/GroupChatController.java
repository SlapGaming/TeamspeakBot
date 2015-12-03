package nl.stoux.slaptsbot.Teamspeak.Logic;

import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import com.google.common.eventbus.Subscribe;
import nl.stoux.slaptsbot.Globals;
import nl.stoux.slaptsbot.Teamspeak.Commands.Base.BotCommand;
import nl.stoux.slaptsbot.Teamspeak.Commands.Base.BotCommandException;
import nl.stoux.slaptsbot.Teamspeak.Commands.Base.CommandConstructor;
import nl.stoux.slaptsbot.Teamspeak.Events.BotCalledEvent;
import nl.stoux.slaptsbot.Teamspeak.Interfaces.ChatSender;
import nl.stoux.slaptsbot.Teamspeak.Models.Chat;
import nl.stoux.slaptsbot.Util;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by Stoux on 29/10/2015.
 */
public class GroupChatController {

    //TODO: Possible actions
    //TODO: 0. !help | List of possible actions
    //TODO: 1. !seen {User} | Last seen time of a user
    //TODO: 2. !move from {Channels/here} to {Channel/Here} | Move all users in X channels to 1 Channel
    //TODO: 3. !serveradmin | Let the server assign you ServerAdmin. Only if verified to do this (keep in DB)
    //TODO: 4. !poke {user} /w {message} | Let users poke other users X (prob 1) times a day
    //TODO: 5. !mail {user} {message}
    //TODO: 6. !automove {channel} | Configure the bot to auto move a certain user to a certain channel
    //TODO: 7. !groupchat {users} | Start a group chat with multiple users
    //TODO:     8. !users | See who is in this chat
    //TODO:     9. !adduser {user} | Add a user to the chat
    //TODO:     10. !kickuser {user} | Kick a user from the chat
    //TODO:     11. !lobbies | Get a list of chat lobbies + members in them
    //TODO:     12. !spyon {lobby} | Receive all chat that's send in a certain lobby
    //TODO:     13. !
    //TODO:     14. !
    //TODO:     15. !

    private ChatSender chatSender;
    private Map<String, Chat> uuidToChat;

    public GroupChatController(ChatSender chatSender) {
        this.chatSender = chatSender;
        uuidToChat = new HashMap<>();
        Globals.getEventBus().register(this);
    }

    public void onMessageReceived(String uuid, String message) {
        Chat chat = getChat(uuid);
        chat.addChatMessage(false, message);
        Client client = Util.getClient(uuid);


        //TODO: Check if any pending awnsers left

        try {
            //Parse the command
            if (!message.substring(0, 1).equals("!")) {
                throw new BotCommandException("Unknown command. See !help for all commands.");
            }


            String[] split = message.substring(1).split(" ", 2);
            CommandConstructor cc = BotCommand.getCommand(split[0].toLowerCase());
            if (cc == null) {
                throw new BotCommandException("Unknown command. See !help for all commands.");
            }

            BotCommand command = cc.construct(chat, client);
            command.handleCommand(split.length > 1 ? split[1] : "");
        } catch (BotCommandException e) {
            Util.sendPrivateMessage(client, "Error: " + e.getMessage());
        }
    }


    public void sendMessage(String uuid, String message) {
        Chat chat = getChat(uuid);
        chat.addChatMessage(true, message);
        chatSender.sendChat(uuid, message);
    }

    public void killChat(String uuid) {
        uuidToChat.remove(uuid);
    }

    private Chat getChat(String uuid) {
        Chat chat = uuidToChat.get(uuid);
        if (chat == null) {
            uuidToChat.put(uuid, chat = new Chat(uuid));
        }
        return chat;
    }


    /* EVENTS */
    @Subscribe
    public void onBotCalled(BotCalledEvent event) {
        sendMessage(event.getCalledByUniqueUserId(), "Hi there! You rang?");
    }

}
