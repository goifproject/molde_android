package com.limefriends.molde.screen.common.views;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public abstract class BaseObservableView<ListenerType>
        extends BaseView implements ObservableView<ListenerType> {

    private Set<ListenerType> mListeners = Collections.newSetFromMap(
            new ConcurrentHashMap<ListenerType, Boolean>(1));

    @Override
    public void registerListener(ListenerType listener) {
        mListeners.add(listener);
    }

    @Override
    public void unregisterListener(ListenerType listener) {
        mListeners.remove(listener);
    }

    protected Set<ListenerType> getListeners() {
        return Collections.unmodifiableSet(mListeners);
    }
}
