package com.chiwanpark.example.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint(value = "/websocket", configurator = SampleEndPointConfigurator.class)
public class SampleEndPoint {

  private static Logger logger = LoggerFactory.getLogger(SampleEndPoint.class);

  @OnOpen
  public void onOpen(Session userSession) {
    logger.debug("onOpen - " + userSession.getId());
  }

  @OnClose
  public void onClose(Session userSession) {
    logger.debug("onClose - " + userSession.getId());
  }

  @OnMessage
  public void onMessage(String message, Session userSession) {
    logger.debug("onMessage - " + userSession.getId() + " - " + message);
  }

}
