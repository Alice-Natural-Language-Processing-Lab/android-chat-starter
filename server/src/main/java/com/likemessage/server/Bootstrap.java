package com.likemessage.server;

import com.generallycloud.nio.extend.startup.BaseServerStartup;

public class Bootstrap {

	public static void main(String[] args) throws Exception {

		BaseServerStartup s = new BaseServerStartup();

		s.launch("");
	}

}
