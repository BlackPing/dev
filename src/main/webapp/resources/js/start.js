let Socket;
let sendMessage = function(type, roomNumber, msg) { };
let onMessage = function(msg) { };
let textarea = 0;
let status = false;
let click_index = 0;
let Option = {};

$(document).ready(function() {
//	async function asyncOpen() { // 비동기 처리
//		const t1 = await socket();
//		const t2 = await select();
//	}
	CattingOption();
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
        		sendMessage("send", textarea, text, Option);
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
						select();
						topicselect();
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
				
				if(list.children().length > 5) {
					alert("5개의 채널만 이용 가능 합니다.");
					return;
				}
				
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
					$('.app-chat[data-topic="' + no + '"]').children('.ChatList').append('<div class="text-center connect">' + title + ' 채널 입니다 즐거운 채팅 되세요~!<div>');
					// 첫입장
					textarea = no;
					click_index = list.children().length - 1;
					sendMessage("connect", no, "Connect!!!", Option);
				}
			
				
				$('td').off().on('click', function() {
					click_index = $(this).index();
					ChatingView(click_index);
				});
				
				function ChatingView(index) {
					let topic = $('td').eq(index).attr('data-topic');
					let app_topic = $('#app-topic');
					let app_chat = $('#app-chat');
					textarea = topic;
					
					$('td').siblings().addClass('opacity');
					
					if(topic == "main") {
						textarea = 0;
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
							ChatingView(--click_index);
							sendMessage("disconnect", app.attr('data-topic'), "Disconnect!!!", Option);
							modal.empty();
							app.remove();
							$('.app-chat[data-topic="' + app.attr('data-topic') + '"]').remove();
						});
					}
				});

				$('#clean').off().on('click', function(e) {
					if(textarea == 0) return;
					$('.app-chat[data-topic="' + textarea + '"]').children('.ChatList').empty();
					$('.app-chat[data-topic="' + textarea + '"]').children('.ChatList').append('<div class="text-center">채팅창을 청소했습니다.</div>');
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
		sendMessage = function sendMessage(type, roomNumber, msg, Option) {
			Socket.send(type + "/" + roomNumber + "/" + JSON.stringify(Option)  + "/" + msg);
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
			
			$('.User').off().on('click', function() {
				let element = document.createElement('div');
				element.id = "user-modal";
				
				$('#user-add-modal').append(element);
				let modal = $('#user-modal');
			});

		} else if(type == "send") {
			$('.app-chat[data-topic="' + roomNumber + '"]').children('.ChatList').append(message);
			
			let P_height = $('.chatmsg').last().parent().height() - 3;
			$('.chatnick').last().css('position', 'relative');
			$('.chatnick').last().css('bottom', P_height/2 - $('.chatnick').last().height()/2);
		}
		
		$('.ChatList').scrollTop($('.ChatList').prop('scrollHeight'));
	}
	Socket.onmessage = onMessage;
}

function CattingOption() {
	let font_family = $('#font-family').val();
	let font_size = $('#font-size').val();
//	font_size = font_size.substr(0, font_size.lastIndexOf(' '));
	let font_color = '#' + $('#font-color').val();
	let back_color = '#' + $('#back-color').val();
	let font_strong = $('#font-strong').is(":checked");
	let font_i = $('#font-i').is(":checked");
	let font_underline = $('#font-underline').is(":checked");
	let app = $('#chatting');
	
	app.css('font-family', font_family);
	app.css('font-size', font_size);
	app.css('color', font_color);
	if(font_strong)
		app.css('font-weight', 700);
	else
		app.css('font-weight', '');
	
	if(font_i)
		app.css('font-style', 'italic');
	else
		app.css('font-style', '');
	
	if(font_underline)
		app.css('text-decoration', 'underline');
	else
		app.css('text-decoration', '');
	
	Option = {
			"font-family": font_family,
			"font-size": font_size,
			"color": font_color,
			"font-weight": font_strong,
			"font-style": font_i,
			"text-decoration": font_underline,
			"background-color": back_color
	}
}