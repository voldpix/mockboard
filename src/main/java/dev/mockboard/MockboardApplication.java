package dev.mockboard;

import dev.mockboard.app.MockboardApp;

import java.util.TimeZone;

public class MockboardApplication {

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        MockboardApp.create().start(MockboardApp.port());
    }

}
