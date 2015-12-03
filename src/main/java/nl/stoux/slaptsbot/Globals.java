package nl.stoux.slaptsbot;

import com.google.common.eventbus.EventBus;
import lombok.Getter;
import lombok.Setter;
import nl.stoux.slaptsbot.Teamspeak.Bots.MasterBot;
import nl.stoux.slaptsbot.Teamspeak.Models.ServerOverview;

/**
 * Created by Stoux on 29/10/2015.
 */
public class Globals {

    public static final String STOUX_UUID = "cKSOKEV3nN9tBf94FS2nHY6WrXY=";

    @Getter
    private static EventBus eventBus = new EventBus();

    @Getter @Setter
    private static ServerOverview overview;

    @Getter @Setter
    private static MasterBot masterBot;

}
