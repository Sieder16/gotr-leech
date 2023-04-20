package com.gotrleech.overlay;

import lombok.NonNull;
import lombok.Value;

import java.awt.TrayIcon;

@Value
public class OverlayMessage {

    @NonNull TrayIcon.MessageType messageType;
    @NonNull String messageText;
}
