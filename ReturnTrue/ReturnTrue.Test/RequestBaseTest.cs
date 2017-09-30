using Newtonsoft.Json;
using ReturnTrue.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Xunit;

namespace ReturnTrue.Test
{
    public class RequestBaseTest
    {
        internal class TestRequest : RequestBase
        {
            [JsonProperty("test_field")]
            public string TestField { get; set; }
        }

        [Fact]
        public void TestRequestBaseGetSignatureSources()
        {
            // Arrange
            var expected = new Dictionary<string, string>() {
                { "test_field", "test" }
            };
            Dictionary<string, string> actual;
            var testRequest = new TestRequest()
            {
                TestField = "test"
            };

            // Act
            actual = testRequest.GetSignatureSources();

            // Arrange
            Assert.Equal(expected, actual);
        }
    }
}
