package nl.stoux.slaptsbot;

import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import nl.stoux.slaptsbot.Teamspeak.Bots.AbstractBot;
import nl.stoux.slaptsbot.Teamspeak.Bots.MasterBot;

/**
 * Created by Stoux on 28/10/2015.
 */
public class Main {

    private MasterBot masterBot;

    public Main() {
        masterBot = new MasterBot();
        Globals.setMasterBot(masterBot);
    }

    public static void main(String[] args) {
        new Main();
    }

}
