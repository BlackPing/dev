<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="true" isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8">
		<style>
			@import url("/res/css/commons.css");
			@import url("/res/css/home.css");
		</style>
		<title>WebSocket</title>
		<script type="text/javascript" src="/res/js/jquery-3.4.1.min.js"></script>
		<script type="text/javascript" src="/res/js/topic.js"></script>
		<script>
			let week = ['일', '월', '화', '수', '목', '금', '토'];	
		
			getWeek(2019, 9, 29);
			function getWeek(year, month, day) {
				let date = new Date();
				
				date.setFullYear(year);
				date.setMonth(month - 1);
				date.setDate(day);
				
				let start = date.getDate() - date.getDay();
				let end = start + 7;
				
				for(var i = start; i < end; i++) {
					date.setMonth(month - 1);
					date.setDate(start++);
					
					if(date.getDate() <= 1) break;
					console.log(date.getFullYear(), month, date.getDate(), week[date.getDay()]);
				}
			}
		</script>
	</head>
	<body>
		<jsp:include page="/resources/jsp/header.jsp" flush="true" />
		<div id="left-container">
			<div id="profile-container">
			<c:set var="session" value="${sessionScope.SESSION_OBJECT}" />
			<c:choose>
				<c:when test="${empty session}">
					<form action="/login" method="post">
						<button id="login-btn"><img src="/res/img/kakao_login_btn.png"></button>
					</form>
				</c:when>
				<c:otherwise>
					<div><span class="kakaofont kakaoform s-kakao-logo">Kakao Account</span><form id="logout-form" action="/logout" method="post"><button id="logout-btn" type="submit">로그아웃</button></form></div>
					<div id="nickname">
						닉네임 : ${sessionScope.SESSION_OBJECT.nickname}
						<span id="cert-btn">인증</span>
						</div>
					<div style="font-size: 12px; opacity: 0.5;">채팅방 구독을 할 수 없습니다<br>닉네임 인증을 받아주세요</div>
				</c:otherwise>
			</c:choose>

			</div>
			<div id="topic-container">
				<button id="topic-create-btn" class="text-center"><span>구독 채널 만들기</span><img src="/res/img/square-add-button.png" class="topic-plus"></button>
				<div id="channel-list" class="text-center"><span>채널 리스트</span></div>
			</div>
		</div>
		<div id="socket-container"></div>
	</body>
</html>