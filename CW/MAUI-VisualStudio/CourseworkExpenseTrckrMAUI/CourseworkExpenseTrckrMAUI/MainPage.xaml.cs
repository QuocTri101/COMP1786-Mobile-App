using Firebase.Database;
using Firebase.Database.Query;
using System.Collections.ObjectModel;

namespace CourseworkExpenseTrckrMAUI
{
    public partial class MainPage : ContentPage
    {
        private readonly string FirebaseUrl = "https://so-awesome-project-1e71b-default-rtdb.asia-southeast1.firebasedatabase.app/";
        private FirebaseClient firebaseClient;
        private List<Project> _allProjects = new List<Project>();
        public ObservableCollection<Project> Projects { get; set; } = new ObservableCollection<Project>();
        public MainPage()
        {
            InitializeComponent();
            firebaseClient = new FirebaseClient(FirebaseUrl);
            projectCollectionView.ItemsSource = Projects;
            LoadProjects();
        }
        private async void LoadProjects()
        {
            var observable = firebaseClient.Child("projects").AsObservable<Project>();

            observable.Subscribe(d =>
            {
                if (d.EventType == Firebase.Database.Streaming.FirebaseEventType.InsertOrUpdate)
                {
                    MainThread.BeginInvokeOnMainThread(() =>
                    {
                        var existingProject = _allProjects.FirstOrDefault(p => p.ProjectId == d.Object.ProjectId);
                        if (existingProject != null)
                        {
                            _allProjects.Remove(existingProject);
                        }
                        _allProjects.Add(d.Object);
                        FilterProjects(searchBar.Text);
                    });
                }
            });
        }

        private void OnSearchTextChanged(object sender, TextChangedEventArgs e)
        {
            FilterProjects(e.NewTextValue);
        }
        private readonly string[] FavKeywords = { "fav", "favorite", "favourite", "star", "starred", "heart", "love"};
        private void FilterProjects(string keyword)
        {
            if (string.IsNullOrWhiteSpace(keyword))
            {
                Projects.Clear();
                foreach (var p in _allProjects) Projects.Add(p);
                return;
            }

            keyword = keyword.ToLower().Trim();
            List<Project> filtered;
            if (FavKeywords.Contains(keyword))
            {
                filtered = _allProjects.Where(p => p.Favorite).ToList();
            }
            else
            {
                filtered = _allProjects.Where(p =>
                    (p.Name != null && p.Name.ToLower().Contains(keyword)) ||
                    (p.StartDate != null && p.StartDate.ToLower().Contains(keyword))
                ).ToList();
            }

            Projects.Clear();
            foreach (var p in filtered) Projects.Add(p);

            if (filtered.Count == 0)
            {
                MainThread.BeginInvokeOnMainThread(async () =>
                {
                });
            }
        }
        private async void OnFavoriteClicked(object sender, EventArgs e)
        {
            var button = sender as ImageButton;
            var project = button?.CommandParameter as Project;

            if (project != null)
            {
                project.Favorite = !project.Favorite;

                try
                {
                    await firebaseClient
                        .Child("projects")
                        .Child(project.ProjectId)
                        .Child("favorite")
                        .PutAsync(project.Favorite);
                }
                catch (Exception ex)
                {
                    await DisplayAlert("Error", "Could not connect to database.", "OK");

                    project.Favorite = !project.Favorite;
                }
            }
        }
        private async void OnProjectTapped(object sender, EventArgs e)
        {
            var frame = sender as Frame;
            var selectedProject = frame?.BindingContext as Project;

            if (selectedProject != null)
            {
                await Navigation.PushAsync(new ProjExpenses(selectedProject.ProjectId));
            }
        }
    }
}
