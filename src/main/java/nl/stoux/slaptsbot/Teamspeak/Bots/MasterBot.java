package nl.stoux.slaptsbot.Teamspeak.Bots;

import com.github.theholywaffle.teamspeak3.api.ClientProperty;
import com.github.theholywaffle.teamspeak3.api.TextMessageTargetMode;
import com.github.theholywaffle.teamspeak3.api.event.*;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import nl.stoux.slaptsbot.Globals;
import nl.stoux.slaptsbot.Teamspeak.Interfaces.ChatSender;
import nl.stoux.slaptsbot.Teamspeak.Logic.GroupChatController;
import nl.stoux.slaptsbot.Teamspeak.Logic.BotSlaveController;
import nl.stoux.slaptsbot.Teamspeak.Models.ServerOverview;
import nl.stoux.slaptsbot.Util;

/**
 * Created by Stoux on 29/10/2015.
 */
public class MasterBot extends AbstractBot implements ChatSender {

    private final GroupChatController chatController;
    private final BotSlaveController slaveController;

    public MasterBot() {
        super(createTS3Config("main-user", "main-pass"), NAME);

        refreshServerOverview();
        chatController = new GroupChatController(this);
        slaveController = new BotSlaveController();

        api.registerAllEvents();
        api.addTS3Listeners(this);

        slaveController.fullCheck();
    }

    @Override
    public void onTextMessage(TextMessageEvent e) {
        System.out.println(e.getMap());
        if (e.getMessage().equals("leave")) { //TODO: *Debugging* Remove this
            killAll();
            return;
        }
        if (e.getTargetMode() == TextMessageTargetMode.CLIENT) {
            if (e.getInvokerId() == getBotClientId()) {
                return;
            }
            chatController.onMessageReceived(e.getInvokerUniqueId(), e.getMessage());
            api.sendPrivateMessage(e.getInvokerId(), "Kek; " + e.getMessage());
        } else if (e.getTargetMode() == TextMessageTargetMode.SERVER) {
            //TODO: Track in DB
        }
    }

    @Override
    public void sendChat(String uuid, String message) {
        synchronized (api) {
            System.out.println(Thread.currentThread().getName());
            api.sendPrivateMessage(
                    Util.getClientIdFromUUID(uuid),
                    message
            );
        }
    }

    @Override
    public void onClientJoin(ClientJoinEvent e) {
        System.out.println(e.getMap()); //TODO: Debugging
        if (!isUserType(e.getClientType())) return;

        Globals.getOverview().addClient(api.getClientByUId(e.getUniqueClientIdentifier()));
        slaveController.onClientJoin(e.getClientTargetId());

        //TODO: Refresh users


        //TODO: Track in DB
        //TODO: Check if not imitating other user
        //TODO: Check if first login since bot is active
        //TODO: If not -> Send 'Hi there!' message
    }

    @Override
    public void onClientLeave(ClientLeaveEvent e) {
        System.out.println(e.getMap());
        if (!isUser(e.getClientId())) {
            return;
        } else {
            System.out.println("[LEFT] " + e.getClientId());
        }
        //TODO: Track in DB
        slaveController.onClientLeave(Util.getClient(e.getClientId()).getChannelId());
        chatController.killChat(e.getInvokerUniqueId());
        Globals.getOverview().removeClient(Util.getClient(e.getClientId()));
    }

    @Override
    public void onClientMoved(ClientMovedEvent e) {
        Client c = Util.getClient(e.getClientId());
        String cString = (c == null ? "SQ (ID: " + e.getClientId() + ")" : c.getNickname() + " (ID: " + c.getId() + ")");
        String movedFrom = (c == null ? "Unknown" : Globals.getOverview().getChidToChannel().get(c.getChannelId()).getName() + " (ID: " + c.getChannelId() + ")");
        String movedTo = Globals.getOverview().getChidToChannel().get(e.getClientTargetId()).getName() + " (ID: " + e.getClientTargetId() + ")";
        System.out.println(cString + " | From: " + movedFrom + " | To: " + movedTo + " | isUser: " + isUser(e.getClientId()));
        if (!isUser(e.getClientId())) return;

        try {
            Client client = Util.getClient(e.getClientId());
            int oldChannel = client.getChannelId();
            int newChannel = e.getClientTargetId();
            client.getMap().put(ClientProperty.CID.getName(), String.valueOf(newChannel));
            System.out.println("[MASTER] From: " + oldChannel + " | To: " + client.getChannelId() + " (Should be: " + newChannel + ")"); //Does this work?

            slaveController.onClientMove(oldChannel, newChannel);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        //TODO: Track in DB
    }

    /** Refresh the ServerOverview */
    public void refreshServerOverview() {
        synchronized (api) {
            Globals.setOverview(new ServerOverview(
                    api.getServerInfo(),
                    api.getClients(),
                    api.getChannels(),
                    api.getServerGroups()
            ));
        }
        Util.post(Globals.getOverview());
    }

    private boolean isUserType(int clientType) {
        return clientType == 0;
    }

    private boolean isUser(int clientId) {
        return Globals.getOverview().getClidToClient().containsKey(clientId);
    }

}
