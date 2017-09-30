using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace ReturnTrue.Models
{
    public class BookGrailRequest : RequestBase
    {
        public BookGrailContact contact { get; set; }

        public List<BookGrailPassenger> passengers { get; set; }

        public List<string> sections { get; set; }

        [JsonProperty("seat_reserved")]
        public bool seat_reserved { get; set; }
    }
}