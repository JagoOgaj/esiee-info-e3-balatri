package esiee.info.e3.model.interfaces;


import esiee.info.e3.domain.GameSnapshot;

public interface ModelObserver {
    void onModelUpdated(GameSnapshot snapshot);
}
