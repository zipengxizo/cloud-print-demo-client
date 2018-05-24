var stompClient = null;

function connect() {
	var serviceUrl = $("#serviceUrl").val();
    var socket = new SockJS(serviceUrl + '/cloud-print-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        var sessionId = /\/([^\/]+)\/websocket/.exec(socket._transport.url)[1];
    	$("#sessionId").text(sessionId);
        console.log('Connected to: ' + sessionId);
        stompClient.subscribe('/topic/response', function (response) {
            showResponse(JSON.parse(response.body).responseType, JSON.parse(response.body).responseMessage);
        });
    }, function (error) {
    	alert(error);
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function send() {
	var orderInfo = $("#orderInfo").val();
    stompClient.send("/app/request", {}, JSON.stringify({'orderInfo': orderInfo}));
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#connect" ).click(function() { connect(); });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#send" ).click(function() { send(); });
    $( "#random" ).click(function() { random(); });
    
    $( "#trackingNo" ).on('input', function() {
        $("#responseType").text('');
        $("#responseMessage").text('');
    });
    
    // 云打印服务地址
    var parameters = window.location.search.substring(1);
    var parameter = parameters.split('=');
	$("#serviceUrl").val(parameter[1]);
});

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#responseType").text('');
    $("#responseMessage").text('');
	$("#sessionId").text('');
}

function setResponseClass(responseType) {
	$("#responseType").removeClass();
	if ("Success" === responseType) {
		$("#responseType").addClass("badge progress-bar-success");
	} else {
		$("#responseType").addClass("badge progress-bar-danger");
	}
}

function showResponse(responseType, responseMessage) {
	setResponseClass(responseType);
	$("#responseType").text(responseType);
	$("#responseMessage").text(responseMessage);
}

// 随机变量
function random() {

	var orderItems = [];
	var totalWeight = 0;
	var totalDeclaredValue = 0;
	var randomItemsSize = getRandomInt(1, 5);
	for (i = 0; i < randomItemsSize; i++) {
		var orderItem = new Object();
		
		// 货物名称
		orderItem.name = getRandom(new Array("iPhone", "Kindle", "iPad", "Headphones"));
		var itemCount = getRandomInt(1, 5);
		// 货物数量
		orderItem.count = itemCount;
		var itemWeight = getRandomInt(5, 2000) * itemCount / 1000;
		// 货物重量
		orderItem.weight = itemWeight;
		totalWeight = totalWeight + itemWeight;
		var itemDeclaredValue = getRandomInt(5, 2000) * itemCount / 100;
		// 货物申报价值
		orderItem.amount = itemDeclaredValue;
		totalDeclaredValue = totalDeclaredValue + itemDeclaredValue;
		
		orderItems.push(orderItem);
	}
	
	var object = new Object();
	// 代理单号
	object.agent_mailno = "SF" + getRandomInt(100000000, 999999999) + "EE";
	// 收件人地址
	object.d_address = getRandom(new Array("Shenzhen University", "Beijing Insitute of Technology"));
	// 收件人城市
	object.d_city = getRandom(new Array("Moscow", "Tallin", "Tokyo"));
	// 收件人姓名
	object.d_contact = getRandom(new Array("Barack Obama", "Vladimir Putin", "Donald Trump", "Kim Jong-un"));
	// 收件人国家
	object.d_country = getRandom(new Array("AT", "AU", "BE", "BR", "BY", "CA", "CH", "CZ", "DE", "ES", "FI", "FR", "GB", "GR", "HU", "ID", "IE", "IL", "IN", "IT", "JP", "KR", "MY", "NL", "NO", "NZ", "PL", "PT", "RU", "SE", "SG", "TH", "TR", "UA", "US"));
	// 流向代码
	object.direction_code = getRandom(new Array("5-EE-RU-P", "6-EE-RU-G"));
	// 收件人手机
	object.d_mobile = "136" + getRandomInt(10000000, 99999999);
	// 收件人邮编
	object.d_post_code = getRandomInt(100000, 999999);
	// 收件人省份
	object.d_province = getRandom(new Array("California", "Texas", "Quebec", "Alaska"));
	// 是否带电
	object.isBat = getRandom(new Array("1", "0"));
	// 产品类型
	object.express_type = getRandom(new Array("25", "26"));
	// 发件人姓名
	object.j_contact = getRandom(new Array("Bruce Lee", "Jackie Chan", "Fan Bingbing", "Yao Ming", "Jay Chou", "Andy Lau"));
	// 顺丰单号
	object.mailno = getRandomInt(100000000000, 999999999999);
	// 总重量
	object.cargo_total_weight = totalWeight;
	// 总申报价值
	object.declared_value = totalDeclaredValue;
	// 货物信息
	object.Cargo = orderItems;
	
	$("#orderInfo").val(JSON.stringify(object, null, '\t'));
}

function getRandom(array) {
	return array[getRandomInt(0, array.length)];
}

function getRandomInt(min, max) {
	min = Math.ceil(min);
	max = Math.floor(max);
	return Math.floor(Math.random() * (max - min)) + min;
}