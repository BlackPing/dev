let Socket;
let sendMessage = function(type, roomNumber, msg) { };
let onMessage = function(msg) { };
let textarea = 0;
$(document).ready(function() {
	console.log("test3");
	
	
	async function asyncOpen() { // 비동기 처리
		const t1 = await socket();
		const t2 = await select();
	}
	
	asyncOpen();
	
	$('textarea').on('keydown', function(e) {
		let text = "";
        if (e.keyCode == 13) {
        	if (!e.shiftKey){
        		e.preventDefault();
        		text = $('textarea').val();
        		$('textarea').val('');
        		
        		if(textarea == 0) return;
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
});

function select() {
	let flag = false;
	return getData("POST", "/topicsl", {}, function(data) {
		data = JSON.parse(data);
		$('#channel').empty();
		$('#app-topic').empty();
		console.log("select");
		let html;
		let inghtml;
		for(var i = 0; i < data.select.result.length; i++) {
			html = '<div class="channel-topic">';
			html += '<span class="channel-no">NO: ' + data.select.result[i].NO + '</span>';
			html += '<span class="channel-master">Master: ' + data.select.result[i].NICKNAME + '</span>';
			html += '<div class="channel-title">' + data.select.result[i].TITLE + '</div>';
			
			
			if(data.topic.result != undefined) {
				for(var j = 0; j < data.topic.result.length; j++) {
					if(data.topic.result[j].TOPIC_NO == data.select.result[i].NO) {
						html += '<button class="channel-btn"><img src="/res/img/checked-symbol.png" height="20px" style="margin-top: 2px;"></button>';
						inghtml = '<div class="app-topic-list">';
						inghtml += '<span>NO: ' + data.select.result[i].NO + '</span> ';
						inghtml += '<span>' + data.select.result[i].TITLE + '</span><span class="topic-up">참여</span>';
						inghtml += '</div>';
						flag = true;
						break;
					}
				}
			}
			if(!flag) {
				html += '<button class="channel-btn">구독</button>';
			}
			
			$('#channel').append(html);
			$('#app-topic').append(inghtml);
			html = "";
			inghtml = "";
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
				app.removeClass('display-none');
				$('#app-topic').addClass('display-none');
				
				app.children().addClass('display-none');
				$('.app-chat[data-topic="' + no + '"]').removeClass('display-none');
				
				textarea = no;
				return;
			} else {
				list.children().removeClass('opacity');
				list.children().addClass('opacity');
				
				let html = '<td data-topic="' + no + '"><div class="txt_line" title="' + title + '">' + title + '</div></td>';
				app.append('<div class="app-chat" data-topic="' + no + '"><div class="ChatList"></div><div class="UserList"></div></div>');
				list.append(html);
				
				app.removeClass('display-none');
				$('#app-topic').addClass('display-none');
				
				app.children().addClass('display-none');
				$('.app-chat[data-topic="' + no + '"]').removeClass('display-none');
				// 첫입장
				textarea = no;
				sendMessage("connect", no, "Connect!!!");
			}
			
			if(list.children().length > 5) {
				alert("5개의 채널만 이용 가능 합니다.");
				return;
			}
			
			$('td').off().on('click', function() {
				let topic = $(this).attr('data-topic');
				if(topic == 'main') {
					list.children().removeClass('opacity');
					list.children().addClass('opacity');
					$(this).removeClass('opacity');
					
					app.addClass('display-none');
					$('#app-topic').removeClass('display-none');
				} else {
					list.children().removeClass('opacity');
					list.children().addClass('opacity');
					$(this).removeClass('opacity');
					
					app.removeClass('display-none');
					$('#app-topic').addClass('display-none');
					
					app.children().addClass('display-none');
					$('.app-chat[data-topic="' + topic + '"]').removeClass('display-none');
					
					textarea = topic;
				}
			});
			
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
					
					$('#modal-cancel').empty();
					$('#modal-cancel').append('<div id="topic-close">닫기</div><div id="topic-cancel">취소</div>')
					$('#modal-cancel').offset({ left: e.pageX, top: e.pageY });
					
					$('#modal-cancel').off().on('contextmenu', function() {
						  return false;
					});
					
					$('#topic-cancel').off().on('click', function(e) {
						$('#modal-cancel').empty();
					});
					
					// 퇴장
					$('#topic-close').off().on('click', function(e) {
						sendMessage("disconnect", app.attr('data-topic'), "Disconnect!!!");
						$('#modal-cancel').empty();
						app.remove();
						$('.app-chat[data-topic="' + app.attr('data-topic') + '"]').remove();
					});
				
				}
			});
		});
	});
}

function socket() {
	return getData("POST", "/logincheck", {}, function(data) {
		console.log("socket");
		data = JSON.parse(data);
		if(data.status) {
			Socket = new WebSocket("ws://dev.blackping.shop:8080/echo/websocket");
			
			Socket.onopen = function () {
			}
			
			sendMessage = function sendMessage(type, roomNumber, msg) {
				Socket.send(type + "," + roomNumber + "," + msg);
			}
			
			onMessage = function onMessage(msg) {
				let data = JSON.parse(msg.data);
				console.log(data);
				
				let type = data.type;
				let message = data.msg;
				let nickname = '<div data-nickname="' + data.nickname + '">' + data.nickname + '</div>';
				let roomNumber = data.roomNumber;
				
				if(type == "connect") {
					$('.app-chat[data-topic="' + roomNumber + '"]').children('.ChatList').append(message);
					$('.app-chat[data-topic="' + roomNumber + '"]').children('.UserList').append(nickname);
				} else if(type == "disconnect") {
					$('.app-chat[data-topic="' + roomNumber + '"]').children('.ChatList').append(message);
					$('.app-chat[data-topic="' + roomNumber + '"]').children('div[data-nickname="' + nickname + '"]').remove();
				} else if(type == "send") {
					console.log("test");
					let text = '<div>' + data.nickname + ' : ' + message + '</div>';
					$('.app-chat[data-topic="' + roomNumber + '"]').children('.ChatList').append(text);
				}
				
			}			
			Socket.onmessage = onMessage;
		}
	});
}