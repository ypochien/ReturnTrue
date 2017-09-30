using System;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using ReturnTrue.Models;
using Newtonsoft.Json;
using System.Collections.Generic;

namespace ReturnTrue.Test
{
    [TestClass]
    public class RequestBaseTest
    {
        internal class TestRequest : RequestBase
        {
            [JsonProperty("test_field")]
            public string TestField { get; set; }
        }

        [TestMethod]
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
            Assert.AreEqual(expected.Count, actual.Count);
        }

        [TestMethod]
        public void TestRequestBaseGetURL()
        {
            // Arrange
            var expected = "test_field=test";
            var actual = string.Empty;
            var testRequest = new TestRequest()
            {
                TestField = "test"
            };

            // Act
            actual = testRequest.GetURL();

            // Arrange
            Assert.AreEqual(expected, actual);
        }
    }
}
