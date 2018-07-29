package net.iGap.eventbus;

/**
 * Created by keyvan on 7/14/17.
 */

public interface EventListener {
    void receivedMessage(int id, Object... message);


}