package org.niooii.chromedriver;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.Collections;

public class ChromeDriverInit {
    ChromeOptions options = new ChromeOptions();
    public ChromeDriverInit(){
        System.setProperty("webdriver.chrome.driver", "src/main/java/org/niooii/chromedriver/chromedriver.exe");
        System.setProperty("webdriver.chrome.silentOutput", "true");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--disable-in-process-stack-traces");
        options.addArguments("--disable-logging");
        options.addArguments("--log-level=OFF");
        options.addArguments("--silent");

        // trying to trick bot detection
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));
        options.setExperimentalOption("useAutomationExtension", null);
        options.addArguments("window-size=1920,1080");
        options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36");
        options.addArguments("disable-infobars");
        options.addArguments("start-maximized");
    }

    public ChromeDriver getChromeDriver(){
        return new ChromeDriver(options);
    }

}