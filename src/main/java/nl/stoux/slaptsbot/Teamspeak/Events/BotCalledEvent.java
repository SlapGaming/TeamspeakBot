package nl.stoux.slaptsbot.Teamspeak.Events;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by Stoux on 29/10/2015.
 */
@Getter
@AllArgsConstructor
public class BotCalledEvent {
    private String calledByUniqueUserId;
}
