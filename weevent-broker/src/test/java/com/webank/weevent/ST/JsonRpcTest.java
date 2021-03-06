package com.webank.weevent.ST;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.webank.weevent.JUnitTestBase;
import com.webank.weevent.sdk.BrokerException;
import com.webank.weevent.sdk.ErrorCode;
import com.webank.weevent.sdk.SendResult;
import com.webank.weevent.sdk.TopicInfo;
import com.webank.weevent.sdk.TopicPage;
import com.webank.weevent.sdk.WeEvent;
import com.webank.weevent.sdk.jsonrpc.IBrokerRpc;

import lombok.extern.slf4j.Slf4j;
import com.googlecode.jsonrpc4j.JsonRpcHttpClient;
import com.googlecode.jsonrpc4j.ProxyUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

@Slf4j
public class JsonRpcTest extends JUnitTestBase {
    private IBrokerRpc iBrokerRpc;
    private String groupId = "1";
    private String jsonTopic = "com.weevent.test.jsonrpc";
    private String eventId;
    private Map<String, String> extension = new HashMap<>();
    private String content = "Hellow WeEvent";

    @Before
    public void before() throws Exception {
	String url = "http://localhost:" + listenPort + "/weevent/jsonrpc";

	JsonRpcHttpClient client = new JsonRpcHttpClient(new URL(url));
	this.iBrokerRpc = ProxyUtil.createClientProxy(client.getClass().getClassLoader(), IBrokerRpc.class, client);
	this.eventId = iBrokerRpc.publish(this.jsonTopic, this.content.getBytes()).getEventId();
    }

    @Test
    public void testOpen_noGroupId() throws BrokerException {
	boolean open = iBrokerRpc.open(this.jsonTopic);
	log.info("open topic : " + open);
	Assert.assertTrue(open);
    }

    @Test
    public void testOpen_cotainGroupId() throws BrokerException {
	boolean open = iBrokerRpc.open(this.jsonTopic, this.groupId);
	log.info("open topic : " + open);
	Assert.assertTrue(open);
    }

    @Test
    public void testClose_noGroupId() throws BrokerException {
	boolean open = iBrokerRpc.close(this.jsonTopic);
	log.info("close topic : " + open);
	Assert.assertTrue(open);
    }

    @Test
    public void testClose_cotainGroupId() throws BrokerException {
	boolean open = iBrokerRpc.close(this.jsonTopic, this.groupId);
	log.info("close topic : " + open);
	Assert.assertTrue(open);
    }

    @Test
    public void testExist_noGroupId() throws BrokerException {
	boolean exist = iBrokerRpc.exist(this.jsonTopic);
	log.info("topic exist : " + exist);
	Assert.assertTrue(exist);
    }

    @Test
    public void testExist_cotainGroupId() throws BrokerException {
	boolean exist = iBrokerRpc.exist(this.jsonTopic, this.groupId);
	log.info("topic exist : " + exist);
	Assert.assertTrue(exist);
    }

    @Test
    public void testState_noGroupId() throws BrokerException {
	TopicInfo state = iBrokerRpc.state(this.jsonTopic);
	log.info("state : " + state);
	Assert.assertNotNull(state.getTopicAddress());
    }

    @Test
    public void testState_cotainGroupId() throws BrokerException {
	TopicInfo state = iBrokerRpc.state(this.jsonTopic, this.groupId);
	log.info("state : " + state);
	Assert.assertNotNull(state.getTopicAddress());
    }

    @Test
    public void testList_noGroupId() throws BrokerException {
	TopicPage list = iBrokerRpc.list(0, 10);
	log.info("list topic : " + list);
	Assert.assertTrue(list.getTotal() > 0);
    }

    @Test
    public void testList_cotainGroupId() throws BrokerException {
	TopicPage list = iBrokerRpc.list(0, 10, this.groupId);
	log.info("list topic : " + list);
	Assert.assertTrue(list.getTotal() > 0);
    }

    @Test
    public void testPublish_noGroupIdExt() throws BrokerException {
	SendResult publish = iBrokerRpc.publish(this.jsonTopic, "Hello World!".getBytes());
	log.info("publish: " + publish);
	Assert.assertNotNull(publish.getEventId());
    }

    @Test
    public void testPublish_cotainGroupIdNoExt() throws BrokerException {
	SendResult publish = iBrokerRpc.publish(this.jsonTopic, this.groupId, this.content.getBytes());
	log.info("publish: " + publish);
	Assert.assertNotNull(publish.getEventId());
    }

    @Test
    public void testPublish_noGroupId() throws BrokerException {
	Map<String, String> ext = new HashMap<>();
	ext.put("json rpc ext test1", "json rpc ext value1");
	ext.put("json rpc ext test2", "json rpc ext value2");
	SendResult publish = iBrokerRpc.publish(this.jsonTopic, this.content.getBytes(), ext);
	log.info("publish: " + publish);
	Assert.assertNotNull(publish.getEventId());
    }

    @Test
    public void testPublish_cotainGroupId() throws BrokerException {
	Map<String, String> ext = new HashMap<>();
	ext.put("json rpc ext test1", "json rpc ext value1");
	ext.put("json rpc ext test2", "json rpc ext value2");
	SendResult publish = iBrokerRpc.publish(this.jsonTopic, this.groupId, this.content.getBytes(), ext);
	log.info("publish: " + publish);
	Assert.assertNotNull(publish.getEventId());
    }

    @Test
    public void testPublish_contentEq10K() throws BrokerException {
	String str = get10KStr();
	SendResult publish = iBrokerRpc.publish(this.jsonTopic, this.groupId, str.getBytes(), extension);
	log.info("publish: " + publish);
	Assert.assertNotNull(publish.getEventId());
    }

    @Test
    public void testPublish_contentGt10K() {
	String str = get10KStr() + "s";
	try {
	    SendResult publish = iBrokerRpc.publish(this.jsonTopic, this.groupId, str.getBytes(), extension);
	    Assert.assertNull(publish);
	} catch (BrokerException e) {
	    log.info(e.getMessage());
	    Assert.assertEquals(ErrorCode.EVENT_CONTENT_EXCEEDS_MAX_LENGTH.getCodeDesc(), e.getMessage());
	}
    }

    @Test
    public void testPublish_extEq1K() throws BrokerException {
	Map<String, String> ext = get1KMap();
	SendResult publish = iBrokerRpc.publish(this.jsonTopic, this.groupId, this.content.getBytes(), ext);
	log.info("publish: " + publish);
	Assert.assertNotNull(publish.getEventId());
    }

    @Test
    public void testPublish_extGt1K() {
	Map<String, String> ext = get1KMap();
	ext.put("key2", "value2");
	try {
	    SendResult publish = iBrokerRpc.publish(this.jsonTopic, this.groupId, this.content.getBytes(), ext);
	    Assert.assertNull(publish);
	} catch (BrokerException e) {
	    log.info(e.getMessage());
	    Assert.assertEquals(ErrorCode.EVENT_EXTENSIONS_EXCEEDS_MAX_LENGTH.getCodeDesc(), e.getMessage());
	}
    }

    @Test
    public void testGetEvent_noGroupId() throws BrokerException {
	WeEvent event = iBrokerRpc.getEvent(this.eventId);
	log.info("getEvent : " + event);
	Assert.assertEquals(this.content, new String(event.getContent()));
    }

    @Test
    public void testGetEvent_cotainGroupId() throws BrokerException {
	WeEvent event = iBrokerRpc.getEvent(this.eventId, this.groupId);
	log.info("getEvent : " + event);
	Assert.assertEquals(this.content, new String(event.getContent()));
    }

    @Test
    public void testSubscribe_containTopicUrl() throws BrokerException {
	String subscriptionId = iBrokerRpc.subscribe(this.jsonTopic,
		"http://localhost:" + listenPort + "/weevent/mock/jsonrpc");
	Assert.assertNotNull(subscriptionId);
	Assert.assertTrue(subscriptionId.contains("-"));
    }

    @Test
    public void testSubscribe_containTopicErrorUrl() {
	try {
	    String subscriptionId = iBrokerRpc.subscribe(this.jsonTopic,
		    "http://localhost:" + 8089 + "/weevent/mock/jsonrpc");
	    Assert.assertNotNull(subscriptionId);
	} catch (BrokerException e) {
	    Assert.assertEquals(ErrorCode.URL_CONNECT_FAILED.getCodeDesc(), e.getMessage());
	}
    }

    @Test
    public void testReSubscribe_containTopicUrl() throws BrokerException {
	String subId = iBrokerRpc.subscribe(this.jsonTopic, "http://localhost:" + listenPort + "/weevent/mock/jsonrpc");

	String subscriptionId = iBrokerRpc.subscribe(this.jsonTopic, subId,
		"http://localhost:" + listenPort + "/weevent/mock/jsonrpc");
	Assert.assertNotNull(subscriptionId);
	Assert.assertEquals(subId, subscriptionId);
    }

    @Test
    public void testReSubscribe_containTopicErrorUrl() throws BrokerException {
	String subId = iBrokerRpc.subscribe(this.jsonTopic, "http://localhost:" + listenPort + "/weevent/mock/jsonrpc");

	try {
	    String subscriptionId = iBrokerRpc.subscribe(this.jsonTopic, subId,
		    "http://localhost:" + 8089 + "/weevent/mock/jsonrpc");
	    Assert.assertNull(subscriptionId);
	} catch (BrokerException e) {
	    Assert.assertEquals(ErrorCode.URL_CONNECT_FAILED.getCodeDesc(), e.getMessage());
	}
    }

    @Test
    public void testReSubscribe_containTopicUrlGroupId() throws BrokerException {
	String subId = iBrokerRpc.subscribe(this.jsonTopic, "http://localhost:" + listenPort + "/weevent/mock/jsonrpc");

	String subscriptionId = iBrokerRpc.subscribe(this.jsonTopic, this.groupId, subId,
		"http://localhost:" + listenPort + "/weevent/mock/jsonrpc");
	Assert.assertNotNull(subscriptionId);
	Assert.assertEquals(subId, subscriptionId);
    }

    @Test
    public void testReSubscribe_containTopicErrorUrlGroupId() throws BrokerException {
	String subId = iBrokerRpc.subscribe(this.jsonTopic, "http://localhost:" + listenPort + "/weevent/mock/jsonrpc");

	try {
	    String subscriptionId = iBrokerRpc.subscribe(this.jsonTopic, this.groupId, subId,
		    "http://localhost:" + 8089 + "/weevent/mock/jsonrpc");
	    Assert.assertNull(subscriptionId);
	} catch (BrokerException e) {
	    Assert.assertEquals(ErrorCode.URL_CONNECT_FAILED.getCodeDesc(), e.getMessage());
	}
    }

    @Test
    public void testUnSubscribe() throws BrokerException {
	String subId = iBrokerRpc.subscribe(this.jsonTopic, "http://localhost:" + listenPort + "/weevent/mock/jsonrpc");
	boolean unSubscribe = iBrokerRpc.unSubscribe(subId);
	Assert.assertTrue(unSubscribe);
    }

    @Test
    public void testSubscribe_andPublish() throws BrokerException, InterruptedException {
	String subscriptionId = iBrokerRpc.subscribe(this.jsonTopic,
		"http://localhost:" + listenPort + "/weevent/mock/jsonrpc");
	Assert.assertNotNull(subscriptionId);
	Assert.assertTrue(subscriptionId.contains("-"));
	iBrokerRpc.publish(this.jsonTopic, "Hello World!".getBytes());
	Thread.sleep(3000);

    }

    @Test
    public void testSubscribe_andPublishWildCard() throws BrokerException, InterruptedException {
	String subscriptionId = iBrokerRpc.subscribe("com.weevent.testzjy/#",
		"http://localhost:" + listenPort + "/weevent/mock/jsonrpc");
	Assert.assertNotNull(subscriptionId);
	Assert.assertTrue(subscriptionId.contains("-"));
	iBrokerRpc.publish("com.weevent.testzjy/test", "Hello World!".getBytes());
	Thread.sleep(3000);

    }

    // get 10K string
    private String get10KStr() {
	StringBuilder result = new StringBuilder("");
	for (int i = 0; i < 1024; i++) {
	    result = result.append("abcdefghij");
	}
	return result.toString();
    }

    // get 1K Map
    private Map<String, String> get1KMap() {
	Map<String, String> map = new HashMap<>();

	StringBuilder valueStr = new StringBuilder("");
	for (int i = 0; i < 101; i++) {
	    valueStr = valueStr.append("abcdefghij");
	}
	valueStr.append("abcdefd");
	map.put("key1", valueStr.toString());
	return map;
    }

}
