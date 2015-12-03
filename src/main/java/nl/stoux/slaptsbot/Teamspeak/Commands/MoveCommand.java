package nl.stoux.slaptsbot.Teamspeak.Commands;

import com.github.theholywaffle.teamspeak3.api.wrapper.Channel;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import nl.stoux.slaptsbot.Globals;
import nl.stoux.slaptsbot.Teamspeak.Commands.Base.BotCommand;
import nl.stoux.slaptsbot.Teamspeak.Commands.Base.BotCommandException;
import nl.stoux.slaptsbot.Teamspeak.Models.Chat;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Stoux on 14/11/2015.
 */
public class MoveCommand extends BotCommand {

    private final static Options options;
    static {
        options = new Options();
        options.addOption("f", "from", true, "From channels (comma-separated, name of channel or 'Here')");
        options.addOption("t", "to", true, "To channels (defaults to 'Here')");
    }


    public MoveCommand(Chat chat, Client requester) {
        super(chat, requester);
    }

    @Override
    public void handleCommand(String command) throws BotCommandException {
        CommandLine cl = toCLI(options, command);
        if (!hasOption(cl, "f")) {
            throw new BotCommandException("Missing -from flag");
        }

        //From channels
        Set<Integer> fromChannels = parseChannels(cl.getOptionValue("f"));

        //To channel
        int targetChannel = -1;
        if (hasOption(cl, "t")) {
            Set<Integer> toChannels = parseChannels(cl.getOptionValue("t"));
            if (toChannels.size() > 1) {
                throw new BotCommandException("Found multiple channels that matched the names...");
            }
            targetChannel = toChannels.iterator().next();
        } else {
            targetChannel = requester.getChannelId();
        }
        if (fromChannels.contains(targetChannel)) {
            throw new BotCommandException("The target channel is also a origin channel... (from -> to = same)");
        }

        //Move people
        List<Integer> clients = new ArrayList<>();
        for (Client client : Globals.getOverview().getClients()) {
            if (fromChannels.contains(client.getChannelId())) {
                clients.add(client.getId());
            }
        }
        int[] clientArr = new int[clients.size()];
        for (int i = 0; i < clients.size(); i++) {
            clientArr[i] = clients.get(i);
        }
        //=> Execute
        Globals.getMasterBot().getApi().moveClients(clientArr, targetChannel);
    }

    private Set<Integer> parseChannels(String channelString) throws BotCommandException {
        Set<Integer> channels = new HashSet<>();
        String[] split = channelString.split(",");
        for (String ch : split) {
            ch = ch.trim().toLowerCase();
            if (ch.equals("here")) {
                channels.add(requester.getChannelId());
            } else {
                boolean added = false;
                for (Channel channel : Globals.getOverview().getChannels()) {
                    if (channel.getName().toLowerCase().contains(ch.toLowerCase())) {
                        channels.add(channel.getId());
                        added = true;
                        break;
                    }
                }
                if (!added) {
                    throw new BotCommandException("Unknown channel: " + ch);
                }
            }
        }
        if (channels.isEmpty()) {
            throw new BotCommandException("No channels found...");
        }
        return channels;
    }
}
