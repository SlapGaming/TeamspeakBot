package nl.stoux.slaptsbot.Teamspeak.Commands.Base;

import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import nl.stoux.slaptsbot.Teamspeak.Commands.MoveCommand;
import nl.stoux.slaptsbot.Teamspeak.Models.Chat;
import nl.stoux.slaptsbot.Util;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;

import java.util.*;

/**
 * Created by Stoux on 14/11/2015.
 */
public abstract class BotCommand {

    /** Map with Command -> Constructor */
    private static final Map<String, CommandConstructor> commandMap;
    static {
        commandMap = new HashMap<>();
        commandMap.put("move", MoveCommand::new);
    }
    public static CommandConstructor getCommand(String command) {
        return commandMap.get(command);
    }

    protected Chat chat;
    protected Client requester;

    public BotCommand(Chat chat, Client requester) {
        this.chat = chat;
        this.requester = requester;
    }

    public abstract void handleCommand(String command) throws BotCommandException;

    /**
     * Check if this command has pending answers/commands to go through
     * @return pending
     */
    public boolean hasPendingAnswer() {
        return false;
    }

    protected void send(String message) {
        Util.sendPrivateMessage(requester, message);
    }

    protected CommandLine toCLI(Options options, String message) {
        CommandLineParser parser = new DefaultParser();
        try {
            return parser.parse(options, new String[]{message});
        } catch (Exception e) {
            return null;
        }
    }

    protected boolean hasOption(CommandLine cl, String flag) {
        return (cl.hasOption(flag) && cl.getOptionValue(flag) != null);
    }

}
