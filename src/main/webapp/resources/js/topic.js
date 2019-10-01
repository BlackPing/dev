var nickname_modal = false;
var topic_modal = false;
function topicadd(event) {
	let modal = $('#topic-add-modal');
	
	let element = document.createElement("div");
	element.id = "topic-modal";
	element.className = "topic-modal";
	modal.append(element);
	
	if(!topic_modal) {
		topic_modal = true;
		let html = '<div class="topic-modal-top">';
		html += '<span>구독 채널</span>';
		html += '</div>';
		html += '<div class="topic-modal-middle">';
		html += '<input id="topic-room" type="text" maxlength="10" oninput="MaxLength(this)" >';
		html += '</div>';
		html += '<div class="topic-modal-bottom">';
		html += '<button id="add-room" class="button button1">추가</button>';
		html += '</div>';
		$('#topic-modal').append(html);
		$('header').prepend('<div id="display"></div>');
		$('#topic-add-modal').animate({ height: 0 }, 0);
		$('#topic-add-modal').animate({ height: "+=250px" }, 500);
		
		$('#display').off().on('click', function() {
			topic_modal = false;
			$('header').find('#display').remove();
			$('#topic-add-modal').empty();
		});
		$('#add-room').off().on('click', function() {
			if($('#topic-room').val() == "") {
				alert("공백은 입력할 수 없습니다.");
				return;
			}
			
			getData("POST", "/topicroom", {roomname: $('#topic-room').val()}, function(data) {
				data = JSON.parse(data);
				console.log(data);
				if(data.status == false) {
					if(!getMessage(data.msg)) {
						alert("네트워크 오류입니다.");
					}
				} else {
					topic_modal = false;
					$('#display').remove();
					modal.empty();
					getMessage(data.msg);
					select();
				}
			});
		});
	} else {
		topic_modal = false;
		modal.empty();
	}
}

function nickname(event) {
	let modal = $('#modal');
	
	let element = document.createElement("div");
	element.id = "nickname-modal";
	element.className = "nickname-modal";

	if(!nickname_modal) {
		nickname_modal = true;
		modal.append(element);
		let html = '<span style="cursor: default;">닉네임 변경</span>';
		html += '<img src="/res/img/cancel.png" id="nickname-cancel">';
		html += '<input id="nickname-text" type="text" maxlength="10" oninput="MaxLength(this)" />';
		html += '<span id="nick-cert-btn">인증</span>';
		$('#nickname-modal').append(html);
		$('#nickname-modal').offset({left: event.pageX, top: event.pageY + 49});
		$('#nickname-modal').animate({opacity: "0.1"}, 1);
		$('#nickname-modal').animate({ top: "-=49px", opacity: "1" }, 200);
		$("#nick-cert-btn").off().on("click", function(event) {
			if($('#nickname-text').val() == "") {
				alert("공백은 입력할 수 없습니다.");
				return;
			}
			
			getData("POST", "/cert", {nickname: $('#nickname-text').val()}, function(data) {
				data = JSON.parse(data);
				console.log(data);
				if(data.status == false) {
					if(!getMessage(data.msg)) {
						alert("네트워크 오류입니다.");
					}
				} else {
					getMessage(data.msg);
					location.reload();
				}
			});
		});
		$("#nickname-cancel").off().on("click", function(event) {
			modal.empty();
			nickname_modal = false;
		});
	} else {
		nickname_modal = false;
		modal.empty();
	}
}