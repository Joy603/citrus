/*
 * Copyright 2006-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.consol.citrus.selenium.actions;

import com.consol.citrus.context.TestContext;
import com.consol.citrus.exceptions.CitrusRuntimeException;
import com.consol.citrus.selenium.endpoint.SeleniumBrowser;
import org.openqa.selenium.JavascriptExecutor;
import org.springframework.util.StringUtils;

import java.util.Set;

/**
 * @author Tamer Erdogan, Christoph Deppisch
 * @since 2.7
 */
public class OpenWindowAction extends AbstractSeleniumAction {

    private String windowName = "selenium_popup_window";

    /**
     * Default constructor.
     */
    public OpenWindowAction() {
        super("open-window");
    }

    @Override
    protected void execute(SeleniumBrowser browser, TestContext context) {
        Set<String> windowHandles = browser.getWebDriver().getWindowHandles();
        String newWindow = null;
        String lastWindow = browser.getWebDriver().getWindowHandle();
        context.setVariable("selenium_last_window", lastWindow);

        ((JavascriptExecutor) browser.getWebDriver()).executeScript("window.open();");

        Set<String> newWindowHandles = browser.getWebDriver().getWindowHandles();

        for (String window : newWindowHandles) {
            if (!windowHandles.contains(window)) {
                newWindow = window;
            }
        }

        if (!StringUtils.isEmpty(newWindow)) {
            browser.getWebDriver().switchTo().window(newWindow);
            log.info("Open window: " + newWindow);
            context.setVariable("selenium_active_window", newWindow);
            context.setVariable(windowName, newWindow);
        } else {
            throw new CitrusRuntimeException("Failed to open new window");
        }

    }

    public void setWindowName(String windowName) {
        this.windowName = windowName;
    }

    public String getWindowName() {
        return windowName;
    }
}