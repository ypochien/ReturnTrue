using Newtonsoft.Json;

namespace ReturnTrue.Models
{
    public class BookGrailCreditCard
    {
        [JsonProperty("number")]
        public string Number { get; set; }

        [JsonProperty("exp_month")]
        public string Month { get; set; }

        [JsonProperty("exp_year")]
        public string Year { get; set; }

        [JsonProperty("cvv")]
        public string CVV { get; set; }
    }
}