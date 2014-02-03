package com.chiwanpark.example.websocket;

import javax.websocket.server.ServerEndpointConfig;

public class SampleEndPointConfigurator extends ServerEndpointConfig.Configurator {

  private SampleEndPoint endPoint = new SampleEndPoint();

  @Override
  @SuppressWarnings("unchecked")
  public <T> T getEndpointInstance(Class<T> endpointClass) throws InstantiationException {
    return (T) endPoint;
  }
}
