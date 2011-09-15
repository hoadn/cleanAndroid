package com.cssweb.android.quote;

import com.cssweb.android.connect.RequestParams;

public class ZSS extends QHSCBaseActivity {

	protected void onclickMenu(RequestParams requestParams , int which) {
		switch (which) {
		case 0:
			requestParams.setMarket("czro");
			break;
		case 1:
			requestParams.setMarket("czws");
			break;
		case 2:
			requestParams.setMarket("czwt");
			break;
		case 3:
			requestParams.setMarket("czcf");
			break;
		case 4:
			requestParams.setMarket("czsr");
			break;
		case 5:
			requestParams.setMarket("czta");
			break;
		case 6:
			requestParams.setMarket("czer");
			break;
		}
	}

}
