package `in`.transacts.ui

import java.io.Serializable

data class TransactionSer ( val ts: Long,
                            val accountType: String,
                            val transactionType: String,
                            val amount: Double,
                            val number: String,
                            val merchant: String,
                            val bank: String): Serializable