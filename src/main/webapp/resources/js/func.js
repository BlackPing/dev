function getWeek(year, month, day) {
	let week = ['일', '월', '화', '수', '목', '금', '토'];
	let date = new Date(year, (month - 1), day);
	
	let start = date.getDate() - date.getDay();
	let end = start + 7;
	
	// 해당 주 기준
/* 				for(var i = start; i < end; i++) {
					date.setMonth(month - 1);
					date.setDate(start++);
					console.log(date.getFullYear(), date.getMonth() + 1, date.getDate(), week[date.getDay()]);
				} */
	// 다음주 까지
	for(var i = 0; i < 7; i++) {
		if(i > 0) date.setDate(date.getDate() + 1);
		console.log(date.getFullYear(), date.getMonth() + 1, date.getDate(), week[date.getDay()]);
	}
}