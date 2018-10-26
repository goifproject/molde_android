package com.limefriends.molde.screen.common.views;

public interface ObservableView<ListenerType> extends ViewMvc {

    void registerListener(ListenerType listener);

    void unregisterListener(ListenerType listener);
}
