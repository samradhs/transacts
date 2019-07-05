package `in`.transacts.ui.landing

import `in`.transacts.R
import `in`.transacts.databinding.ItemAccSummaryBinding
import `in`.transacts.ui.TransactionSer
import `in`.transacts.ui.transactions.TransactionActivity
import `in`.transacts.utils.TransactionUtils
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment

class AccountsFragment: Fragment() {

    companion object {
        private const val TAG = "AccountsFragment"
        private const val TRANSACTIONS = "transactions"

        fun newInstance(transactions: List<TransactionSer>): AccountsFragment {

            val accountsFragment = AccountsFragment()
            val bundle = Bundle()
            bundle.putSerializable(TRANSACTIONS, transactions.toTypedArray())
            accountsFragment.arguments = bundle
            return accountsFragment
        }
    }

    private lateinit var transactions: List<TransactionSer>
    private val accMap: MutableMap<String, ArrayList<TransactionSer>> = mutableMapOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.e(TAG, "onCreate")
        if (accMap.isNotEmpty()) return

        transactions = (arguments!!.getSerializable(TRANSACTIONS) as Array<TransactionSer>).toList()
        for (transaction in transactions) {

            val key = transaction.bank + TransactionUtils.getFormattedNumber(transaction)
            var accNumTransactions = accMap[key]
            if (accNumTransactions == null) {
                accNumTransactions = arrayListOf()
                accMap[key] = accNumTransactions
            }

            accNumTransactions.add(transaction)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val rootView: View

        if (accMap.isEmpty()) {
            rootView = inflater.inflate(R.layout.layout_page_placeholder, container, false) as TextView
            rootView.text = getString(R.string.no_acc_found)

        } else {
            rootView = inflater.inflate(R.layout.fragment_accounts, container, false)
            val parent: ViewGroup = rootView.findViewById(R.id.accounts_cont)

            for (keys in accMap.keys) {
                Log.e(TAG, "keys: $keys")
                val numTrans = accMap[keys]!!
                for (trans in numTrans) {
                    Log.e(TAG, "$trans")
                }

                addCard(parent, keys, numTrans)
            }
        }

        return rootView
    }

    private fun addCard(parent: ViewGroup, key: String, transactions: List<TransactionSer>) {

        val accNum = key.takeLast(4)
        val bank = key.dropLast(4)

        var debited = 0.0
        var credited = 0.0

        for (transaction in transactions) {
            if (TransactionUtils.isDebit(transaction)) {
                debited += transaction.amount
            } else {
                credited += transaction.amount
            }
        }

        val netDebit = debited > credited
        val net = if (netDebit) {
            debited - credited
        } else {
            credited - debited
        }

//        val netStr = if (netDebit) {
//            getString(R.string.debited, TransactionUtils.getFormattedAmount(net))
//        } else {
//            getString(R.string.credited, TransactionUtils.getFormattedAmount(net))
//        }

        val netStr = TransactionUtils.getAmountForUI(context!!, net, netDebit)
//        Log.e(TAG, "netStr: $netStr")

        val binding= DataBindingUtil.inflate<ItemAccSummaryBinding>(layoutInflater,
            R.layout.item_acc_summary, parent, false)
        val accSummaryView = binding.root
        binding.summary = AccountSummary(bank, accNum, TransactionUtils.getFormattedAmount(debited),
            TransactionUtils.getFormattedAmount(credited), netStr, netDebit)
        parent.addView(accSummaryView)

        accSummaryView.setOnClickListener {

            val intent = Intent(activity, TransactionActivity::class.java)
            val bundle = Bundle()

            bundle.putString(TransactionActivity.BANK, bank)
            bundle.putString(TransactionActivity.NUM, accNum)
            bundle.putSerializable(TransactionActivity.TRANSACTIONS, transactions.toTypedArray())
            intent.putExtras(bundle)

            startActivity(intent)
        }
    }
}