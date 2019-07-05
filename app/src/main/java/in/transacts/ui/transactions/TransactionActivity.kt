package `in`.transacts.ui.transactions

import `in`.transacts.R
import `in`.transacts.ui.base.BaseActivity
import `in`.transacts.ui.TransactionSer
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_transaction.*

class TransactionActivity: BaseActivity() {

    companion object {

        internal const val BANK         = "bank"
        internal const val NUM          = "num"
        internal const val TRANSACTIONS = "transactions"

        private const val TAG           = "TransactionActivity"
    }

    override fun getLayoutId(): Int {

        return R.layout.activity_transaction
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.e(TAG, "onCreate")

        val bundle = intent.extras!!
        val bank = bundle.getString(BANK)
        val accNum = bundle.getString(NUM)
        val transactions = (bundle.getSerializable(TRANSACTIONS) as Array<TransactionSer>).toList()
            .sortedByDescending { it.ts }

        title = "${bank!!.toUpperCase()} ($accNum)"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setAdapter(transactions)
        Log.e(TAG, "accNum: $accNum, bank: $bank")
    }

    private fun setAdapter(response: List<TransactionSer>) {

        val recyclerView    = transactions_view
        recyclerView.layoutManager       = LinearLayoutManager(this)
        recyclerView.adapter             = TransactionAdapter(response, this)
//        recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL))
    }
}