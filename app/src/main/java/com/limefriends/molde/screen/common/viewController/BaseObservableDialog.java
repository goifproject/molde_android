package com.limefriends.molde.screen.common.viewController;

import com.limefriends.molde.screen.common.dialog.ObservableDialog;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public abstract class BaseObservableDialog<ListenerType>
        extends BaseDialog implements ObservableDialog<ListenerType> {

    private Set<ListenerType> mListeners = new HashSet<>();

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
