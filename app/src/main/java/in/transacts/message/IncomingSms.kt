package `in`.transacts.message

import `in`.transacts.Const
import `in`.transacts.db.Database
import `in`.transacts.db.Transaction
import `in`.transacts.enums.Bank
import `in`.transacts.parser.Parser
import `in`.transacts.ui.landing.LandingActivity
import `in`.transacts.utils.FileWriteUtils
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.telephony.SmsManager
import android.telephony.SmsMessage
import android.util.Log
import org.jetbrains.anko.doAsync
import org.json.JSONObject
import java.lang.Exception

class IncomingSms: BroadcastReceiver() {

    companion object {
        private const val TAG = "IncomingSms"
        private const val PDUS = "pdus"
        private const val LOGS_FILE = "Logs.txt"
    }

    private val sms = SmsManager.getDefault()

//    @RequiresApi(Build.VERSION_CODES.P)
    override fun onReceive(context: Context?, intent: Intent?) {

        Log.e(TAG, "onReceive")
        doAsync {

            val logJson = JSONObject()
            logJson.put("onReceive", true)
            FileWriteUtils.writeJsonToFileExternal(LOGS_FILE, logJson)
        }

        if (intent == null || intent.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) return
        val bundle = intent.extras ?: return

        try {
            val puds = bundle.get(PDUS) as Array<Object>
            for (i in 0 until puds.size) {
                // do anything with SMS
                val currentMessage = SmsMessage.createFromPdu(puds[i] as ByteArray, SmsMessage.FORMAT_3GPP)
                // todo check for different versions

                val body = currentMessage.displayMessageBody
                val ts = currentMessage.timestampMillis
                val address = currentMessage.displayOriginatingAddress


                doAsync {

                    val logJson = JSONObject()
                    logJson.put("body", body)
                    logJson.put("ts", ts)
                    logJson.put("address", address)
                    logJson.put("originating address", currentMessage.originatingAddress)
                    FileWriteUtils.writeJsonToFileExternal(LOGS_FILE, logJson)
                }

                var response: JSONObject
                when {
                    address.contains(Bank.ICICI.bankName, true) -> response = Parser.parseMessage(context!!, body,
                        Bank.ICICI
                    )
                    address.contains(Bank.HDFC.bankName, true) -> response = Parser.parseMessage(context!!, body,
                        Bank.HDFC
                    )
                    address.contains(Bank.HSBC.bankName, true) -> response = Parser.parseMessage(context!!, body,
                        Bank.HSBC
                    )
                    address.contains(Bank.KOTAK.bankName, true) -> response = Parser.parseMessage(context!!, body,
                        Bank.KOTAK
                    )
                    address.contains(Bank.AMEX.bankName, true) -> response = Parser.parseMessage(context!!, body,
                        Bank.AMEX
                    )
                    else -> {
                        response = JSONObject()
                        Log.e(TAG, "bank not supported")
                    }
                }

                if (response.length() != 0 && !(response.has(Const.ParseResponse.REJECT) && response.getBoolean(Const.ParseResponse.REJECT))) {

//                    Log.e(TAG, "length: ${response.length()}")
//                    Log.e(TAG, "date: $date")
//                    Log.e(TAG, "a_type: ${response.getDouble("amt")}")


                    val transaction = Transaction(ts,
                        response.getString(Const.ParseResponse.ACC_TYPE),
                        response.getString(Const.ParseResponse.TRANS_TYPE),
                        response.getDouble(Const.ParseResponse.AMOUNT),
                        response.getString(Const.ParseResponse.NUMBER),
                        response.optString(Const.ParseResponse.MERCHANT, ""),
                        response.getString(Const.ParseResponse.BANK))

                    doAsync {
                        val transactionDao = Database.getDb(context!!).transactionDao()
                        transactionDao.insert(transaction)
                    }

//                    Log.e(TAG, "added a row")
                }
            }

        } catch (e: Exception) {
            Log.e(TAG, "Exception in Incoming SMS: $e")
        }
    }
}