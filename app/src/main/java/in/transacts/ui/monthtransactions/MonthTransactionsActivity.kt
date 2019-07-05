package `in`.transacts.ui.monthtransactions

import `in`.transacts.R
import `in`.transacts.enums.AccountType
import `in`.transacts.ui.TransactionSer
import `in`.transacts.ui.base.BaseActivity
import `in`.transacts.ui.base.BaseViewPagerAdapter
import `in`.transacts.ui.landing.AccountsFragment
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.layout_viewpager_tabs.*

class MonthTransactionsActivity: BaseActivity() {

    companion object {
        internal const val MONTH            = "month"
        internal const val ALL_TRANSACTIONS = "allTransactions"
        private const val TAG               = "MonthTransActivity"
    }

    override fun getLayoutId(): Int {
        return R.layout.layout_viewpager_tabs
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.e(TAG, "onCreate")

        val bundle = intent.extras!!
        val month = bundle.getString(MONTH)
        val transactions = (bundle.getSerializable(ALL_TRANSACTIONS) as Array<TransactionSer>).toList()

        title = month!!
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        Log.e(TAG, "thisMonthTrans: ${transactions.size}")

        val accTrans = transactions.filter {
            it.accountType.equals(AccountType.BankAccount.accountType, true)
        }

        Log.e(TAG, "acc transactions::::: ${accTrans.size}")
        val ccTrans= transactions.filter {
            it.accountType.equals(AccountType.CreditCard.accountType, true)
        }

        Log.e(TAG, "cc transactions::::: ${ccTrans.size}")
        setViewPager(accTrans, ccTrans)
    }

    private fun setViewPager(accTrans: List<TransactionSer>, ccTrans: List<TransactionSer>) {

        val viewPager = cmn_viewpager
        val monthTransAdapter = BaseViewPagerAdapter(supportFragmentManager)
        monthTransAdapter.addFragment(AccountsFragment.newInstance(accTrans), getString(R.string.tab_bank_account))
        monthTransAdapter.addFragment(AccountsFragment.newInstance(ccTrans), getString(R.string.tab_cc))
//        landingAdapter.addFragment(AccountsFragment.newInstance(ccTrans), getString(R.string.tab_cash)")

        viewPager.adapter = monthTransAdapter
        cmn_tabs.setupWithViewPager(viewPager)
    }
}