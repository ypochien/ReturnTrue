using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace ReturnTrue.Services
{
    public partial class BookGrailBookResponse
    {
        [JsonProperty("payment_price")]
        public ChargingPrice PaymentPrice { get; set; }

        [JsonProperty("from")]
        public From From { get; set; }

        [JsonProperty("created_at")]
        public string CreatedAt { get; set; }

        [JsonProperty("charging_price")]
        public ChargingPrice ChargingPrice { get; set; }

        [JsonProperty("departure")]
        public string Departure { get; set; }

        [JsonProperty("memo")]
        public object Memo { get; set; }

        [JsonProperty("id")]
        public string Id { get; set; }

        [JsonProperty("passengers")]
        public Passenger[] Passengers { get; set; }

        [JsonProperty("rtp_price")]
        public ChargingPrice RtpPrice { get; set; }

        [JsonProperty("rebate_amount")]
        public ChargingPrice RebateAmount { get; set; }

        [JsonProperty("railway")]
        public Railway Railway { get; set; }

        [JsonProperty("records")]
        public object[] Records { get; set; }

        [JsonProperty("tickets")]
        public Ticket[] Tickets { get; set; }

        [JsonProperty("ticket_price")]
        public ChargingPrice TicketPrice { get; set; }

        [JsonProperty("to")]
        public From To { get; set; }
    }

    public partial class ChargingPrice
    {
        [JsonProperty("cents")]
        public long Cents { get; set; }

        [JsonProperty("currency")]
        public string Currency { get; set; }
    }

    public partial class From
    {
        [JsonProperty("code")]
        public string Code { get; set; }

        [JsonProperty("name")]
        public string Name { get; set; }
    }

    public partial class Passenger
    {
        [JsonProperty("gender")]
        public string Gender { get; set; }

        [JsonProperty("email")]
        public string Email { get; set; }

        [JsonProperty("birthdate")]
        public string Birthdate { get; set; }

        [JsonProperty("first_name")]
        public string FirstName { get; set; }

        [JsonProperty("last_name")]
        public string LastName { get; set; }

        [JsonProperty("id")]
        public string Id { get; set; }

        [JsonProperty("phone")]
        public string Phone { get; set; }
    }

    public partial class Railway
    {
        [JsonProperty("code")]
        public string Code { get; set; }
    }

    public partial class Ticket
    {
        [JsonProperty("id")]
        public string Id { get; set; }

        [JsonProperty("from")]
        public From From { get; set; }

        [JsonProperty("price")]
        public ChargingPrice Price { get; set; }

        [JsonProperty("to")]
        public From To { get; set; }
    }
}