from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
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
        driver.find_element_by_xpath('/html/body/div/div[5]/div/ul/li[1]/div/div/div[1]/select/option[2]').click()
        time.sleep(1)
        driver.find_element_by_xpath('/html/body/div/div[5]/div/ul/li[1]/div/div/div[2]/select/option[2]').click()
        time.sleep(1)
        driver.find_element_by_xpath('//*[@id="tags"]').send_keys('單身')
        time.sleep(1)
        driver.find_element_by_xpath('/html/body/div/div[5]/div/div/button').click()
        try:
            wait_actual = WebDriverWait(driver, 120).until(
                EC.presence_of_element_located((By.ID, "swal2-title"))
            )
            actual = driver.find_element_by_id("swal2-title").text

        except:
            driver.quit()

        expected = '購票成功'
        self.assertEqual(actual, expected)
        time.sleep(3)

    def tearDown(self):
        driver = self.driver
        driver.quit()


if __name__ == '__main__':
    suite = unittest.TestSuite()
    suite.addTest(ReturnTrueTest("test_Order")) 
    unittest.TextTestRunner(verbosity=2).run(suite)




