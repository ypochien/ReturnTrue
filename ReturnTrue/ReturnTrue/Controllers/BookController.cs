using RestSharp;
using ReturnTrue.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Web.Http;
using System.Web.Http.Cors;

namespace ReturnTrue.Controllers
{
    [EnableCors(origins: "*", headers: "*", methods: "*")]
    public class BookController : ApiController
    {
        // POST api/values
        public BookResponse Post(BookRequest requestData)
        {
            var secret = "85237047-65cc-46bf-95f3-4a382faf75d1";
            var apiKey = "af09a360b7e4485b98f8bb768a38c9d8";
            var host = "https://alpha.api.detie.cn/";

            var requestBody = new BookGrailRequest
            {
                passengers = new BookGrailPassenger
                {
                    first_name = "test",
                    last_name = "test",
                    email = requestData.Email,
                    birthdate = "1985-01-01",
                    gender = "male",
                    passport = "A123456",
                    phone = "15000367081"
                },
                contact = new BookGrailContact
                {
                    name = "Liping",
                    email = requestData.Email,
                    phone = "10086",
                    address = "beijing",
                    postcode = "100100"
                },
                sections = new List<string>
                {
                    "bc_01"
                },
                seat_reserved = true
            };

            var dateTime = DateTime.Now.ToUniversalTime();
            var secure = new ParamSecure(secret, apiKey, dateTime, requestBody);
            var signature = secure.Sign();

            var client = new RestClient(host);

            var request = new RestRequest($"/api/v2/online_orders", Method.POST);
            request.AddHeader("From", apiKey);
            request.AddHeader("Date", dateTime.ToString("r"));
            request.AddHeader("Authorization", signature);
            request.AddHeader("Content-type", "application/json");
            request.AddJsonBody(requestBody);

            var response = client.Post(request);

            return new BookResponse
            {
                Start = "羅馬",
                End = "米蘭",
                TicketPrice = 100,
                UserInfo = new UserInfo
                {
                    Email = requestData.Email
                }
            };
        }
    }
}
