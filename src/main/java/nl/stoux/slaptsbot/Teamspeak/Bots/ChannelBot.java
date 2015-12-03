package nl.stoux.slaptsbot.Teamspeak.Bots;

import com.github.theholywaffle.teamspeak3.api.TextMessageTargetMode;
import com.github.theholywaffle.teamspeak3.api.event.TS3EventType;
import com.github.theholywaffle.teamspeak3.api.event.TextMessageEvent;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import nl.stoux.slaptsbot.Teamspeak.Events.BotCalledEvent;
import nl.stoux.slaptsbot.Teamspeak.Events.MessageInChannelEvent;
import nl.stoux.slaptsbot.Util;

/**
 * Created by Stoux on 28/10/2015.
 */
public class ChannelBot extends AbstractBot {


    @Setter @Getter
    private boolean reporting;
    @Getter(AccessLevel.PRIVATE)
    private int channelId;

    public ChannelBot(int channelId) {
        super(createTS3Config("channel-user", "channel-pass"), NAME + " - CH" + channelId);
        this.channelId = channelId;
        reporting = true;
        api.moveQuery(channelId);
        api.registerEvent(TS3EventType.TEXT_CHANNEL);
        api.addTS3Listeners(this);
    }

    /**
     * Move to bot to another channel
     * @param channelId the channel
     */
    public void moveToChannel(int channelId) {
        this.channelId = channelId;
        api.setNickname(NAME + " - CH" + channelId);
        api.moveQuery(channelId);

    }

    @Override
    public void onTextMessage(TextMessageEvent e) {
        System.out.println("[CID-" + channelId + "-" + e.getTargetMode() + "] " + e.getMessage());
        if (reporting && e.getTargetMode() == TextMessageTargetMode.CHANNEL) {
            Util.post(new MessageInChannelEvent(getChannelId(), e.getInvokerUniqueId(), e.getMessage()));
            if (e.getMessage().toLowerCase().startsWith("!bot")) {
                Util.post(new BotCalledEvent(e.getInvokerUniqueId()));
            }
        }
    }

}
