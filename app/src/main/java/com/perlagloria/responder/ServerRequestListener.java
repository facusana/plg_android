package com.perlagloria.responder;

public interface ServerRequestListener {
    void onRequestStarted();

    void onRequestFinished();
}
