package `in`.transacts.parser

import `in`.transacts.Const
import `in`.transacts.enums.Bank
import `in`.transacts.R
import `in`.transacts.Template
import android.content.Context
import android.util.Log
import com.google.gson.Gson
import org.json.JSONObject
import java.io.BufferedReader
import java.util.regex.Pattern




object Parser {

    private const val TAG = "Parser"
    private val templateMap = HashMap<String, Array<Template>>()

    private var counter = 0
    private var matchCounter = 0
    private var rejectCounter = 0
    private var rupeeCount = 0

//    private val rejectionStrings = arrayOf(
//                                            "Appln",
//                                            "Applicant",
//                                            "pin is generated",
//                                            "delivered",
//                                            "delivery",
//                                            "dispatched",
//                                            "dynamic access code",
//                                            "wrong pin entered",
//                                            "feedback",
//                                            "net banking password",
//                                            "failed",
//                                            "wrong pwd",
//                                            "request for",
//                                            "requested",
//                                            "as per your request",
//                                            "generate pin",
//                                            "never share confidential details",
//                                            "as per rbi",
//                                            "requested",
////                                          "request for",
////                                          "we miss you",
////                                          "as per your request",
////                                          "Application",
////                                          "Pre-approved",
////                                          "increase the credit limit"
//                                            "month you have completed")

    private val rupees = arrayOf("INR", "rs", "balance")

    fun parseMessage(context: Context, message: String, bank: Bank): JSONObject {

//        Log.v(TAG, "$bank:$message")
        var templates = templateMap[bank.bankName]
        if (templates == null) {
            templates = readJSONfromFile(context, bank.templateFile)
            templateMap[bank.bankName] = templates
        }

        return parse(context, message, templates, bank)
    }

    fun printCounters() {
        Log.e(TAG, "matched: $matchCounter & unmatched: $counter & rejected: $rejectCounter & rupeeCount: $rupeeCount")
    }

    private fun parse(context: Context, message: String, templates: Array<Template>, bank: Bank): JSONObject {

        val returnObj = JSONObject()

//        if (matchCounter >= 10) return returnObj

        if (message.isBlank() || templates.isEmpty()) {
            Log.e(TAG, "message: ${message.length} & templates: ${templates.size}")
            return returnObj
        }

        val forBasicCheck = message.replace("ers", "", true)  // offers, members, diners, numbers, vouchers, customers, fraudsters, reminders, transfers, letters, version, person
                                        .replace("hrs", "", true)        // hrs
                                        .replace("urs", "", true)        // hours, thursday
                                        .replace("irs", "", true)      // first

        var contains = false
        for (rupee in rupees) {
            contains = forBasicCheck.contains(rupee, true)
//            Log.e(TAG, "$message, $rupee, $contains")
            if (contains) break
        }

        if (!contains) {
            rupeeCount++
            returnObj.put(Const.ParseResponse.REJECT, true)
            return returnObj
        }

        // shadowing name since method parameters cannot be changed
        var messageStr = message.replace("?", "").replace("\n", "")
        if (messageStr.last() == '.') messageStr = messageStr.dropLast(1)

        var matched = false
        for (template in templates) {

            var templateStr = template.msg.replace("?", "")
            if (templateStr.last() == '.') templateStr = templateStr.dropLast(1)

//            Log.e(TAG, "templateStr: $templateStr")
            val pattern = templateStr.replace("%.*?%".toRegex(), ".*")
//            Log.e(TAG, "$pattern: ${bank.bankName}")

            matched = Pattern.matches(pattern, messageStr)
//            Log.e(TAG, "matched: $matched")
            if (!matched) continue

//            Log.e(TAG, "string got matched: $message\n$pattern")

            if (template.t_type == "reject") {
                returnObj.put(Const.ParseResponse.REJECT, true)
                return returnObj
            }

            getValues(returnObj, messageStr, templateStr)

            if (returnObj.has(Const.ParseResponse.REJECT) && returnObj[Const.ParseResponse.REJECT] == true) {
                Log.e(TAG, "message: $message, template: $template")
                break
            }

            returnObj.put(Const.ParseResponse.ACC_TYPE, template.a_type)
            returnObj.put(Const.ParseResponse.TRANS_TYPE, template.t_type)
            returnObj.put(Const.ParseResponse.BANK,   bank.bankName)

//            Log.e(TAG, "returnObj: $returnObj")
            matchCounter++
            break
        }

        var rejected = false
        val rejectionStrs = context.resources.getStringArray(R.array.rejection_strings)
        if (!matched) {

            for (str in rejectionStrs) {
                if (message.contains(str, true)) {
//                    Log.e(TAG, "rejecting:::: $message due to :: $str")
                    rejectCounter++
                    rejected = true
                    break
                }
            }
        }

        if (!matched && !rejected) {
            //todo handle string not matched with template case
            counter++
            Log.e(TAG, "string not matched with any template: $message, ${bank.bankName}")
        }

        return returnObj
    }

    private fun readJSONfromFile(context: Context, f:String): Array<Template> {

        val gson = Gson()
        val bufferedReader: BufferedReader = context.assets.open("templates/$f").bufferedReader()
        // Read the text from buffferReader and store in String variable
        val inputString = bufferedReader.use { it.readText() }

        //Convert the Json File to Gson Object
        val templates = gson.fromJson(inputString, Array<Template>::class.java)
        for (template in templates) {
//            Log.v(TAG, "$template")
        }

        return templates
    }

    @Suppress("NAME_SHADOWING")
    private fun getValues(returnObj: JSONObject, message: String, template: String) {

        var message = message
        var template = template

//        Log.e(TAG, "message: $message, template: $template")

        val splitted = template.split("%.*?%".toRegex())

        for (split in splitted) {

//            Log.e(TAG, "split: $split")

            if (split == ".") {
                message = replaceLast(message, ".", "|")
                template = replaceLast(template, ".", "|")

            } else {
                message = message.replaceFirst(
                    split.toRegex(),
                    "|"
                ) // replace on regex because template might have regex but message will have values
                template = template.replaceFirst(
                    split,
                    "|"
                ) // replace on value basis because template will have regex format and replace on value will work for it
            }
        }

        val keys = template.split("\\|".toRegex())
        val vals = message.split("\\|".toRegex())

        if (keys.size != vals.size) {

            Log.e(TAG, "lengths not matched: $keys, $vals")
//            Log.e(TAG, "message: $message, template: $template")
            returnObj.put(Const.ParseResponse.REJECT, true)
            return
        }

//        Log.e(TAG, "lengths matched")
        for (i in 0 until keys.size) {
            var key = keys[i]
            var value = vals[i]

            if (key.isEmpty() || value.isEmpty()) continue
            key = key.substring(1, key.length - 1)

            if (key == "amt" || key == "bal") {
                value = value.replace(",", "")
            }

            returnObj.put(key, value)
        }
    }

    private fun replaceLast(string: String, substring: String, replacement: String): String {
        val index = string.lastIndexOf(substring)
        return if (index == -1) string
                else string.substring(0, index) + replacement + string.substring(index + substring.length)
    }
}