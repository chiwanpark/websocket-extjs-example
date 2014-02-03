package com.chiwanpark.example.websocket;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint(value = "/websocket", configurator = SampleEndPointConfigurator.class)
public class SampleEndPoint {

  private static Logger logger = LoggerFactory.getLogger(SampleEndPoint.class);
  private static ObjectMapper objectMapper = new ObjectMapper();
  private static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  private static Map<String, Timer> timerMap = new HashMap<String, Timer>();

  public static Map<String, Object> createNewData() {
    Map<String, Object> data = new HashMap<String, Object>();
    Date date = new Date();

    data.put("date", dateFormat.format(date));
    data.put("value", (int) (Math.random() * 1024));

    return data;
  }

  private class Task extends TimerTask {

    private Session session;

    public Task(Session session) {
      this.session = session;
    }

    @Override
    public void run() {
      Map<String, Object> resultMap = new HashMap<String, Object>();
      List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();

      data.add(createNewData());

      resultMap.put("event", "read");
      resultMap.put("data", data);

      try {
        session.getAsyncRemote().sendText(objectMapper.writeValueAsString(resultMap));
      } catch (IOException e) {
        e.printStackTrace();
        this.cancel();
      }
    }
  }

  @OnOpen
  public void onOpen(Session userSession) {
    logger.debug("onOpen - " + userSession.getId());

    Timer timer = new Timer();
    Task task = new Task(userSession);

    timerMap.put(userSession.getId(), timer);

    timer.scheduleAtFixedRate(task, 200, 2000);
  }

  @OnClose
  public void onClose(Session userSession) {
    logger.debug("onClose - " + userSession.getId());

    String id = userSession.getId();

    if (timerMap.containsKey(id)) {
      Timer timer = timerMap.get(id);
      timer.cancel();
      timerMap.remove(id);
    }
  }

  @OnMessage
  public void onMessage(String message, Session userSession) {
    logger.debug("onMessage - " + userSession.getId() + " - " + message);

    try {
      Map<String, Object> parameter = objectMapper.readValue(message, new TypeReference<Map<String, Object>>() {});

      if ("read".equals(parameter.get("event"))) {
        onOpen(userSession);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

}
