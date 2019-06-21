package `in`.transacts.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = arrayOf(Transaction::class), version = 1)
abstract class TransactsDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
}