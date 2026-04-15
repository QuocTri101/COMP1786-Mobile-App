using System;
using System.Collections.Generic;
using System.Globalization;
using System.Text;

namespace CourseworkExpenseTrckrMAUI
{
    public class FavIcon: IValueConverter
    {
        public object Convert(object value, Type targetType, object parameter, CultureInfo culture)
        {
            if (value is bool isFavorite)
            {
                return isFavorite ? "heart_on.png" : "heart_off.png";
            }
            return "heart_off.png";
        }

        public object ConvertBack(object value, Type targetType, object parameter, CultureInfo culture)
        {
            throw new NotImplementedException();
        }
    }
}
