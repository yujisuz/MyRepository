package com.cisco.jptac.vsec;

import com.cisco.jptac.vsec.http.JptacVsecHttpServer;

public class JptacVsecWebhookRecierver {

    public static void main(String[] args) throws Exception {
        System.out.println("start >>>");

        JptacVsecHttpServer server = new JptacVsecHttpServer();
        server.start();
    }
}
