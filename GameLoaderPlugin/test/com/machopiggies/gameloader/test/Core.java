package com.machopiggies.gameloader.test;

import org.junit.Test;

import java.util.Properties;

public class Core {

    @Test
    public void test() {
        Properties properties = new Properties();
        properties.setProperty("Hello", "Hi");
        System.out.println(properties.stringPropertyNames().contains("Hello"));
    }
}
