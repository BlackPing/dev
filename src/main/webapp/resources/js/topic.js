console.log("topic");
var type = false;
function nickname() {
	let modal = $('#modal');
	console.log($('#modal'));
	var element = document.createElement("div");
	console.log(element);
	
	if(!type) {
		type = true;
		console.log("create");
		modal.append(element);
	} else {
		type = false;
		console.log("remove");
		modal.find("div").remove("div");
	}
	
}