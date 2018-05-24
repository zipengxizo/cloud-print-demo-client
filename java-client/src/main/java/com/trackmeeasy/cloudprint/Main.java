package com.trackmeeasy.cloudprint;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.RestTemplateXhrTransport;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class Main {

	public static void main(String[] args) {
		try {
			
			// Set transports
			List<Transport> transports = new ArrayList<Transport>(2);
			transports.add(new WebSocketTransport(new StandardWebSocketClient()));
			transports.add(new RestTemplateXhrTransport());

			// Set websocket client
			WebSocketClient webSocketClient = new SockJsClient(transports);
			WebSocketStompClient stompClient = new WebSocketStompClient(webSocketClient);
			stompClient.setMessageConverter(new StringMessageConverter());
			stompClient.setMessageConverter(new MappingJackson2MessageConverter());
			
			// Connect
			ListenableFuture<StompSession> future = stompClient.connect("http://localhost:8098/cloud-print-websocket", new StompSessionHandler() {

				@Override
				public Type getPayloadType(StompHeaders headers) {
					return Response.class;
				}

				@Override
				public void handleFrame(StompHeaders headers, Object payload) {
					Response response = (Response) payload;
					System.out.println(response.getResponseType());
					System.out.println(response.getResponseMessage());
				}

				@Override
				public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
					session.subscribe("/topic/response", this);
				}

				@Override
				public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
					System.out.println(exception.getMessage());
				}

				@Override
				public void handleTransportError(StompSession session, Throwable exception) {
					System.out.println(exception.getMessage());
				}});
			
			// Get stomp session
			StompSession session = future.get();
			
			// Send empty request
		    session.send("/app/request", randomOrder());
		    
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private static String randomOrder() throws Exception {
		
        // 获取 fields
		JsonObject order = new JsonObject();
		// 顺丰单号
		order.addProperty("mailno", getRandomInt(100000000000L, 999999999999L));
		// 代理单号
		order.addProperty("agent_mailno", "SF" + getRandomInt(100000000, 999999999) + "EE");
		// 流向代码
		order.addProperty("direction_code", getRandom(new String[]{"5-EE-RU-P", "6-EE-RU-G"}));
		// 产品类型
		order.addProperty("express_type", getRandom(new String[]{"25", "26"}));
		// 发件人姓名
		order.addProperty("j_contact", getRandom(new String[]{"Bruce Lee", "Jackie Chan", "Fan Bingbing", "Yao Ming", "Jay Chou", "Andy Lau"}));
		// 收件人地址
		order.addProperty("d_address", getRandom(new String[]{"Shenzhen University", "Beijing Insitute of Technology"}));
		// 收件人城市
		order.addProperty("d_city", getRandom(new String[]{"Moscow", "Tallin", "Tokyo"}));
		// 收件人姓名
		order.addProperty("d_contact", getRandom(new String[]{"Barack Obama", "Vladimir Putin", "Donald Trump", "Kim Jong-un"}));
		// 收件人国家
		order.addProperty("d_country", getRandom(new String[]{"RU", "UA", "BY"}));
		// 收件人手机
		order.addProperty("d_mobile", "136" + getRandomInt(10000000L, 99999999L));
		// 收件人邮编
		order.addProperty("d_post_code", getRandomInt(100000L, 999999L));
		// 收件人身份
		order.addProperty("d_province", getRandom(new String[]{"California", "Texas", "Quebec", "Alaska"}));
		// 是否退回
		order.addProperty("return_remark", getRandomInt(0, 1));
		// 是否带电
		order.addProperty("isBat", getRandomInt(0, 1));
		
		BigDecimal totalWeight = BigDecimal.ZERO;
		BigDecimal totalDeclaredValue = BigDecimal.ZERO;
		JsonArray cargoArray = new JsonArray();
		
		// 随机货物信息
		for (int i=0; i<getRandomInt(1L, 5L); i++) {
			
			JsonObject cargo = new JsonObject();
			long count = getRandomInt(1L, 5L);
			
			BigDecimal weight = new BigDecimal(getRandomInt(5L, 2000L)).multiply(new BigDecimal(count)).divide(new BigDecimal(1000));
			totalWeight = totalWeight.add(weight);
			
			BigDecimal declaredValue = new BigDecimal(getRandomInt(5L, 2000L)).multiply(new BigDecimal(count)).divide(new BigDecimal(100));
			totalDeclaredValue = totalDeclaredValue.add(declaredValue);
			
			cargoArray.add(cargo);

			// 货物名称
			cargo.addProperty("name", getRandom(new String[]{"iPhone", "Kindle", "iPad", "Headphones"}));
			
			// 货物数量
			cargo.addProperty("count", count);
						
			// 货物重量
			cargo.addProperty("weight", weight.toString());
						
			// 货物申报价值
			cargo.addProperty("amount", declaredValue.toString());
		}
		
		// 货物集合
		order.add("Cargo", cargoArray);
		// 总重量
		order.addProperty("cargo_total_weight", totalWeight.toString());
		// 总申报价值
		order.addProperty("declared_value", totalDeclaredValue.toString());

		return new Gson().toJson(order);
	}

	private static String getRandom(String[] array) throws Exception {
		return array[(int) getRandomInt(0, array.length)];
	}
	
	private static long getRandomInt(long min, long max) throws Exception {

		if (min >= max) {
			throw new Exception("max must be greater than min");
		}

		return (long) (Math.random() * (max - min)) + min;
	}

}
