using Firebase.Database;
using Firebase.Database.Query;
using System.Collections.ObjectModel;

namespace CourseworkExpenseTrckrMAUI;

public partial class ProjExpenses : ContentPage
{
    private readonly string FirebaseUrl = "https://so-awesome-project-1e71b-default-rtdb.asia-southeast1.firebasedatabase.app/";
    private FirebaseClient firebaseClient;
    private string _projectId;
    public ObservableCollection<Expense> Expenses { get; set; } = new ObservableCollection<Expense>();
    public ProjExpenses(string projectId)
	{
        InitializeComponent();
        _projectId = projectId;
        firebaseClient = new FirebaseClient(FirebaseUrl);

        expensesCollectionView.ItemsSource = Expenses;
        LoadExpenses();
    }
    private void LoadExpenses()
    {
        var observable = firebaseClient
            .Child("projects")
            .Child(_projectId)
            .Child("expenses")
            .AsObservable<Expense>();

        observable.Subscribe(d =>
        {
            if (d.EventType == Firebase.Database.Streaming.FirebaseEventType.InsertOrUpdate)
            {
                MainThread.BeginInvokeOnMainThread(() =>
                {
                    var existingExpense = Expenses.FirstOrDefault(e => e.ExpenseId == d.Object.ExpenseId);
                    if (existingExpense != null)
                    {
                        Expenses.Remove(existingExpense);
                    }
                    Expenses.Add(d.Object);
                });
            }
        });
    }

    private async void OnAddNewExpenseClicked(object sender, EventArgs e)
    {
        await Navigation.PushAsync(new AddExpense(_projectId));
    }
}