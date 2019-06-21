package `in`.transacts.ui.landing

import `in`.transacts.R
import `in`.transacts.enums.AccountType
import `in`.transacts.ui.TransactionSer
import `in`.transacts.utils.DateTimeUtils
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_landing.*

class LandingActivity : AppCompatActivity(), GetDbData {

    companion object {
        private const val TAG = "LandingActivity"
    }

    private lateinit var landingVM: LandingVM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing)

        landingVM = LandingVM()
        landingVM.getAllData(this, this)
    }

    override fun onResponse(response: List<TransactionSer>) {

//        for (transaction in response) {
//            Log.e(TAG, "$transaction: id::${transaction.id}")
//        }

        Log.e(TAG, "response: ${response.size}")

        val monthStart = DateTimeUtils.getCurrentMonthStart()
        Log.e(TAG, "${DateTimeUtils.getDateFormatted(monthStart)}, ${DateTimeUtils.getTimeFormatted(monthStart)}")

        val thisMonthTrans = response.filter {
            t -> t.ts >= monthStart
        }

        Log.e(TAG, "thisMonthTrans: ${thisMonthTrans.size}")

        val accTrans = thisMonthTrans.filter {
            t -> t.accountType.equals(AccountType.BankAccount.accountType, true)
        }

        Log.e(TAG, "acc transactions::::: ${accTrans.size}")
        val ccTrans= thisMonthTrans.filter {
            t -> t.accountType.equals(AccountType.CreditCard.accountType, true)
        }

        Log.e(TAG, "cc transactions::::: ${ccTrans.size}")

//        val cashTrans = thisMonthTrans.filter {
//            t -> t.accountType.equals(AccountType.Cash.accountType, true)
//        }
//
//        Log.e(TAG, "printing cash transactions:::::")
//        for (t in cashTrans) {
//            Log.e(TAG, "$t")
//        }

        setViewPager(accTrans, ccTrans)
    }

    private fun setViewPager(accTrans: List<TransactionSer>, ccTrans: List<TransactionSer>) {

        val viewPager = act_ovr_viewpager
        val landingAdapter = LandingAdapter(supportFragmentManager)

//        landingAdapter.addFragment(SummaryFragment.newInstance(), getString(R.string.tab_summary))
        landingAdapter.addFragment(AccountsFragment.newInstance(accTrans), getString(R.string.tab_bank_account))
        landingAdapter.addFragment(AccountsFragment.newInstance(ccTrans), getString(R.string.tab_cc))
//        landingAdapter.addFragment(AccountsFragment.newInstance(ccTrans), getString(R.string.tab_cash)")

        viewPager.adapter = landingAdapter
        act_ovr_bot_dots.setupWithViewPager(viewPager)
    }
}