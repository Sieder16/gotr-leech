package com.gotrleech.event;

import com.gotrleech.GotrGameState;
import lombok.Value;

/**
 * An event denoting that the GotR game state has changed.
 */
@Value
public class GotrGameStateChanged {

    GotrGameState.State state;
}