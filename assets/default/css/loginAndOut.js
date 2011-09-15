	var a = 0;

	var XMLHttpReqLoginAndOutServlet;
	//创建XMLHttpRequest对象
    function createXMLHttpRequestLoginAndOutServlet() {
		if(window.XMLHttpRequest) { //Mozilla 浏览器
			XMLHttpReqLoginAndOutServlet = new XMLHttpRequest();
		}
		else if (window.ActiveXObject) { // IE浏览器
			try {
				XMLHttpReqLoginAndOutServlet = new ActiveXObject("Msxml2.XMLHTTP");
			} catch (e) {
				try {
					XMLHttpReqLoginAndOutServlet = new ActiveXObject("Microsoft.XMLHTTP");
				} catch (e) {}
			}
		}
	}

	//发送请求函数
	function sendRequestLoginAndOutServlet() {
		createXMLHttpRequestLoginAndOutServlet();  
		var strurl = location.href;
		strurl=strurl.replace(/&/g,"a:a")
        var url = '/iphone/loginAndOut.jsp?url='+strurl+'&a='+a;
		XMLHttpReqLoginAndOutServlet.open("post", url, true);
		XMLHttpReqLoginAndOutServlet.send(null);// 发送请求
	}

	if (a==null || a==''){
		a=0;
	}
	a=a+1;
	sendRequestLoginAndOutServlet();
