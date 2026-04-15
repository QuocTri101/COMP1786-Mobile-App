using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Text;

namespace CourseworkExpenseTrckrMAUI
{
    public class Project : INotifyPropertyChanged
    {
        public event PropertyChangedEventHandler PropertyChanged;
        public string ProjectId { get; set; }
        public string Name { get; set; }
        public string Description { get; set; }
        public string StartDate { get; set; }
        public string EndDate { get; set; }
        public string Manager { get; set; }
        public string Status { get; set; }
        public double Budget { get; set; }
        public string SpecialReq { get; set; }
        public string ClientInfo { get; set; }
        private bool _favorite;
        public bool Favorite
        {
            get => _favorite;
            set
            {
                if (_favorite != value)
                {
                    _favorite = value;
                    PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(nameof(Favorite)));
                }
            }
        }
    }
}
