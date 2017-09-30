# -*- coding: utf-8 -*-
from selenium import webdriver
from selenium.webdriver.chrome.options import Options
import unittest
import time


class ReturnTrueTest(unittest.TestCase):

    def setUp(self):
        self.driver = webdriver.Chrome()
        self.driver.implicitly_wait(30)
        self.verificationErrors = []
        self.accept_next_alert = True

    def test_Order(self):
        driver = self.driver
        driver.implicitly_wait(15) 
        driver.get('http://returntrue123456.azurewebsites.net/booking.html')
        driver.find_element_by_xpath('//*[@id="tags"]').send_keys('單身')
        time.sleep(1)
        driver.find_element_by_xpath('/html/body/div/div[2]/div/div/button').click()
        actual = driver.find_element_by_xpath('//*[@id="swal2-title"]').text
        expected = '購票成功'
        self.assertEqual(actual, expected)
        time.sleep(3)

if __name__ == '__main__':
    suite = unittest.TestSuite()
    suite.addTest(ReturnTrueTest("test_Order")) 
    unittest.TextTestRunner(verbosity=2).run(suite)




