package com.yboyacigil.reactor.essentials.bridge;

import lombok.Value;

@Value
public class Event {
    EventType type;
    Object payload;
}
