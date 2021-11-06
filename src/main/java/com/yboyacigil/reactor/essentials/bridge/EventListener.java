package com.yboyacigil.reactor.essentials.bridge;

public interface EventListener<T> {

    void onData(T data);

    void complete();

}
