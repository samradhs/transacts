package `in`.transacts.ui.landing

import `in`.transacts.R
import `in`.transacts.enums.AccountType
import `in`.transacts.ui.base.BaseActivity
import `in`.transacts.ui.TransactionSer
import `in`.transacts.ui.base.BaseViewPagerAdapter
import `in`.transacts.utils.DateTimeUtils
import `in`.transacts.utils.FileWriteUtils
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.layout_viewpager_tabs.*
import org.jetbrains.anko.doAsync
import org.json.JSONObject
import java.io.File

class LandingActivity : BaseActivity(), GetDbData {

    companion object {
        private const val TAG = "LandingActivity"
    }

    private lateinit var landingVM: LandingVM

    override fun getLayoutId(): Int {

        return R.layout.layout_viewpager_tabs
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
            it.ts >= monthStart
        }

        val remMonthTrans = response.filter {
            it.ts < monthStart
        }

        Log.e(TAG, "thisMonthTrans: ${thisMonthTrans.size}")

        val accTrans = thisMonthTrans.filter {
            it.accountType.equals(AccountType.BankAccount.accountType, true)
        }

        Log.e(TAG, "acc transactions::::: ${accTrans.size}")
        val ccTrans= thisMonthTrans.filter {
            it.accountType.equals(AccountType.CreditCard.accountType, true)
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

        setViewPager(thisMonthTrans, remMonthTrans, accTrans, ccTrans)
    }

    private fun setViewPager(thisMonthTrans: List<TransactionSer>, remMonthTrans: List<TransactionSer>,
                             accTrans: List<TransactionSer>, ccTrans: List<TransactionSer>) {

        val viewPager = cmn_viewpager
        val landingAdapter = BaseViewPagerAdapter(supportFragmentManager)

        landingAdapter.addFragment(SummaryFragment.newInstance(thisMonthTrans, remMonthTrans), getString(R.string.tab_summary))
        landingAdapter.addFragment(AccountsFragment.newInstance(accTrans), getString(R.string.tab_bank_account))
        landingAdapter.addFragment(AccountsFragment.newInstance(ccTrans), getString(R.string.tab_cc))
//        landingAdapter.addFragment(AccountsFragment.newInstance(ccTrans), getString(R.string.tab_cash)")

        viewPager.adapter = landingAdapter
        cmn_tabs.setupWithViewPager(viewPager)
    }
}