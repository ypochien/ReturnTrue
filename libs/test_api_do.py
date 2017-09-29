import unittest

import time

from libs.api_test import api_do

class TestApiDo(unittest.TestCase):
    def setUp(self):
        self.sut = api_do(api_key='af09a360b7e4485b98f8bb768a38c9d8',secret='85237047-65cc-46bf-95f3-4a382faf75d1')

    def test_Api_do(self):
        self.sut.sign()
        self.assertDictContainsSubset({'From':'af09a360b7e4485b98f8bb768a38c9d8'},self.sut.header)


    def test_Api_do_soltions_should_be_async_key(self):
        para = {'from': 'ST_EZVVG1X5', 'to': 'ST_D8NNN9ZK', 'date': '2017-10-20', 'time': '12:00', 'adult': 1,
                'child': 0}
        self.sut.get_async(para=para)
        self.assertIn('async',self.sut.async_key)
        for i in range(10):
            self.sut.get_result()
            if isinstance(self.sut.body, dict) and self.sut.body.get('description'):
                time.sleep(3)
                print('wait.', i)
            else:
                print(self.sut.body)
                break

        self.assertIsInstance(self.sut.body,list)

if __name__ == '__main__':
    unittest.main()
