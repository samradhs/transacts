package `in`.transacts.utils

import `in`.transacts.R
import `in`.transacts.ui.TransactionSer
import android.content.Context

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

    fun getAmountForUI(context: Context, transaction: TransactionSer): String {

        return getAmountForUI(context, transaction.amount, isDebit(transaction))
    }

    fun getAmountForUI(context: Context, amount: Double, isDebit: Boolean): String {

        return if (isDebit) {
            context.getString(R.string.debited, getFormattedAmount(amount))
        } else {
            context.getString(R.string.credited, getFormattedAmount(amount))
        }
    }
}