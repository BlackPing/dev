$(document).ready(function() {
	console.log("test3");
	select();
});

function select() {
	let flag = false;
	getData("POST", "/topicsl", {}, function(data) {
		data = JSON.parse(data);
		console.log(data);
		$('#channel').empty();
	
		let html;
		for(var i = 0; i < data.select.result.length; i++) {
			html = '<div class="channel-topic">';
			html += '<span class="channel-no">NO: ' + data.select.result[i].NO + '</span>';
			html += '<span class="channel-master">Master: ' + data.select.result[i].NICKNAME + '</span>';
			html += '<div class="channel-title">' + data.select.result[i].TITLE + '</div>';
			
			if(data.topic.result != undefined) {
				for(var j = 0; j < data.topic.result.length; j++) {
					if(data.topic.result[j].TOPIC_NO == data.select.result[i].NO) {
						html += '<button class="channel-btn"><img src="/res/img/checked-symbol.png" height="20px" style="margin-top: 2px;"></button>';
						flag = true;
						break;
					}
				}
			}
			if(!flag) {
				html += '<button class="channel-btn">구독</button>';
			}
			
			$('#channel').append(html);
			html = "";
			flag = false;
		}
		
		$('.channel-btn').off().on('click', function(e) {
			let no = $(e.target).parent().children('.channel-no').text();
			no = no.substr(4, no.length);
			getData("POST", "topicup", {"no": no}, function(data) {
				console.log(data);
			});
		});
	});
}