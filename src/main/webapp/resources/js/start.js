let Socket;
let sendMessage = function(type, roomNumber, msg) { };
let onMessage = function(msg) { };
let textarea = 0;
let status = false;
$(document).ready(function() {
//	async function asyncOpen() { // 비동기 처리
//		const t1 = await socket();
//		const t2 = await select();
//	}
	select();
	socket();
	
//	asyncOpen();
	
	$('#socket-container').on('contextmenu', function() {
		  return false;
	});
	
	$('textarea').on('keydown', function(e) { // 엔터 처리
		let text = "";
        if (e.keyCode == 13) {
        	if (!e.shiftKey){
        		e.preventDefault();
        		text = $('textarea').val();
        		$('textarea').val('');
        		
        		if(text == "" || text == undefined) return;
        		else if(textarea == 0) return;
        		sendMessage("send", textarea, text);
        	} else {
        		let maxRows = $('textarea').attr('rows');
        		let spit = $('textarea').val().split('\n');
        		let rows = spit.length;
        		if(rows >= maxRows) {
        			for(var i = 0; i < maxRows; i++) {
        				text += spit[i];
        				
        				if(i < (maxRows - 1)) {
        					text += '\n';
        				}
        			}
        			$('textarea').val(text);
        		}
        	}
        }
    });
	
	$('#select-refresh').on('click', function(e) {
		select();
	});
	
	$('#search').on('click', function(e) {
		select($('#search-text').val());
	});
});

function select(search) {
	let flag = false;

	return getData("POST", "/topicsl", {"search": search}, function(data) {
		data = JSON.parse(data);
		console.log(data);
		if(data.status == false) {
			if(!getMessage(data.msg)) {
				alert("네트워크 오류입니다.");
			}
		} else {
			$('#channel').empty();
			let html;
			for(var i = 0; i < data.select.length; i++) {
				html = '<div class="channel-topic">';
				html += '<span class="channel-no">NO: ' + data.select[i].NO + '</span>';
				html += '<span class="channel-master">Master: ' + data.select[i].NICKNAME + '</span>';
				if(data.create[data.select[i].NO]) html += '<span class="channel-delete">삭제</span>';
				html += '<div class="channel-title">' + data.select[i].TITLE + '</div>';
				
				if(data.topic[data.select[i].NO]) {
					html += '<button class="channel-btn"><img src="/res/img/checked-symbol.png" height="20px"></button>';
					flag = true;
				}
				
				if(!flag) {
					html += '<button class="channel-btn">구독</button>';
				}
				
				$('#channel').append(html);
				html = "";
				flag = false;
			}
		
			$('.channel-btn').off().on('click', function(e) {
				let no = $(this).parent().children('.channel-no').text();
				no = no.substr(4, no.length);
				getData("POST", "topicup", {"no": no}, function(data) {
					data = JSON.parse(data);
					if(data.status == false) {
						if(!getMessage(data.msg)) {
							alert("네트워크 오류입니다.");
						}
					} else {
						getMessage(data.msg);
						if(Socket != undefined) {
							select();
							topicselect();
						}
					}
				});
			});
		
			$('.channel-delete').off().on('click', function(e) {
				if(confirm("정말 삭제하시겠습니까?")) {
					let no = $(this).parent().children('.channel-no').text();
					no = no.substr(4, no.length);
					getData("POST", "/topicdel", {"no": no}, function(data) {
						data = JSON.parse(data);
						if(data.status == false) {
							if(!getMessage(data.msg)) {
								alert("네트워크 오류입니다.");
							}
						} else {
							select();
							topicselect();
							getMessage(data.msg);
						}
					});
				}
			});
		}
	});
}

function topicselect() {
	return getData("POST", "/topicDynamic", {}, function(data) {
		data = JSON.parse(data);
		
		if(data.status == false) {
			if(!getMessage(data.msg)) {
				alert("네트워크 오류입니다.");
			}
		} else {
			$('#app-topic').empty();
			let html = '<img id="topic-refresh" title="새로고침" class="refresh" src="/res/img/refresh-arrow.png">';
			
			for(var i = 0; i < data.topic.length; i++) {
				html += '<div class="app-topic-list">';
				html += '<span>NO: ' + data.topic[i].TOPIC_NO + '</span> ';
				html += '<span>' + data.topic[i].TITLE + '</span><span class="topic-up">참여</span><span style="float: right;">접속중인 유저 ' + data.account[data.topic[i].TOPIC_NO] + '명</span>';
				html += '</div>';
			}

			$('#app-topic').append(html);
			$('#topic-refresh').on('click', function(e) {
				topicselect();
			});
			
			let room_index = 0;
			$('.channel-btn').off().on('click', function(e) {
				let no = $(this).parent().children('.channel-no').text();
				no = no.substr(4, no.length);
				getData("POST", "topicup", {"no": no}, function(data) {
					data = JSON.parse(data);
					if(data.status == false) {
						if(!getMessage(data.msg)) {
							alert("네트워크 오류입니다.");
						}
					} else {
						getMessage(data.msg);
						if(Socket != undefined) select();
					}
				});
			});

			$('.app-topic-list span').off().on('click', function(e) {
				let no = $(this).parent().children('span').eq(0).text();
				let title = $(this).parent().children('span').eq(1).text();
				let app = $('#app-chat');
				let list = $('#topic-connectionlist tr');
				no = no.substr(4, no.length);
				
				if($('.app-chat[data-topic="' + no + '"]').length > 0) {
					list.children().removeClass('opacity');
					list.children().addClass('opacity');
					$('td[data-topic="' + no + '"]').removeClass('opacity');
					app.removeClass('hidden');
					$('#app-topic').addClass('hidden');
					
					app.children().addClass('hidden');
					$('.app-chat[data-topic="' + no + '"]').removeClass('hidden');
					
					textarea = no;
					return;
				} else {
					list.children().removeClass('opacity');
					list.children().addClass('opacity');
					
					let html = '<td data-topic="' + no + '"><div class="txt_line" title="' + title + '">' + title + '</div></td>';
					app.append('<div class="app-chat" data-topic="' + no + '"><div class="ChatList"></div><div class="UserList"><div class="UserCount">채팅중인 회원 : <span class="color-red"><span class="Counting">0</span> 명</span></div><div class="UsernickName"></div></div></div>');
					list.append(html);
					
					app.removeClass('hidden');
					$('#app-topic').addClass('hidden');
					
					app.children().addClass('hidden');
					$('.app-chat[data-topic="' + no + '"]').removeClass('hidden');
					// 첫입장
					textarea = no;
					sendMessage("connect", no, "Connect!!!");
				}
			
				if(list.children().length > 5) {
					alert("5개의 채널만 이용 가능 합니다.");
					return;
				}
				
				$('td').off().on('click', function() {
					let click_index = $(this).index();
					ChatingView(click_index);
				});
				
				function ChatingView(index) {
					let topic = $('td').eq(index).attr('data-topic');
					let app_topic = $('#app-topic');
					let app_chat = $('#app-chat');
					textarea = topic;
					
					$('td').siblings().addClass('opacity');
					
					if(topic == "main") {
						app_topic.removeClass('hidden');
						app_chat.addClass('hidden');
						
						app_topic.removeClass('opacity');
						$('#topic-list').removeClass('opacity');
					} else {
						app_chat.removeClass('hidden');
						app_topic.addClass('hidden');
						$('#app-chat').children().siblings().addClass('hidden');
						
						let app = $('#app-chat').children('div[data-topic="' + topic + '"]');
						app.removeClass('hidden');
						$('td').eq(index).removeClass('opacity');
					}
				}
				
				$('td').on('contextmenu', function() {
					return false;
				});
				
				$('td').on('mousedown', function(e) {
					if((e.button == 2) || (e.which == 3)) {
						e.preventDefault();
						
						let app = $(this);
						
						if(app.hasClass('topic-list')) {
							return;
						}
						
						let modal = $('#modal-cancel');
						modal.empty();
						
						let element = document.createElement("div");
						element.id = "cancel-modal";
						element.className = "cancel-modal";
						modal.append(element);
						
						$('#cancel-modal').append('<div id="topic-close">닫기</div><div id="topic-cancel">취소</div>')
						$('#cancel-modal').offset({ left: e.pageX, top: e.pageY });
						
						$('#cancel-modal').off().on('contextmenu', function() {
							return false;
						});
						
						$('#topic-cancel').off().on('click', function(e) {
							modal.empty();
						});
						
						// 퇴장
						// 퇴장 처리시  index chatview 처리
						$('#topic-close').off().on('click', function(e) {
							console.log(app);
							sendMessage("disconnect", app.attr('data-topic'), "Disconnect!!!");
							modal.empty();
							app.remove();
							$('.app-chat[data-topic="' + app.attr('data-topic') + '"]').remove();
						});
					}
				});

			});
		}
	});
}

function socket() {
	return a_getData("POST", "/logincheck", {}, function(data) {
		data = JSON.parse(data);
		if(data.status) {
			$('.loader').removeClass('display-none');
			Connect();
		}
	});
}

function Connect() {
//	Socket = new WebSocket("ws://dev.blackping.shop:8080/echo/websocket");
	Socket = new WebSocket("ws://socket.com:8080/echo/websocket");
	
	Socket.onopen = function () {
		$('.loader').addClass('display-none');
		topicselect();
		sendMessage = function sendMessage(type, roomNumber, msg) {
			Socket.send(type + "," + roomNumber + "," + msg);
		}
	}
	
	onMessage = function onMessage(msg) {
		let data = JSON.parse(msg.data);
		let type = data.type;
		let message = data.msg;
		
		let roomNumber = data.roomNumber;
		let userList = data.userList;
		let UserDrawing = "";
		
		let C_UserList = $('.app-chat[data-topic="' + roomNumber + '"]').children('.UserList');
		let C_ChatList = $('.app-chat[data-topic="' + roomNumber + '"]').children('.ChatList');
		
		if(type == "error") {
			Socket.close();
			alert(data.msg);
		}
		else if(type == "connect" || type == "disconnect") {
			$('.app-chat[data-topic="' + roomNumber + '"]').children().find('.Counting').text(data.count);
			for(var i = 0; i < userList.length; i++) {
				UserDrawing += '<div class="User" data-nickname="' + userList[i] + '">' + userList[i] + '</div>';
			}
			
			C_UserList.children('.UsernickName').empty();
			C_UserList.children('.UsernickName').append(UserDrawing);
			C_ChatList.append(message);
		} else if(type == "send") {
			let text = '<div>' + data.nickname + ' : ' + message + '</div>';
			$('.app-chat[data-topic="' + roomNumber + '"]').children('.ChatList').append(text);
		}
		
		$('.ChatList').scrollTop($('.ChatList').prop('scrollHeight'));
	}
	Socket.onmessage = onMessage;
}