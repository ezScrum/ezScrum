#!/usr/bin/python
# -*- coding: utf-8 -*-
from Selenium2Library import Selenium2Library
from selenium.webdriver import ActionChains
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.common.by import By


class Selenium2Improved(Selenium2Library):

    '''Sometimes Selenium2Library just dont go far enough.'''

    def __init__(self, timeout=5.0, implicit_wait=0.0,
                 run_on_failure='Capture Page Screenshot'):
        super(Selenium2Improved, self).__init__()

    def open_tab(self, url, alias):
        """
        Selenium1 的 Open Window 功能已被移除
        所以 Selenium2Improved 改由自己實作
        以執行 JS 的方式來實作開新 Tab 的功能
        開完新 Tab 後會將舊有的 Tab Title 重新命名成 Original Tab
        借此區別新舊 Tab
        """
        driver = self._current_browser()
        driver.execute_script("window.open('" + url + "', '_blank');")
    
    def press_shift_and_click_element(self, locator):
        if locator.startswith("xpath="):
            web_element = self._current_browser().find_element(By.XPATH, locator[len("xpath="):])
            actions = ActionChains(self._current_browser())
            actions.key_down(Keys.SHIFT).click(web_element).key_up(Keys.SHIFT).perform()
    def press_control_and_click_element(self, locator):
        if locator.startswith("xpath="):
            web_element = self._current_browser().find_element(By.XPATH, locator[len("xpath="):])
            actions = ActionChains(self._current_browser())
            actions.key_down(Keys.CONTROL).click(web_element).key_up(Keys.CONTROL).perform()
