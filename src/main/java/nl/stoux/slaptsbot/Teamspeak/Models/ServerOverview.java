package nl.stoux.slaptsbot.Teamspeak.Models;

import com.github.theholywaffle.teamspeak3.api.wrapper.Channel;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import com.github.theholywaffle.teamspeak3.api.wrapper.ServerGroup;
import com.github.theholywaffle.teamspeak3.api.wrapper.VirtualServerInfo;
import lombok.Getter;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by Stoux on 28/10/2015.
 */
@Getter
public class ServerOverview {

    public ServerOverview(VirtualServerInfo serverInfo, List<Client> clients, List<Channel> channels, List<ServerGroup> serverGroups) {
        this.serverInfo = serverInfo;
        this.channels = channels;
        this.serverGroups = serverGroups;
        this.clients = clients.stream().filter(c -> c.getType() == 0).collect(Collectors.toCollection(HashSet::new));
        mapAll();
    }

    private void mapAll() {
        clidToClient = map(clients, Client::getId);
        uuidToClient = map(clients, Client::getUniqueIdentifier);
        chidToChannel = map(channels, Channel::getId);
        gridToServerGroup = map(serverGroups, ServerGroup::getId);
    }

    private VirtualServerInfo serverInfo;
    private Set<Client> clients;
    private List<Channel> channels;
    private List<ServerGroup> serverGroups;

    private transient HashMap<Integer, Client> clidToClient;
    private transient HashMap<String, Client> uuidToClient;
    private transient HashMap<Integer, Channel> chidToChannel;
    private transient HashMap<Integer, ServerGroup> gridToServerGroup;

    private <Key, Clazz> HashMap<Key, Clazz> map(Collection<Clazz> list, Function<Clazz, Key> getKeyFunction) {
        HashMap<Key, Clazz> map = new HashMap<>();
        for (Clazz clazz : list) {
            map.put(getKeyFunction.apply(clazz), clazz);
        }
        return map;
    }

    public void addClient(Client client) {
        clients.add(client);
        clidToClient.put(client.getId(), client);
        uuidToClient.put(client.getUniqueIdentifier(), client);
    }

    public void removeClient(Client client) {
        clients.remove(client);
        clidToClient.remove(client.getId());
        uuidToClient.remove(client.getUniqueIdentifier());
    }

}
