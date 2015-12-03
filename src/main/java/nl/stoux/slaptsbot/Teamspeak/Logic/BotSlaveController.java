package nl.stoux.slaptsbot.Teamspeak.Logic;

import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import nl.stoux.slaptsbot.Globals;
import nl.stoux.slaptsbot.Teamspeak.Bots.ChannelBot;
import nl.stoux.slaptsbot.Teamspeak.Models.ServerOverview;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Stoux on 29/10/2015.
 */
public class BotSlaveController {

    private static final int LOBBY_ID = 4;

    private Map<Integer, ChannelBot> channelToBot;
    private Map<Integer, Integer> channelToClients;

    public BotSlaveController() {
        channelToBot = new ConcurrentHashMap<>();
        channelToClients = new ConcurrentHashMap<>();
    }

    public void fullCheck() {
        calculateClients();
        checkBots();
    }

    private void calculateClients() {
        channelToClients.clear();
        channelToClients.put(LOBBY_ID, 0);
        ServerOverview overview = Globals.getOverview();
        for (Client client : overview.getClients()) {
            int chid = client.getChannelId();
            int current = (channelToClients.containsKey(chid) ? channelToClients.get(chid) : 0);
            channelToClients.put(chid, ++current);
        }
    }

    private void checkBots() {
        List<ChannelBot> idleBots = new ArrayList<>();

        //Check for leftover bots
        HashSet<Integer> channels = new HashSet<>(channelToClients.keySet());
        for (Integer channelId : new HashSet<>(channelToBot.keySet())) {
            if (channels.contains(channelId)) {
                //Channel is still active and running
                channels.remove(channelId);
            } else {
                //Empty channel with a bot in it
                ChannelBot bot = channelToBot.remove(channelId);
                idleBots.add(bot);
            }
        }

        //Check for channels without bots
        for (Integer channelId : channels) {
            //Add the bot
            ChannelBot bot;
            if (!idleBots.isEmpty()) {
                bot = idleBots.remove(0);
                bot.moveToChannel(channelId);
                channelToBot.put(channelId, bot);
            } else {
                new Thread(() -> {
                    ChannelBot newBot = new ChannelBot(channelId);
                    channelToBot.put(channelId, newBot);
                }).start();
            }
        }

        //Discontinue left over bots
        if (!idleBots.isEmpty()) {
            for (ChannelBot idleBot : idleBots) {
                idleBot.disconnect();
            }
        }
    }

    public void onClientMove(int fromChannel, int toChannel) {
        Optional<ChannelBot> idleBot = removeFromChannel(fromChannel);
        boolean botUsed = addToChannel(toChannel, idleBot);

        //Discontinue bot if leftover
        if (idleBot.isPresent() && !botUsed) {
            idleBot.get().disconnect();
        }
    }

    public void onClientJoin(int joinedChannel) {
        addToChannel(joinedChannel, Optional.empty());
    }

    public void onClientLeave(int leftChannel) {
        Optional<ChannelBot> idleBot = removeFromChannel(leftChannel);
        if (idleBot.isPresent()) {
            idleBot.get().disconnect();
        }
    }

    private Optional<ChannelBot> removeFromChannel(int fromChannel) {
        System.out.println("[SLAVE] Removing 1 from: " + fromChannel);
        int clients = channelToClients.get(fromChannel) - 1;
        if (clients == 0 && fromChannel != LOBBY_ID) {
            channelToClients.remove(fromChannel);
            return Optional.of(channelToBot.remove(fromChannel));
        } else {
            channelToClients.put(fromChannel, clients);
            return Optional.empty();
        }
    }

    private boolean addToChannel(int toChannel, Optional<ChannelBot> idleBot) {
        System.out.println("[SLAVE] Adding 1 to: " + toChannel);
        //Check the new channel
        boolean botUsed = false;
        int newChannel = (channelToClients.containsKey(toChannel) ? channelToClients.get(toChannel) : 0) + 1;
        if (newChannel == 1 && toChannel != LOBBY_ID) {
            //Requires a bot
            ChannelBot newBot;
            if (idleBot.isPresent()) {
                newBot = idleBot.get();
                newBot.moveToChannel(toChannel);
                botUsed = true;
            } else {
                newBot = new ChannelBot(toChannel);
            }
            channelToBot.put(toChannel, newBot);
        }
        channelToClients.put(toChannel, newChannel);
        return botUsed;
    }


}
