using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace ReturnTrue.Models
{
    public class SearchRequest : RequestBase
    {
        [JsonProperty("from")]
        public string StartStationCode { get; set; }

        [JsonProperty("to")]
        public string DestinationStationCode { get; set; }

        [JsonProperty("date")]
        public string StartDateString
        {
            get
            {
                return this.StartTime.ToString("yyyy-MM-dd ");
            }
        }

        [JsonProperty("time")]
        public string StartTimeString
        {
            get
            {
                return this.StartTime.ToString("HH:mm");
            }
        }

        [JsonProperty("adult")]
        public int NumberOfAdult { get; set; }

        [JsonProperty("child")]
        public int NumberOfChildren { get; set; }


        public DateTime StartTime { get; set; }
    }
}