package com.cssweb.android.quote;

import com.cssweb.android.connect.RequestParams;

public class SQS extends QHSCBaseActivity {

	protected void onclickMenu(RequestParams requestParams , int which) {
		switch (which) {//setMarket 在这里的作用相当于setKind() 
		case 0:
			requestParams.setMarket("sfcu");
			break;
		case 1:
			requestParams.setMarket("sfal");
			break;
		case 2:
			requestParams.setMarket("sfzn");
			break;
		case 3:
			requestParams.setMarket("sfpb");
			break;
		case 4:
			requestParams.setMarket("sfau");
			break;
		case 5:
			requestParams.setMarket("sffu");
			break;
		case 6:
			requestParams.setMarket("sfru");
			break;
		case 7:
			requestParams.setMarket("sfrb");
			break;
		case 8:
			requestParams.setMarket("sfwr");
			break;
		}
	}

}
