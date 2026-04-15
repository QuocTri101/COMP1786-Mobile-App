package com.example.logbook_excersise3;
import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Expense.class}, version = 1, exportSchema = false)
public abstract class ExpenseDB extends RoomDatabase{
    private static ExpenseDB instance;

    public abstract ExpenseDAO expenseDao();

    public static synchronized ExpenseDB getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            ExpenseDB.class, "expense_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
