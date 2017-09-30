using RestSharp;
using ReturnTrue.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Web.Http;

namespace ReturnTrue.Controllers
{
    public class BookController : ApiController
    {
        // POST api/values
        public BookResponse Post(BookRequest request)
        {
            return new BookResponse
            {
                Start = "羅馬",
                End = "米蘭",
                TicketPrice = 100,
                UserInfo = new UserInfo
                {
                    Email = "test@test.com"
                }
            };
        }

        public void Get()
        {
            var secret = "85237047-65cc-46bf-95f3-4a382faf75d1";
            var apiKey = "af09a360b7e4485b98f8bb768a38c9d8";
            var host = "https://alpha.api.detie.cn/";


            var searchReqeust = new SearchRequest
            {
                StartStationCode = "ST_EZVVG1X5",
                DestinationStationCode = "ST_D8NNN9ZK",
                StartTime = DateTime.Now.AddDays(20),
                NumberOfAdult = 1,
                NumberOfChildren = 0
            };

            var dateTime = DateTime.Now.ToUniversalTime();
            var secure = new ParamSecure(secret, apiKey, dateTime, searchReqeust);
            var signature = secure.Sign();

            var client = new RestClient(host);

            var request = new RestRequest($"/api/v2/online_solutions?{searchReqeust.GetURL()}", Method.GET);
            request.AddHeader("From", apiKey);
            request.AddHeader("Date", dateTime.ToString("r"));
            request.AddHeader("Authorization", signature);

            var response = client.Get(request);
        }
    }
}
