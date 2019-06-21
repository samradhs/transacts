package `in`.transacts.db

import android.content.Context
import androidx.room.Room

object Database {

    private const val DB_NAME = "transacts"

    fun getDb(context: Context): TransactsDatabase {

        return Room.databaseBuilder(
            context,
            TransactsDatabase::class.java,
            DB_NAME
        ).build()
    }
}