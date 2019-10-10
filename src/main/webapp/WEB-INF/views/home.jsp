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
		<script type="text/javascript" src="/res/js/func.min.js"></script>
		<script type="text/javascript" src="/res/js/topic.min.js"></script>
		<script type="text/javascript" src="/res/js/jscolor.js"></script>
		<script type="text/javascript" src="/res/js/start.min.js"></script>
		<script></script>
	</head>
	<body>
		<c:set var="session" value="${sessionScope.SESSION_OBJECT}" />
		
		<jsp:include page="/resources/jsp/header.jsp" flush="false" />
		<div id="container">
			<div class="loader display-none"></div>
			<div id="left-container">
				<div id="profile-container">
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
							<span id="cert-btn" onclick="nickname(event)">변경</span>
							</div>
						<c:if test="${session.power eq 0}">
							<div style="font-size: 12px; opacity: 0.5;">채팅방 구독을 할 수 없습니다<br>닉네임을 변경해주세요</div>
						</c:if>
					</c:otherwise>
				</c:choose>
	
				</div>
				<div id="topic-container">
					<c:if test="${session.power eq 1}">
						<button id="topic-create-btn" class="text-center" onclick="topicadd(event)"><span>구독 채널 만들기</span><img src="/res/img/square-add-button.png" class="topic-plus"></button>
					</c:if>
					<div id="channel-list" class="text-center"><span>채널 리스트</span><img id="select-refresh" title="새로고침" class="refresh" src="/res/img/refresh-arrow.png"></div>
					<input id="search-text" type="text"><button id="search">검색</button>
					<div id="channel"></div>
				</div>
			</div>
			<div id="socket-container">
				<c:choose>
					<c:when test="${empty session}">
						로그인이 필요한 서비스입니다.
					</c:when>
					<c:otherwise>
						<table id="topic-connectionlist">
							<thead>
								<tr>
									<td id="topic-list" class="topic-list txt_line" data-topic="main">구독한 채널</td>
								</tr>
							</thead>
						</table>
						<div id="app-topic"></div>
						<div id="app-chat" class="hidden"></div>
						<div id="app-textarea">
							<div>
								<div class="display-inline">
									글꼴
									<select id="font-family" onchange="CattingOption()">
										<option>굴림</option>
										<option>바탕</option>
										<option>돋움</option>
										<option>궁서</option>
										<option>맑은 고딕</option>
									</select>
								</div>
								<div class="display-inline">
									크기
									<select id="font-size"  onchange="CattingOption()">
										<c:forEach var="i" begin="8" end="16">
											<c:choose>
												<c:when test="${i eq 9}"><option selected>${i}pt</option></c:when>
												<c:otherwise><option>${i}pt</option></c:otherwise>
											</c:choose>
										</c:forEach>
									</select>
								</div>
								<div class="display-inline">
									색상
									<input id="font-color" style="width: 50px;" class="jscolor" value="000000" onchange="CattingOption()" readonly>
								</div>
								<div class="display-inline">
									배경
									<input id="back-color" style="width: 50px;" class="jscolor" value="FFFFFF" onchange="CattingOption()" readonly>
								</div>
								<button id="clean">청소</button>
								알림 ON/OFF<input id="call" type="checkbox">
							</div>
							<div class="display-inline" style="position: relative; bottom: 40px;">
								<div>입력대상</div>
								<select id="send-type">
									<option>모두에게</option>
								</select>
							</div>
							<textarea id="chatting" rows="5" cols="40" wrap="hard" class="display-inline" maxlength="200" onchange="MaxLength(this)"></textarea>
							<div class="display-inline" style="position: relative; bottom: 40px;">
								<div><div style="width: 13px; height: 10px" class="display-inline"><strong>B</strong></div><input id="font-strong" type="checkbox" onchange="CattingOption()"></div>
								<div><div style="width: 13px; height: 10px; font-style: italic;" class="display-inline">I</div><input id="font-i" type="checkbox" onchange="CattingOption()"></div>
								<div><div style="width: 13px; height: 10px; text-decoration: underline;" class="display-inline">U</div><input id="font-underline" type="checkbox" onchange="CattingOption()"></div>
							</div>
						</div>
					</c:otherwise>
				</c:choose>
			</div>
		</div>
		<jsp:include page="/resources/jsp/footer.jsp" flush="false" />
		<div id="modal"></div>
		<div id="topic-add-modal"></div>
		<div id="modal-cancel"></div>
		<div id="user-add-modal"></div>
	</body>
</html>