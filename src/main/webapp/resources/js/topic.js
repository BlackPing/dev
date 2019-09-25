console.log("topi2c");
var type = false;
function nickname(event) {
	let modal = $('#modal');
	
	let element = document.createElement("div");
	element.id = "nickname-modal";
	element.className = "nickname-modal";

	if(!type) {
		type = true;
		modal.append(element);
		let html = '<span style="cursor: default;">닉네임 변경</span>';
		html += '<img src="/res/img/cancel.png" id="nickname-cancel">';
		html += '<input type="text" />';
		html += '<span id="nick-cert-btn">인증</span>';
		$('#nickname-modal').append(html);
		$('#nickname-modal').offset({left: event.clientX, top: event.clientY + 50});
		$('#nickname-modal').animate({opacity: "0.1"}, 1);
		$('#nickname-modal').animate({ top: "-=49px", opacity: "1" }, "slow" );
		
		$("#nick-cert-btn").off().on("click", function(event) {
			getData("POST", "/cert", {test:"test"}, function(data) {
				console.log("success");
				console.log(data);
			})
		});
		$("#nickname-cancel").off().on("click", function(event) {
			modal.find("#nickname-modal").remove("div");
			type = false;
		});
	} else {
		type = false;
		modal.find("#nickname-modal").remove("div");
	}
	
}