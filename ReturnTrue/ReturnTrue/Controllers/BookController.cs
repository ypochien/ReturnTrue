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
    }
}
