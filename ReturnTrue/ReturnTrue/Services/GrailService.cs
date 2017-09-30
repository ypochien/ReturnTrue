using Newtonsoft.Json;
using RestSharp;
using ReturnTrue.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Threading;
using System.Web;

namespace ReturnTrue.Services
{
    public class GrailService
    {
        public string ApiKey { get; private set; }

        public string Secret { get; private set; }

        public string Host { get; private set; }

        public GrailService(string apiKey, string secret, string host)
        {
            this.ApiKey = apiKey;
            this.Secret = secret;
            this.Host = host;

            ServicePointManager.Expect100Continue = true;
            ServicePointManager.SecurityProtocol = SecurityProtocolType.Tls12;
        }

        public BookGrailTokenResponse Search()
        {

            var searchReqeust = new SearchRequest
            {
                StartStationCode = "ST_E0203JK4",
                DestinationStationCode = "ST_DQMOQ7GW",
                StartTime = DateTime.Today.AddHours(5).AddDays(20),
                NumberOfAdult = 1,
                NumberOfChildren = 0
            };

            var dateTime = DateTime.Now.ToUniversalTime();
            var secure = new ParamSecure(this.Secret, this.ApiKey, dateTime, searchReqeust);
            var signature = secure.Sign();

            var client = new RestClient(this.Host);

            var request = new RestRequest($"/api/v2/online_solutions?{searchReqeust.GetURL()}", Method.GET);
            request.AddHeader("From", this.ApiKey);
            request.AddHeader("Date", dateTime.ToString("r"));
            request.AddHeader("Authorization", signature);

            var response = client.Get(request);
            var responseObject = JsonConvert.DeserializeObject<BookGrailTokenResponse>(response.Content);

            return responseObject;
        }

        public BookGrailSearchResponse[] GetSearchResult(string token)
        {
            var asyncRequest = new BookAsyncRequest
            {
                AsyncKey = token
            };

            var dateTime = DateTime.Now.ToUniversalTime();
            var secure = new ParamSecure(this.Secret, this.ApiKey, dateTime, asyncRequest);
            var signature = secure.Sign();

            var client = new RestClient(this.Host);

            var request = new RestRequest($"/api/v2/async_results/{asyncRequest.AsyncKey}", Method.GET);
            request.AddHeader("From", this.ApiKey);
            request.AddHeader("Date", dateTime.ToString("r"));
            request.AddHeader("Authorization", signature);

            var notReady = true;
            do
            {
                var response = client.Get(request);
                notReady = response.StatusCode != HttpStatusCode.OK;
                if (!notReady)
                {
                    var responseData = JsonConvert.DeserializeObject<BookGrailSearchResponse[]>(response.Content);
                    return responseData;
                }
                Thread.Sleep(1000);

            } while (notReady);

            return null;
        }

        public BookGrailTokenResponse Book(string bookingCode, string email)
        {
            var passengers = new List<BookGrailPassenger>();
            passengers.Add(new BookGrailPassenger
            {
                first_name = "test",
                last_name = "test",
                email = email,
                birthdate = "1985-01-01",
                gender = "male",
                passport = "A123456",
                phone = "10086"
            });
            var requestBody = new BookGrailRequest
            {
                passengers = passengers,
                contact = new BookGrailContact
                {
                    name = "Liping",
                    email = email,
                    phone = "10086",
                    address = "beijing",
                    postcode = "100100"
                },
                sections = new List<string>
                {
                    bookingCode
                },
                seat_reserved = true
            };

            var dateTime = DateTime.Now.ToUniversalTime();
            var secure = new ParamSecure(this.Secret, this.ApiKey, dateTime, requestBody);
            var signature = secure.Sign();

            var client = new RestClient(this.Host);

            var request = new RestRequest($"/api/v2/online_orders", Method.POST);
            request.AddHeader("From", this.ApiKey);
            request.AddHeader("Date", dateTime.ToString("r"));
            request.AddHeader("Authorization", signature);
            request.AddHeader("Content-type", "application/json");
            request.AddJsonBody(requestBody);

            var response = client.Post(request);
            var responseObject = JsonConvert.DeserializeObject<BookGrailTokenResponse>(response.Content);

            return responseObject;
        }

        public BookGrailBookResponse GetBookResult(string token)
        {
            var asyncRequest = new BookAsyncRequest
            {
                AsyncKey = token
            };

            var dateTime = DateTime.Now.ToUniversalTime();
            var secure = new ParamSecure(this.Secret, this.ApiKey, dateTime, asyncRequest);
            var signature = secure.Sign();

            var client = new RestClient(this.Host);

            var request = new RestRequest($"/api/v2/async_results/{asyncRequest.AsyncKey}", Method.GET);
            request.AddHeader("From", this.ApiKey);
            request.AddHeader("Date", dateTime.ToString("r"));
            request.AddHeader("Authorization", signature);

            var notReady = true;
            do
            {
                var response = client.Get(request);
                notReady = response.StatusCode != HttpStatusCode.OK;
                if (!notReady)
                {
                    var responseData = JsonConvert.DeserializeObject<BookGrailBookResponse>(response.Content);
                    return responseData;
                }
                Thread.Sleep(1000);

            } while (notReady);

            return null;
        }

        public BookGrailTokenResponse Confirm(string orderId)
        {
            var requestData = new BookGrailConfirmRequest();
            requestData.OrderId = orderId;

            var dateTime = DateTime.Now.ToUniversalTime();
            var secure = new ParamSecure(this.Secret, this.ApiKey, dateTime, requestData);
            var signature = secure.Sign();

            var client = new RestClient(this.Host);

            var request = new RestRequest($"/api/v2/online_orders/{orderId}/online_confirmations", Method.POST);
            request.AddHeader("From", this.ApiKey);
            request.AddHeader("Date", dateTime.ToString("r"));
            request.AddHeader("Authorization", signature);
            //request.AddHeader("Content-type", "application/json");
            //request.AddJsonBody(requestData);

            var response = client.Post(request);
            var responseObject = JsonConvert.DeserializeObject<BookGrailTokenResponse>(response.Content);

            return responseObject;
        }

        public BookGrailBookResponse GetConfirmResult(string token)
        {
            var asyncRequest = new BookAsyncRequest
            {
                AsyncKey = token
            };

            var dateTime = DateTime.Now.ToUniversalTime();
            var secure = new ParamSecure(this.Secret, this.ApiKey, dateTime, asyncRequest);
            var signature = secure.Sign();

            var client = new RestClient(this.Host);

            var request = new RestRequest($"/api/v2/async_results/{asyncRequest.AsyncKey}", Method.GET);
            request.AddHeader("From", this.ApiKey);
            request.AddHeader("Date", dateTime.ToString("r"));
            request.AddHeader("Authorization", signature);

            var notReady = true;
            do
            {
                var response = client.Get(request);
                notReady = response.StatusCode != HttpStatusCode.OK;
                if (!notReady)
                {
                    var responseData = JsonConvert.DeserializeObject<BookGrailBookResponse>(response.Content);
                    return responseData;
                }
                Thread.Sleep(1000);

            } while (notReady);

            return null;
        }

    }
}