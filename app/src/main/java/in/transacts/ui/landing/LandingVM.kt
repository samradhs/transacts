package `in`.transacts.ui.landing

import `in`.transacts.db.Database
import `in`.transacts.ui.TransactionSer
import android.content.Context
import android.util.Log
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class LandingVM {

    companion object {
        private const val TAG = "LandingVM"
    }

    fun getAllData(context: Context, getDbData: GetDbData) {

        doAsync {

            Log.e(TAG, "getAll from db")
            val transactionDao = Database.getDb(context).transactionDao()
            val list = transactionDao.getAll()

            val response: ArrayList<TransactionSer> = arrayListOf()
            for (transaction in list) {
                response.add(TransactionSer(transaction.ts,
                                            transaction.accountType,
                                            transaction.transactionType,
                                            transaction.amount,
                                            transaction.number,
                                            transaction.merchant,
                                            transaction.bank))
            }

            uiThread {
                getDbData.onResponse(response)
            }
        }
    }
}

interface GetDbData {

    fun onResponse(response: List<TransactionSer>)
}