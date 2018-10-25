package com.limefriends.molde.screen.common.dialog;

public interface ObservableDialog<ListenerType> {

    void registerListener(ListenerType listener);

    void unregisterListener(ListenerType listener);
}
