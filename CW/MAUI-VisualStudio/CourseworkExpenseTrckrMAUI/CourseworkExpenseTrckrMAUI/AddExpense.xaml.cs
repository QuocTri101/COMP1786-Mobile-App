using Firebase.Database;
using Firebase.Database.Query;

namespace CourseworkExpenseTrckrMAUI;

public partial class AddExpense : ContentPage
{
    private readonly string FirebaseUrl = "https://so-awesome-project-1e71b-default-rtdb.asia-southeast1.firebasedatabase.app/";
    private FirebaseClient firebaseClient;
    private string _projectId;
    public AddExpense(string projectId)
	{
		InitializeComponent();
        _projectId = projectId;
        firebaseClient = new FirebaseClient(FirebaseUrl);
    }
    private async void OnSaveExpenseClicked(object sender, EventArgs e)
    {
        if (string.IsNullOrWhiteSpace(entryAmount.Text) || pickerCurrency.SelectedItem == null || string.IsNullOrWhiteSpace(entryClaimant.Text))
        {
            await DisplayAlert("Error", "Please fill in all required fields.", "OK");
            return;
        }

        if (!double.TryParse(entryAmount.Text, out double parsedAmount))
        {
            await DisplayAlert("Error", "Invalid amount entered.", "OK");
            return;
        }

        string newExpenseId = Guid.NewGuid().ToString("N");

        var newExpense = new Expense
        {
            ExpenseId = newExpenseId,
            ProjectId = _projectId,
            Amount = parsedAmount,
            Currency = pickerCurrency.SelectedItem.ToString(),
            Claimant = entryClaimant.Text.Trim(),
            Date = datePickerExpense.Date?.ToString("dd/MM/yyyy") ?? string.Empty,
            Description = editorDesc.Text?.Trim() ?? "",
            Type = "Other",
            PaymentMethod = "Cash",
            PaymentStatus = "Pending",
            Location = ""
        };

        try
        {
            await firebaseClient
                .Child("projects")
                .Child(_projectId)
                .Child("expenses")
                .Child(newExpenseId)
                .PutAsync(newExpense);

            await DisplayAlert("Success", "Expense added successfully!", "OK");

            await Navigation.PopAsync();
        }
        catch (Exception)
        {
            await DisplayAlert("Error", "Failed to connect to database.", "OK");
        }
    }
}