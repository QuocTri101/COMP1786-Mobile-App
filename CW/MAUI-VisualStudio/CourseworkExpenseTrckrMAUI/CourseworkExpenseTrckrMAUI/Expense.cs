using System;
using System.Collections.Generic;
using System.Text;

namespace CourseworkExpenseTrckrMAUI
{
    public class Expense
    {
        public string ExpenseId { get; set; }
        public string ProjectId { get; set; }
        public string Date { get; set; }
        public double Amount { get; set; }
        public string Currency { get; set; }
        public string Type { get; set; }
        public string PaymentMethod { get; set; }
        public string Claimant { get; set; }
        public string Description { get; set; }
        public string PaymentStatus { get; set; }
        public string Location { get; set; }
    }
}
