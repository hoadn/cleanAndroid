function openpdf(docId,title){
	var obj = new JSBridgeObj();
	obj.addObject("url","http://iweb.njzq.cn/iphone/DocumentViewBean.do?docId=" + docId);
	obj.addObject("title", title);
	obj.addObject("task", "open_pdf");
	obj.sendBridgeObject();
}
function openfaqview(id,title){
	var obj = new JSBridgeObj();
	obj.addObject("url","http://iweb.njzq.cn/iphone/zxhd/faq_view.jsp?id=" + id);
	obj.addObject("title", title);
	obj.addObject("task", "open_pdf");
	obj.sendBridgeObject();
}
function openpdf1(docId,title){
	var obj = new JSBridgeObj();
	obj.addObject("url","http://iweb.njzq.cn/iphone/DocumentViewBean1.do?docId=" + docId);
	obj.addObject("title", title);
	obj.addObject("task", "open_pdf");
	obj.sendBridgeObject();
}

function openpdf_qx(docId,title){
	var obj = new JSBridgeObj();
	obj.addObject("url","http://iweb.njzq.cn/iphone/common/newsView.jsp?docId=" + docId);
	obj.addObject("title", title);
	obj.addObject("task", "open_pdf");
	obj.sendBridgeObject();
}

function openjytd(id,title){
	var obj = new JSBridgeObj();
	obj.addObject("url","http://iweb.njzq.cn/iphone/nzfc/team_memberIntro.jsp?id=" + id);
	obj.addObject("title", title);
	obj.addObject("task", "open_pdf");
	obj.sendBridgeObject();
}


function openvideo(url){
	if(url==''||url==null){
		return;
	}
	var obj = new JSBridgeObj();
	obj.addObject("url",url);
	obj.addObject("task", "open_video");
	obj.sendBridgeObject();
} 

function openggzd(stockid,title){
	var obj = new JSBridgeObj();
	obj.addObject("url","http://iweb.njzq.cn/iphone/zxlp/guGe_zdGupiao.jsp?stock_id="+stockid);
	obj.addObject("title", title);
	obj.addObject("task", "open_pdf");
	obj.sendBridgeObject();
}

function openggzdjj(stockid,title){
	var obj = new JSBridgeObj();
	obj.addObject("url","http://iweb.njzq.cn/iphone/zxlp/guGe_zdJijin.jsp?stock_id="+stockid);
	obj.addObject("title", title);
	obj.addObject("task", "open_pdf");
	obj.sendBridgeObject();
}

function openalert(title,content,buttontitle){
	var obj = new JSBridgeObj();
	obj.addObject("title",title);
	obj.addObject("content", content);
	obj.addObject("buttontitle", buttontitle);
	obj.addObject("task", "alert");
	obj.sendBridgeObject();
}

function opendate(name){
	var obj = new JSBridgeObj();
	obj.addObject("name", name);
	obj.addObject("task", "open_date");
	obj.sendBridgeObject();
}

function login(type){
	var obj = new JSBridgeObj();
	obj.addObject("name", type);//1 交易密码；2 服务密码；3 体验用户
	obj.addObject("task", "back_login");
	obj.sendBridgeObject();
}

function login_url(type,url){
	var obj = new JSBridgeObj();
	obj.addObject("name", type);//1 交易密码；2 服务密码；3 体验用户
	obj.addObject("url", url);
	obj.addObject("task", "back_login_url");
	obj.sendBridgeObject();
}

function returnApps(){
	var obj = new JSBridgeObj();
	obj.addObject("task", "back");
	obj.sendBridgeObject();
}

function locationhref(url){
	var obj = new JSBridgeObj();
	obj.addObject("url",url);
	obj.addObject("task", "open_href");
	obj.sendBridgeObject();
}

function openQuickMenu(){
	var obj = new JSBridgeObj();
	obj.addObject("task", "open_quickmenu");
	obj.sendBridgeObject();
}

/**
 * 调用客户端中展现营业部地图
 * @param {} address 地址
 * @param {} lat	纬度
 * @param {} lng	经度
 */
function openmap(name,address,lat,lng){
	var obj = new JSBridgeObj();
	obj.addObject("name",name);
	obj.addObject("address",address);
	obj.addObject("lat",lat);
	obj.addObject("lng",lng);
	obj.addObject("task", "open_map");
	obj.sendBridgeObject();
}

function opencall(tel){
	var obj = new JSBridgeObj();
	obj.addObject("tel",tel);
	obj.addObject("task", "open_call");
	obj.sendBridgeObject();
}

function openkeyboard(name){
	var obj = new JSBridgeObj();
	obj.addObject("name",name);
	obj.addObject("task", "open_keyboard");
	obj.sendBridgeObject();
}

//消息提示
function errorAlert(text){
	$("<div class='ui-loader ui-overlay-shadow ui-body-e ui-corner-all'><h1>"+text+"</h1></div>")
		.css({"color":"red", "display": "block", "opacity": 0.96, "top": $(window).scrollTop() + 100 })
		.appendTo( $(document.body) )
		.delay( 800 )
		.fadeOut( 400, function(){
		$(this).remove();
	});	
}


//切换选项卡
function changeTabDiv(objOne,objTwo){
	$("#"+objOne+"Tab").css("display","none");
	$("#"+objTwo+"Tab").css("display","none");
	
	$("#"+objOne+"Div").css("display","none");
	$("#"+objTwo+"Div").css("display","none");
	
	$("#"+objTwo+"Tab").css("display","block");
	$("#"+objTwo+"Div").css("display","block");
}
//自选股 
function returnOpenZxg(){
	var obj = new JSBridgeObj();
	obj.addObject("task", "open_zxg");
	obj.sendBridgeObject();
}
//持仓
function returnOpenPosition(){
	var obj = new JSBridgeObj();
	obj.addObject("task", "open_position");
	obj.sendBridgeObject();
}
//自定义快捷栏目
function lCustomQuickColumns(url){
	var obj = new JSBridgeObj();
	obj.addObject("task", "open_href_blank");
	obj.addObject("url", url);
	obj.sendBridgeObject();
}

//获得当前位置经纬度 
function getCurrentLocation(){
	var obj = new JSBridgeObj();
	obj.addObject("task", "get_location");
	obj.sendBridgeObject();
}
//掌上营业厅
function gotoZsyyt(){
	var obj = new JSBridgeObj();
	obj.addObject("task", "open_zsyyt");
	obj.sendBridgeObject();
}

function openFxpc(msg, type, operation){
	var obj = new JSBridgeObj();
	obj.addObject("task", "fxpc");
	obj.addObject("msg", msg);
	obj.addObject("type", type);
	obj.addObject("operation", operation);
	obj.sendBridgeObject();
}

/**
 * 
 * 必须配合confirmTrue(key) confirmFalse(key)方法同时使用
 * @param {} title 标题
 * @param {} content 内容
 * @param {} key	key值，会原封不动的回传，用以判别确定后执行哪个方法
 * 
 * 
 */
function opernconfirm(title, content, key){
	var obj = new JSBridgeObj();
	obj.addObject("title",title);
	obj.addObject("content", content);
	obj.addObject("key", key);
	obj.addObject("task", "confirm");
	obj.sendBridgeObject();
}

function showconfirm(content){
	opernconfirm("信息",content,"key");
}

//积分弹出层
function show_div(){
	var obj = new JSBridgeObj();
	obj.addObject("task", "show_div");
	obj.sendBridgeObject();
}