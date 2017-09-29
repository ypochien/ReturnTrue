import hashlib
from wsgiref.handlers import format_date_time
import time
from collections import OrderedDict
import requests


class api_do():
    def __init__(self,api_key,secret):
        self.api_key = api_key
        self.secret1 = secret
        self.secret = self.secret1
        self.header = None
        self.async_key = None
        self.body = None


    def sign(self,para=None):
        md5 = hashlib.md5()
        sec_timestamp = int(time.time())
        httpdate = format_date_time(sec_timestamp)
        hashdata = {"api_key": self.api_key, "t": sec_timestamp}
        if para:
            hashdata.update(para)
        hashdata = OrderedDict(sorted(hashdata.items(), key=lambda t: t[0]))
        hash_str = ''.join('{}={}'.format(k, v) for k, v in hashdata.items())
        md5.update((hash_str + self.secret).encode())
        self.header = {'From': self.api_key, 'Date': httpdate, 'Authorization': md5.hexdigest(), 'Api-Locale': 'zh-CN'}

    def get_async(self,para=None):
        self.sign(para=para)
        r = requests.get("https://alpha.api.detie.cn/api/v2/online_solutions", params=para, headers=self.header)
        self.async_key = r.json()

    def get_result(self):
        para = {'async_key': self.async_key['async']}
        self.sign(para=para)
        rs = requests.get("https://alpha.api.detie.cn/api/v2/async_results/{}".format(self.async_key['async']), headers=self.header)
        self.body = rs.json()
        return rs.json()

