using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace ReturnTrue.Models
{
    public class BookRequest
    {
        public string Start { get; set; }

        public string End { get; set; }

        public string Email { get; set; }

        public string Tag { get; set; }
    }
}