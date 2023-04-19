package com.gotrleech.event;

import com.gotrleech.GotrState;
import lombok.Value;

/**
 * An event denoting that the GotR game state has changed.
 */
@Value
public class GotrStateChanged {

    GotrState.State state;
}