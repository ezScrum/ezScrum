from Selenium2Library import Selenium2Library
from selenium.webdriver.common.keys import Keys
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

    def open_tab(self, url=''):
        driver = self._current_browser()
        print 'Open tab start'
        ActionChains(driver).send_keys(Keys.COMMAND, 't').perform()
        print 'Open tab end'
        # body = driver.find_element_by_tag_name("body")
        # body.send_keys(Keys.CONTROL + 't')
