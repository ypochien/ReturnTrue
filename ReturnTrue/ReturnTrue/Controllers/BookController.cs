using RestSharp;
using ReturnTrue.Models;
using ReturnTrue.Services;
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
            var secret = "4d69c795-4455-4e8a-bb40-7ef4db2ad0d2";
            var apiKey = "6bbbd8759d744058b9b58f22d5ac96b0";
            var host = "https://alpha.api.detie.cn/";

            var passengers = new List<BookGrailPassenger>();
            passengers.Add(new BookGrailPassenger
            {
                first_name = "test",
                last_name = "test",
                email = "test@test.com",
                birthdate = "1985-01-01",
                gender = "male",
                passport = "A123456",
                phone = "15000367081"
            });
            var requestBody = new BookGrailRequest
            {
                passengers = passengers,
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

        public IHttpActionResult Get()
        {
            var secret = "4d69c795-4455-4e8a-bb40-7ef4db2ad0d2";
            var apiKey = "6bbbd8759d744058b9b58f22d5ac96b0";
            var host = "https://alpha.api.detie.cn/";

            var service = new GrailService(apiKey, secret, host);

            var searchResponse = service.Search();
            var searchResult = service.GetSearchResult(searchResponse.async);

            var bookResponse = service.Book(searchResult[0].Solutions[0].Sections[0].Offers[0].Services[0].BookingCode);
            var bookResult = service.GetBookResult(bookResponse.async);

            return Ok(bookResult);
        }
    }
}
