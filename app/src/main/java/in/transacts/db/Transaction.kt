package `in`.transacts.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Transaction (

    @PrimaryKey val ts: Long,
    @ColumnInfo(name = "acc_type") val accountType: String,
    @ColumnInfo(name = "trans_type") val transactionType: String,
    @ColumnInfo(name = "amount") val amount: Double,
    @ColumnInfo(name = "number") val number: String,
    @ColumnInfo(name = "merchant") val merchant: String,
    @ColumnInfo(name = "bank") val bank: String
)
//{
//    @PrimaryKey(autoGenerate = true)
//    var id: Int = 0
//}