using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace ReturnTrue.Models
{
    public class BookAsyncRequest : RequestBase
    {
        [JsonProperty("async_key")]
        public string AsyncKey { get; set; }
    }
}