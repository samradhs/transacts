package `in`.transacts.utils

import `in`.transacts.ui.TransactionSer

object TransactionUtils {

    fun isDebit(transaction: TransactionSer): Boolean {

        return transaction.transactionType.equals("dr", true)
    }

    fun getFormattedNumber(transaction: TransactionSer): String {

        return transaction.number.takeLast(4)
    }

    fun getFormattedAmount(transaction: TransactionSer): String {

        return String.format("%.2f", transaction.amount)
    }

    fun getFormattedAmount(amount: Double): String {

        return String.format("%.2f", amount)
    }
}