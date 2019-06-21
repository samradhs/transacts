package `in`.transacts.message

import `in`.transacts.db.Database
import `in`.transacts.db.Transaction
import `in`.transacts.enums.Bank.*
import `in`.transacts.parser.Parser
import `in`.transacts.utils.DateTimeUtils
import android.content.Context
import android.net.Uri
import android.util.Log
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

object MessageReader {

    private const val TAG   = "MessageReader"
    private const val INBOX = "content://sms/inbox"
//    private const val SENT  = "content://sms/sent"
//    private const val DRAFT = "content://sms/draft"

//    private const val SELECTION_QUERY = "address LIKE ? OR address LIKE ? OR address LIKE ?"

    //Indexes of projected columns
//    private const val ID        = 0
//    private const val THREAD_ID = 1
    private const val ADDRESS   = 2
    private const val DATE      = 3
    private const val BODY      = 4
//    private const val PERSON    = 3

    fun readSms(context: Context) {

        Log.e(TAG, "reading messages")

//        Log.e(TAG, "readSms")
//        val transaction1 = Transaction(1560543171342,
//                                            AccountType.BankAccount.accountType,
//                                            TransactionType.Debit.transactionType,
//                                            123.02,
//                                            "0025",
//                                            "a1",
//                                            Bank.ICICI.bankName)
//
//
//        val transaction2 = Transaction(1560543171344,
//                                        AccountType.BankAccount.accountType,
//                                        TransactionType.Credit.transactionType,
//                                        1203.02,
//                                        "0025",
//                                        "a2",
//                                        Bank.ICICI.bankName)
//
//
//        val transaction3 = Transaction(1560543171346,
//                                        AccountType.CreditCard.accountType,
//                                        TransactionType.Debit.transactionType,
//                                        1231.02,
//                                        "9007",
//                                        "a3",
//                                        Bank.ICICI.bankName)
//
//
//        val transaction4 = Transaction(1560543171348,
//                                        AccountType.CreditCard.accountType,
//                                        TransactionType.Credit.transactionType,
//                                        123.02,
//                                        "9007",
//                                        "a4",
//                                        Bank.ICICI.bankName)
//
//
//        val transaction5 = Transaction(1560543171350,
//                                        AccountType.CreditCard.accountType,
//                                        TransactionType.Debit.transactionType,
//                                        2123.02,
//                                        "3171",
//                                        "a5",
//                                        Bank.HDFC.bankName)
//
//        val transactionDao = Database.getDb(context).transactionDao()
////        transactionDao.insert(transaction1)
////        transactionDao.insert(transaction2)
////        transactionDao.insert(transaction3)
////        transactionDao.insert(transaction4)
////        transactionDao.insert(transaction5)
//
//        Log.e(TAG, "going to insert")
//        val transactions = arrayOf(transaction1, transaction2, transaction3, transaction4, transaction5)
//        transactionDao.insert(*transactions)
//
//
//        Log.e(TAG, "going to print all")
//        val list = transactionDao.getAll()
//        for (elem in list) {
//            Log.e(TAG, "$elem")
//        }
//
//        return


//        val message = "\"HSBC: Your credit card xxxxx4393 has been used at innovative retail conc for INR 2252.23 on 16/05/19. Available limit - INR 107189.17; current outstanding - INR 1810.83. If you want to report this as a fraud transaction and block your card, please call +914067173402 Or SMS 'BLOCK<space>CC<space>last 4 digits of your card number to '575750' from your registered mobile number.\""
//        Parser.parseMessage(context, message, HSBC)
//        return


        val startTime = DateTimeUtils.getMonthStartYearBack()
        val dateStr = SimpleDateFormat("dd-MMM-yyyy:HH:mm:ss", Locale.ENGLISH)
        Log.e(TAG, dateStr.format(startTime))

        Log.e(TAG, "readSms called")
        val projection      = arrayOf("_id", "thread_id", "address", "date", "body")
        val selectionArgs   = arrayOf( "%${ICICI.bankName}%",
                                                    "%${HDFC.bankName}%",
                                                    "%${HSBC.bankName}%" ,
                                                    "%${KOTAK.bankName}%",
                                                    "%${AMEX.bankName}%")

        var selectionQuery  = "(address LIKE ?"

        for (i in 1 until selectionArgs.size) {
            selectionQuery += " OR address LIKE ?"
        }

        selectionQuery += ") AND (person IS NULL) AND (date >= $startTime)"

        val cursor = context.contentResolver.query(Uri.parse(INBOX),
                                                   projection,
                                                   selectionQuery,
                                                   selectionArgs,
                                          "date DESC")

        val transactions: MutableList<Transaction> = mutableListOf()
        if (cursor!!.moveToFirst()) { // must check the result to prevent exception
            do {
//                Log.e(TAG, "in loop first line")
                var msgData = ""
                for (idx in 0 until cursor.columnCount) {
                    val msg = cursor.getColumnName(idx) + ":" + cursor.getString(idx)
                    msgData += " $msg"
                }

                // use msgData
//                Log.e(TAG, "msgData: $msgData")

                val address = cursor.getString(ADDRESS)
                val body = cursor.getString(BODY)
                val date = cursor.getLong(DATE)
                lateinit var response: JSONObject
                when {
                    address.contains(ICICI.bankName, true) -> response = Parser.parseMessage(context, body, ICICI)
                    address.contains(HDFC.bankName, true) -> response = Parser.parseMessage(context, body, HDFC)
                    address.contains(HSBC.bankName, true) -> response = Parser.parseMessage(context, body, HSBC)
                    address.contains(KOTAK.bankName, true) -> response = Parser.parseMessage(context, body, KOTAK)
                    address.contains(AMEX.bankName, true) -> response = Parser.parseMessage(context, body, AMEX)
                    else -> {
                        response = JSONObject()
                        response.put("not_supported", true)
                        Log.e(TAG, "bank not supported")
                    }
                }

//                val dateStr = SimpleDateFormat("dd-MMM-yyyy:HH:mm:ss", Locale.ENGLISH)
//                Log.e(TAG, "$response: ${dateStr.format(Date(date))}")
//                Log.e(TAG, "$response: ${dateStr.format(date)}")
                // write to db now
                if (response.length() != 0 && !(response.has("reject") && response.getBoolean("reject"))) {

//                    Log.e(TAG, "length: ${response.length()}")
//                    Log.e(TAG, "date: $date")
//                    Log.e(TAG, "a_type: ${response.getDouble("amt")}")

                    transactions.add(Transaction(date,
                                                response.getString("a_type"),
                                                response.getString("t_type"),
                                                response.getDouble("amt"),
                                                response.getString("num"),
                                                response.optString("merchant", ""),
                                                response.getString("bank")))

//                    Log.e(TAG, "added a row")
                }

//                Log.e(TAG, "${cursor.getColumnName(ADDRESS)}: ${cursor.getString(ADDRESS)}")
//                Log.e(TAG, "${cursor.getColumnName(5)}: ${cursor.getString(5)}")

//                Log.e(TAG,"in loop last line")



            } while (cursor.moveToNext())

            Parser.printCounters()
            writeToDb(context, transactions)

//            Log.e(TAG, "out of loop")
        } else {
            // empty box, no SMS
        }

//        Log.e(TAG, "Going to print counter")


//        Log.e(TAG, selectionQuery)

        cursor.close()
    }

    private fun writeToDb(context: Context, transactions: List<Transaction>) {

//        for (transaction in transactions) {
//            Log.e(TAG, "$transaction")
//        }

        val transactionDao = Database.getDb(context).transactionDao()
        transactionDao.insert(*transactions.toTypedArray())

        Log.e(TAG, "after insertion")
    }

    private fun getAllFromDb(context: Context): List<Transaction> {

        Log.e(TAG, "getAll from db")
        val transactionDao = Database.getDb(context).transactionDao()
        val list = transactionDao.getAll()
//        for (transaction in list) {
//            Log.e(TAG, "$transaction")
//        }

        return list
    }
}