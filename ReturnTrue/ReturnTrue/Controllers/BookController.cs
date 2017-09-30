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
        private string secret = "4d69c795-4455-4e8a-bb40-7ef4db2ad0d2";
        private string apiKey = "6bbbd8759d744058b9b58f22d5ac96b0";
        private string host = "https://alpha.api.detie.cn/";

        // POST api/values
        public BookResponse Post(BookRequest requestData)
        {
            var service = new GrailService(apiKey, secret, host);

            var searchResponse = service.Search();
            var searchResult = service.GetSearchResult(searchResponse.async);

            var bookResponse = service.Book(searchResult[0].Solutions[0].Sections[0].Offers[0].Services[0].BookingCode, requestData.Email);
            var bookResult = service.GetBookResult(bookResponse.async);

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
