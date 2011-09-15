

/**
 * 将当前页面跳转到指定url 并且保留滑动显示效果  
 * @param {} url 跳转的链接
 */
function commonUrlChange(url){
	
	var thisUrl = window.location.href;
	if(thisUrl.indexOf("#")!=-1){
		thisUrl = thisUrl.substring(0,thisUrl.indexOf("#"));
	}
	window.location.href = thisUrl + "#" + url;
}

/**
 * 将当前页面跳转到指定url 并且保留滑动显示效果  
 * @param {} url 跳转的链接
 * @param {} id  参数名为id所对应的值 无值情况下不执行
 */
function goBranchList(url,id){
	if(id==null || id==""){
		return
	}
	
	var thisUrl = window.location.href;
	if(thisUrl.indexOf("#")!=-1){
		thisUrl = thisUrl.substring(0,thisUrl.indexOf("#"));
	}
	window.location.href = thisUrl + "#" + url + "?id=" +  id+"&ran=" + Math.random();
}

