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

            var searchResponse = service.Search(requestData.Start, requestData.End);
            var searchResults = service.GetSearchResult(searchResponse.async);

            var searchResult = searchResults.Where(i => i.Railway.Code == "FB").FirstOrDefault();

            var bookResponse = service.Book(searchResult.Solutions[0].Sections[0].Offers[0].Services[0].BookingCode, requestData.Email);
            var bookResult = service.GetBookResult(bookResponse.async);

            var confirmResponse = service.Confirm(bookResult.Id);
            var confirmResult = service.GetConfirmResult(confirmResponse.async);

            return new BookResponse
            {
                Id = confirmResult.Id,
                Start = requestData.Start,
                End = requestData.End,
                TicketPrice = confirmResult.TicketPrice.Cents,
                UserInfo = new UserInfo
                {
                    Email = requestData.Email
                }
            };
        }
    }
}
