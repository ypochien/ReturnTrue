using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace ReturnTrue.Models
{
    public class BookGrailConfirmRequest : RequestBase
    {
        [JsonProperty("online_order_id")]
        public string OrderId { get; set; }

        public BookGrailCreditCard CreditCart { get; set; }
    }
}