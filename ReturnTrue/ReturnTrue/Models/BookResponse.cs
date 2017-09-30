﻿namespace ReturnTrue.Models
{
    public class BookResponse
    {
        public string Id { get; set; }

        public string Start { get; set; }

        public string End { get; set; }

        public decimal TicketPrice { get; set; }

        public UserInfo UserInfo { get; set; }
    }
}