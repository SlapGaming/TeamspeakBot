package nl.stoux.slaptsbot.Teamspeak.Commands.Base;

import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import nl.stoux.slaptsbot.Teamspeak.Models.Chat;

/**
 * Created by Stoux on 14/11/2015.
 */
@FunctionalInterface
public interface CommandConstructor {

    BotCommand construct(Chat chat, Client requester);

}
