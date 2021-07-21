package com.flashfyre.spectrite.component;

import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;

public interface SpectriteComponent extends ComponentV3, AutoSyncedComponent
{
    boolean isSpectrite();

    void setSpectrite(boolean spectrite);
}