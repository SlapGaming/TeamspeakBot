package nl.stoux.slaptsbot;

import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import nl.stoux.slaptsbot.Teamspeak.Models.ServerOverview;

/**
 * Created by Stoux on 29/10/2015.
 */
public class Util {

    /**
     * Post an object in the EventBus
     * @param o The object
     */
    public static void post(Object o) {
        Globals.getEventBus().post(o);
    }

    /**
     * Get the Client ID for an online user by their UUID
     * @param uuid The UUID
     * @return The id or -1
     */
    public static int getClientIdFromUUID(String uuid) {
        ServerOverview overview = Globals.getOverview();
        if (overview == null || !overview.getUuidToClient().containsKey(uuid)) {
            return -1;
        } else {
            return overview.getUuidToClient().get(uuid).getId();
        }
    }

    /**
     * Get a Client from the Overview by their ClientID
     * @param clid
     * @return
     */
    public static Client getClient(int clid) {
        return Globals.getOverview().getClidToClient().get(clid);
    }

    /**
     * Get a Client fromt he overview by their Unique User Identifier
     * @param uuid
     * @return
     */
    public static Client getClient(String uuid) {
        return Globals.getOverview().getUuidToClient().get(uuid);
    }

    /**
     * Let the MasterBot send a Private message
     * @param client The client
     * @param message The message
     */
    public static void sendPrivateMessage(Client client, String message) {
        Globals.getMasterBot().getApi().sendPrivateMessage(client.getId(), message);
    }


}
