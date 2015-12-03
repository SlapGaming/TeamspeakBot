package nl.stoux.slaptsbot.Teamspeak.Commands;

import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import nl.stoux.slaptsbot.Teamspeak.Commands.Base.BotCommand;
import nl.stoux.slaptsbot.Teamspeak.Models.Chat;

/**
 * Created by Stoux on 14/11/2015.
 */
public class HelpCommand extends BotCommand {

    public HelpCommand(Chat chat, Client requester) {
        super(chat, requester);
    }

    @Override
    public void handleCommand(String command) {
        send("Commands:");
        send("Nuthing.");
    }
}
