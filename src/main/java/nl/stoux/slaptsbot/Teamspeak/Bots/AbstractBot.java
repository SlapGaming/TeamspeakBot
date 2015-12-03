package nl.stoux.slaptsbot.Teamspeak.Bots;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.TS3ApiAsync;
import com.github.theholywaffle.teamspeak3.TS3Config;
import com.github.theholywaffle.teamspeak3.TS3Query;
import com.github.theholywaffle.teamspeak3.api.event.TS3EventAdapter;
import lombok.Getter;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;

/**
 * Created by Stoux on 28/10/2015.
 */
public abstract class AbstractBot extends TS3EventAdapter {

    protected static Properties properties;
    static {
        try {
            properties = new Properties();
            properties.load(AbstractBot.class.getClassLoader().getResourceAsStream("bots.properties"));
        } catch (Exception e) {
            throw new RuntimeException("Failed to read properties file");
        }
    }

    protected static TS3Config createTS3Config(String usernameKey, String passwordKey) {
        String username = properties.getProperty(usernameKey);
        String password = properties.getProperty(passwordKey);


        TS3Config config = new TS3Config();
        config.setHost("mc.slapgaming.com");
        config.setQueryPort(10011);
        config.setDebugLevel(Level.SEVERE);
        config.setLoginCredentials(username, password);
        return config;
    }

    private final static Set<AbstractBot> bots = new HashSet<>();
    protected static final String NAME = "MemeBot";

    @Getter protected final TS3Query query;
    @Getter protected final TS3Api api;

    @Getter private int botClientId;

    public AbstractBot(TS3Config config, String name) {
        bots.add(this);
        query = new TS3Query(config);
        query.connect();

        api = query.getApi();
        api.selectVirtualServerById(1);
        api.setNickname(name);

        botClientId = api.whoAmI().getId();
    }


    /** Disconnect the bot */
    public void disconnect() {
        api.logout();
        query.exit();
        bots.remove(this);
    }

    public static void killAll() {
        for (final AbstractBot bot : new HashSet<>(bots)) {
            new Thread(bot::disconnect).start();
        }
    }

}
