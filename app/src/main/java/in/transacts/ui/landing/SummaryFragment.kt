package `in`.transacts.ui.landing

import `in`.transacts.R
import `in`.transacts.enums.AccountType
import `in`.transacts.ui.TransactionSer
import `in`.transacts.ui.monthtransactions.MonthTransactionsActivity
import `in`.transacts.utils.DateTimeUtils
import `in`.transacts.utils.TransactionUtils
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.LargeValueFormatter
import org.jetbrains.anko.find
import java.util.ArrayList
import kotlin.collections.LinkedHashMap
import kotlin.collections.List
import kotlin.collections.MutableCollection
import kotlin.collections.MutableMap
import kotlin.collections.filter
import kotlin.collections.iterator
import kotlin.collections.mutableMapOf
import kotlin.collections.set
import kotlin.collections.sortedDescending
import kotlin.collections.toHashSet
import kotlin.collections.toList
import kotlin.collections.toTypedArray

class SummaryFragment: Fragment() {

    companion object {
        private const val TAG               = "SummaryFragment"
        private const val THIS_MONTH_TRANS  = "thisMonthTrans"
        private const val REM_MONTH_TRANS   = "remMonthTrans"

        fun newInstance(thisMonthTrans: List<TransactionSer>, remMonthTrans: List<TransactionSer>): SummaryFragment {

            val summaryFragment = SummaryFragment()
            val bundle = Bundle()
            bundle.putSerializable(THIS_MONTH_TRANS, thisMonthTrans.toTypedArray())
            bundle.putSerializable(REM_MONTH_TRANS, remMonthTrans.toTypedArray())
            summaryFragment.arguments = bundle
            return summaryFragment
        }
    }

    private lateinit var thisMonthTrans: List<TransactionSer>
    private lateinit var remMonthTrans: List<TransactionSer>

    private var thisMonthAcc    = 0.0
    private var thisMonthCC     = 0.0
    private var last12MonthAcc  = 0.0
    private var last12MonthCC   = 0.0

    private val monthWiseCCTrans: MutableMap<String, Double>  = mutableMapOf()
    private val monthWiseAccTrans: MutableMap<String, Double> = mutableMapOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.e(TAG, "onCreate")

//        condition to check for calculations to be done once
        if (thisMonthAcc != 0.0 || thisMonthCC != 0.0 || last12MonthAcc != 0.0 || last12MonthCC != 0.0) return

        thisMonthTrans = (arguments!!.getSerializable(THIS_MONTH_TRANS) as Array<TransactionSer>).toList()
        Log.e(TAG, "printing this month transactions")
        for (transaction in thisMonthTrans) {
//            Log.e(TAG, "this: ${DateTimeUtils.getDateFormatted(transaction.ts)}")

            if (!TransactionUtils.isDebit(transaction)) continue
            if (transaction.accountType.equals(AccountType.CreditCard.accountType, true)) {
                thisMonthCC += transaction.amount
            } else {
                thisMonthAcc += transaction.amount
            }
        }

        remMonthTrans = (arguments!!.getSerializable(REM_MONTH_TRANS) as Array<TransactionSer>).toList()
        Log.e(TAG, "printing rem month transactions")
        for (transaction in remMonthTrans) {
//            Log.e(TAG, "rem: ${DateTimeUtils.getDateFormatted(transaction.ts)}, monthStamp: ${DateTimeUtils.getMonthStamp(transaction.ts)}")
            if (!TransactionUtils.isDebit(transaction)) continue
            val monthStamp = DateTimeUtils.getMonthStamp(transaction.ts)

            if (transaction.accountType.equals(AccountType.CreditCard.accountType, true)) {
                last12MonthCC += transaction.amount

                val ccDebitAmount = monthWiseCCTrans[monthStamp] ?: 0.0
                monthWiseCCTrans[monthStamp] = ccDebitAmount + transaction.amount

            } else {
                last12MonthAcc += transaction.amount

                val accDebitAmount = monthWiseAccTrans[monthStamp] ?: 0.0
                monthWiseAccTrans[monthStamp] = accDebitAmount + transaction.amount
            }

        }

        Log.e(TAG, "going to print acc trans")
        for (entry in monthWiseAccTrans) {
            Log.e(TAG, "${entry.key}: ${entry.value}")
        }

        Log.e(TAG, "going to print cc trans")
        for (entry in monthWiseCCTrans) {
            Log.e(TAG, "${entry.key}: ${entry.value}")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val rootView = inflater.inflate(R.layout.fragment_summary, container, false)

//        val rootView = inflater.inflate(R.layout.layout_linear_container, container, false)
//        val parent: ViewGroup = rootView as ViewGroup
//
//        if (accMap.isEmpty()) {
//            val placeHolder = inflater.inflate(R.layout.layout_page_placeholder, parent, false) as TextView
//            placeHolder.text = getString(R.string.no_acc_found)
//            parent.addView(placeHolder)
//
//        } else {
//            for (keys in accMap.keys) {
//                Log.e(AccountsFragment.TAG, "keys: $keys")
//                val numTrans = accMap[keys]!!
//                for (trans in numTrans) {
//                    Log.e(AccountsFragment.TAG, "$trans")
//                }
//
//                addCard(parent, keys, numTrans)
//            }
//        }

        rootView.find<TextView>(R.id.total_debit_amt).text = TransactionUtils
            .getAmountForUI(context!!, thisMonthCC + thisMonthAcc, true)

        val thisMonthExp = rootView.find<View>(R.id.this_month_exp)
        thisMonthExp.find<TextView>(R.id.sum_acc_exp_amt).text = TransactionUtils
            .getAmountForUI(context!!, thisMonthAcc, true)
        thisMonthExp.find<TextView>(R.id.sum_cc_exp_amt).text = TransactionUtils
            .getAmountForUI(context!!, thisMonthCC, true)

        val last12MonthExp = rootView.find<View>(R.id.last_12_months_exp)
        last12MonthExp.find<TextView>(R.id.sum_title).text =
            getString(R.string.last_months)
        last12MonthExp.find<TextView>(R.id.sum_acc_exp_amt).text = TransactionUtils
            .getAmountForUI(context!!, last12MonthAcc, true)
        last12MonthExp.find<TextView>(R.id.sum_cc_exp_amt).text = TransactionUtils
            .getAmountForUI(context!!, last12MonthCC, true)

        val accChart = rootView.find<BarChart>(R.id.summary_chart_acc)
        val ccChart = rootView.find<BarChart>(R.id.summary_chart_cc)

        setChart(accChart, monthWiseAccTrans.values)
        setChart(ccChart, monthWiseCCTrans.values)

        for (entry in monthWiseAccTrans) {
            val monthStartEnd = DateTimeUtils.getMonthStartEnd(entry.key)
            Log.e(TAG, entry.key)
            Log.e(TAG, "${DateTimeUtils.getDateFormatted(monthStartEnd.first)}, ${DateTimeUtils.getTimeFormatted(monthStartEnd.first)}")
            Log.e(TAG, "${DateTimeUtils.getDateFormatted(monthStartEnd.second)}, ${DateTimeUtils.getTimeFormatted(monthStartEnd.second)}")
        }

        setSpinner(rootView)

        return rootView
    }

    private fun setChart(chart: BarChart, values: MutableCollection<Double>) {

//        chart.layoutParams.height = (getScreenHeight() * 0.8f).toInt()
        chart.description.isEnabled = false

        // scaling can now only be done on x- and y-axis separately
        chart.setScaleEnabled(false)
        chart.setDrawBarShadow(false)
        chart.setDrawGridBackground(false)
        chart.setTouchEnabled(false)

        val l                 = chart.legend
        l.verticalAlignment   = Legend.LegendVerticalAlignment.TOP
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        l.orientation         = Legend.LegendOrientation.VERTICAL
        l.yOffset             = 0f
        l.xOffset             = 15f
        l.yEntrySpace         = 0f
        l.textSize            = 8f
        l.setDrawInside(true)

        val xAxis         = chart.xAxis
        xAxis.granularity = 1f
        xAxis.position    = XAxis.XAxisPosition.BOTTOM
        xAxis.setCenterAxisLabels(false)
        xAxis.setDrawLabels(false)
        xAxis.setDrawGridLines(false)

        val leftAxis            = chart.axisLeft
        leftAxis.valueFormatter = LargeValueFormatter()
        leftAxis.spaceTop       = 5f
        leftAxis.axisMinimum    = 0f // this replaces setStartAtZero(true)
        leftAxis.setDrawGridLines(true)

        chart.axisRight.isEnabled = false
        chart.setDrawValueAboveBar(false)

//        val groupSpace  = 0.1f
//        val barSpace    = 0f
        val barWidth    = 0.4f
//        val groupCount  = 3

        val values1 = ArrayList<BarEntry>()
//        values1.add(BarEntry(0f, 65f))
//        values1.add(BarEntry(1f, 5f))
//        values1.add(BarEntry(2f, 32f))
//        values1.add(BarEntry(3f, 48f))
//        values1.add(BarEntry(4f, 7f))
//        values1.add(BarEntry(5f, 45f))
//        values1.add(BarEntry(6f, 48f))
//        values1.add(BarEntry(7f, 10f))
//        values1.add(BarEntry(8f, 48f))
//        values1.add(BarEntry(9f, 48f))
//        values1.add(BarEntry(10f, 48f))
//        values1.add(BarEntry(11f, 48f))
//        values1.add(BarEntry(12f, 48f))

        var i = 1f
        for (vals in values) {
            values1.add(BarEntry(i++, vals.toFloat()))
        }

        val set1: BarDataSet

        if (chart.data != null && chart.data.dataSetCount > 0) {

            set1        = chart.data.getDataSetByIndex(0) as BarDataSet
            set1.values = values1
            chart.data.notifyDataChanged()
            chart.notifyDataSetChanged()

        } else {
            set1        = BarDataSet(values1, "Original")
            set1.color  = Color.rgb(229, 229, 229)

            val data = BarData(set1)
            data.setValueFormatter(LargeValueFormatter())
            chart.data = data
        }

        // specify the width each bar should have
        chart.barData.barWidth  = barWidth

        // restrict the x-axis range
        chart.xAxis.axisMinimum = 0f

        // barData.getGroupWith(...) is a helper that calculates the width each group needs based on the provided parameters
//        chart.xAxis.axisMaximum = chart.barData.getGroupWidth(groupSpace, barSpace) * groupCount
//        chart.groupBars(-0.1f, groupSpace, barSpace)
        chart.barData.setDrawValues(false)
//        chart.setOnChartValueSelectedListener(this)

        chart.invalidate()
    }

    private fun setSpinner(rootView: View) {

//        Log.e(TAG, "monthWiseAccTrans.keys")
//        for (key in monthWiseAccTrans.keys) {
//            Log.e(TAG, key)
//        }

//        val arrayList =
//        arrayList.add("201805")
//        Log.e(TAG, "monthWiseCCTrans.keys")
//        for (key in arrayList) {
//            Log.e(TAG, key)
//        }

        val keys = monthWiseAccTrans.keys.toHashSet()
        keys.addAll(monthWiseCCTrans.keys.toHashSet())
        val sortedListStamps = keys.sortedDescending()
        val monthStampMap = LinkedHashMap<String, String>()
        for (monthStamp in sortedListStamps) {
            monthStampMap[DateTimeUtils.getUIMonthFromMonthStamp(context!!, monthStamp)] = monthStamp
        }

        val sortedMonths = monthStampMap.keys

        val spinner = rootView.find<Spinner>(R.id.select_month_picker)
        val arrayAdapter = ArrayAdapter<String>(context!!, android.R.layout.simple_spinner_item, sortedMonths.toList())
        arrayAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice)
        spinner.adapter = arrayAdapter

//        spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
//
//            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//
//                Log.e(TAG, "onItemSelected: $parent, $view, $position, $id")
//            }
//
//            override fun onNothingSelected(parent: AdapterView<*>?) {
//
//                Log.e(TAG, "onNothingSelected: $parent")
//            }
//        }

        rootView.find<View>(R.id.select_month_btn).setOnClickListener {
            val month = spinner.selectedItem.toString()
            val monthStamp = monthStampMap[month] ?: return@setOnClickListener
            Log.e(TAG, "$month ::::: $monthStamp")
            val monthStartEnd = DateTimeUtils.getMonthStartEnd(monthStamp)

            val monthTransactions = remMonthTrans.filter {
                it.ts >= monthStartEnd.first && it.ts <= monthStartEnd.second
            }

            openMonthTransactionsActivity(month, monthTransactions)
        }
    }

    private fun openMonthTransactionsActivity(month: String, monthTransactions: List<TransactionSer>) {

        val extras = Bundle()
        extras.putString(MonthTransactionsActivity.MONTH, month)
        extras.putSerializable(MonthTransactionsActivity.ALL_TRANSACTIONS, monthTransactions.toTypedArray())

        val openMonthTransIntent = Intent(context!!, MonthTransactionsActivity::class.java)
        openMonthTransIntent.putExtras(extras)
        startActivity(openMonthTransIntent)
    }
}