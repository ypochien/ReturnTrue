using System;
using System.Collections.Generic;
using System.Linq;
using System.Security.Cryptography;
using System.Text;
using System.Web;

namespace ReturnTrue.Models
{
    public class ParamSecure
    {
        public ParamSecure(string secret, string apiKey, DateTime datetime, RequestBase request)
        {
            this.Secret = secret;
            this.ApiKey = apiKey;
            this.CurrentTime = datetime;
            this.Request = request;
        }

        public string Secret { get; set; }
        public DateTime CurrentTime { get; set; }
        public RequestBase Request { get; set; }
        public string ApiKey { get; private set; }

        public string Sign()
        {
            var sources = Request.GetSignatureSources();
            sources["api_key"] = this.ApiKey;
            sources["t"] = new DateTimeOffset(CurrentTime).ToUnixTimeSeconds().ToString();
            var sortedSources = new SortedDictionary<string, string>(sources);

            var input = string.Join("", sortedSources.Select(x => string.Format("{0}={1}", x.Key, x.Value)).ToList());

            using (MD5 md5Hash = MD5.Create())
            {
                var data = md5Hash.ComputeHash(Encoding.UTF8.GetBytes(input + this.Secret));

                var sb = new StringBuilder();

                for (int i = 0; i < data.Length; i++)
                {
                    sb.Append(data[i].ToString("x2"));
                }

                return sb.ToString();
            }
        }
    }
}