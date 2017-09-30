using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace ReturnTrue.Models
{
    public partial class BookGrailSearchResponse
    {
        [JsonProperty("description")]
        public string Description { get; set; }

        [JsonProperty("railway")]
        public Railway Railway { get; set; }

        [JsonProperty("solutions")]
        public Solution[] Solutions { get; set; }
    }

    public partial class Railway
    {
        [JsonProperty("code")]
        public string Code { get; set; }
    }

    public partial class Solution
    {
        [JsonProperty("duration")]
        public Duration Duration { get; set; }

        [JsonProperty("sections")]
        public Section[] Sections { get; set; }

        [JsonProperty("departure")]
        public string Departure { get; set; }

        [JsonProperty("from")]
        public From From { get; set; }

        [JsonProperty("to")]
        public From To { get; set; }

        [JsonProperty("transfer_times")]
        public long TransferTimes { get; set; }
    }

    public partial class Duration
    {
        [JsonProperty("hour")]
        public long Hour { get; set; }

        [JsonProperty("minutes")]
        public long Minutes { get; set; }
    }

    public partial class Section
    {
        [JsonProperty("offers")]
        public Offer[] Offers { get; set; }

        [JsonProperty("trains")]
        public Train[] Trains { get; set; }
    }

    public partial class Offer
    {
        [JsonProperty("description")]
        public string Description { get; set; }

        [JsonProperty("code")]
        public string Code { get; set; }

        [JsonProperty("detail")]
        public string Detail { get; set; }

        [JsonProperty("services")]
        public Service[] Services { get; set; }
    }

    public partial class Service
    {
        [JsonProperty("booking_code")]
        public string BookingCode { get; set; }

        [JsonProperty("description")]
        public string Description { get; set; }

        [JsonProperty("available")]
        public Available Available { get; set; }

        [JsonProperty("code")]
        public string Code { get; set; }

        [JsonProperty("detail")]
        public string Detail { get; set; }

        [JsonProperty("price")]
        public Price Price { get; set; }
    }

    public partial class Available
    {
        [JsonProperty("seats")]
        public long Seats { get; set; }
    }

    public partial class Price
    {
        [JsonProperty("cents")]
        public long Cents { get; set; }

        [JsonProperty("currency")]
        public string Currency { get; set; }
    }

    public partial class Train
    {
        [JsonProperty("departure")]
        public string Departure { get; set; }

        [JsonProperty("number")]
        public string Number { get; set; }

        [JsonProperty("arrival")]
        public string Arrival { get; set; }

        [JsonProperty("from")]
        public From From { get; set; }

        [JsonProperty("to")]
        public From To { get; set; }

        [JsonProperty("type")]
        public string Type { get; set; }
    }

    public partial class From
    {
        [JsonProperty("code")]
        public string Code { get; set; }

        [JsonProperty("name")]
        public string Name { get; set; }
    }
}