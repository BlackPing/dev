<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">

<script type="text/javascript" src="/res/js/jquery-3.4.1.min.js"></script>
</head>

<body>
	<input type="text" id="message" />
	<input type="button" id="sendBtn" value="전송" />
	<div id="data"></div>
</body>

<h1>Echo Server 1</h1>
<c:url value="/echo1"/>

<script type="text/javascript">
	$(document).ready(function() {
		$("#sendBtn").click(function() {
			sendMessage();
			$('#message').val('')
		});
		$("#message").keydown(function(key) {
			if (key.keyCode == 13) {// 엔터
				sendMessage();
				$('#message').val('')
			}
		});
	});
	
	let Socket = new WebSocket("ws://socket.com:8080/echo1/websocket");
	Socket.onopen = function () {
		console.log("서버 온");
	}
	Socket.onmessage = onMessage;
	
	function sendMessage() {
		Socket.send($("#message").val());
	}
	
	function onMessage(msg) {
		var data = msg.data;
		$("#data").append(data + "<br/>");
	}

/* 	// 웹소켓을 지정한 url로 연결한다.
	let sock = new SockJS("http://112.154.178.73:8080/echo1");
	sock.onmessage = onMessage;
	sock.onclose = onClose;

	// 메시지 전송

	function sendMessage() {
		sock.send($("#message").val());
	}

	// 서버로부터 메시지를 받았을 때
	function onMessage(msg) {
		var data = msg.data;
		$("#data").append(data + "<br/>");
	}

	// 서버와 연결을 끊었을 때

	function onClose(evt) {
		$("#data").append("연결 끊김");
	} */
</script>
</html>