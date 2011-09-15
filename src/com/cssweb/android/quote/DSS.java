package com.cssweb.android.quote;

import com.cssweb.android.connect.RequestParams;

public class DSS extends QHSCBaseActivity {

	protected void onclickMenu(RequestParams requestParams , int which) {
		switch (which) {
		case 0:
			requestParams.setMarket("dca");
			break;
		case 1:
			requestParams.setMarket("dcb");
			break;
		case 2:
			requestParams.setMarket("dcm");
			break;
		case 3:
			requestParams.setMarket("dcy");
			break;
		case 4:
			requestParams.setMarket("dcc");
			break;
		case 5:
			requestParams.setMarket("dcp");
			break;
		case 6:
			requestParams.setMarket("dcl");
			break;
		case 7:
			requestParams.setMarket("dcv");
			break;
		case 8:
			requestParams.setMarket("dcj");
			break;
		}
	}

}
