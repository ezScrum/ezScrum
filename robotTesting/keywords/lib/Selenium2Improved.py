#!/usr/bin/python
# -*- coding: utf-8 -*-
from Selenium2Library import Selenium2Library
from selenium.webdriver.common.action_chains import ActionChains


class Selenium2Improved(Selenium2Library):

    '''Sometimes Selenium2Library just dont go far enough.'''

    def __init__(self, timeout=5.0, implicit_wait=0.0,
                 run_on_failure='Capture Page Screenshot'):
        super(Selenium2Improved, self).__init__()

    def mouse_down_at(self, locator, coordx, coordy):
        element = self._element_find(locator, True, False)
        if element is None:
            raise AssertionError("ERROR: Element %s not found." % (locator))
        ActionChains(self._current_browser()).move_to_element(
            element).move_by_offset(coordx, coordy).click_and_hold().perform()

    def mouse_up_at(self, locator, coordx, coordy):
        element = self._element_find(locator, True, False)
        if element is None:
            raise AssertionError("ERROR: Element %s not found." % (locator))
        ActionChains(self._current_browser()).move_to_element(
            element).move_by_offset(coordx, coordy).release().perform()

    def open_tab(self, url, alias):
        """
        Selenium1 的 Open Window 功能已被移除
        所以 Selenium2Improved 改由自己實作
        以執行 JS 的方式來實作開新 Tab 的功能
        開完新 Tab 後會將舊有的 Tab Title 重新命名成 Original Tab
        借此區別新舊 Tab
        """
        driver = self._current_browser()
        driver.execute_script("window.open('" + url + "');")

        driver.implicitly_wait(1)
        driver.execute_script(
            "document.title = 'Original Tab';")
