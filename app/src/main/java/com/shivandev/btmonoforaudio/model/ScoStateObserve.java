package com.shivandev.btmonoforaudio.model;

import java.util.Observable;

public class ScoStateObserve extends Observable {

    public enum ScoState { SCO, BT_LISTENER }

    public void scoStateChanged(ScoState data) {
        setChanged();
        notifyObservers(data);
    }
}
