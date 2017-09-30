using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace ReturnTrue.Models
{
    public class SearchRequest : RequestBase
    {
        [JsonProperty("s")]
        public string StartStationCode { get; set; }

        [JsonProperty("d")]
        public string DestinationStationCode { get; set; }

        [JsonProperty("dt")]
        public string StartTimeString
        {
            get
            {
                return this.StartTime.ToString("yyyy-MM-dd HH:mm");
            }
        }

        [JsonProperty("na")]
        public int NumberOfAdult { get; set; }

        [JsonProperty("nc")]
        public int NumberOfChildren { get; set; }


        public DateTime StartTime { get; set; }
    }
}