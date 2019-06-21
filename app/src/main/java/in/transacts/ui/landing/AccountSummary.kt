package `in`.transacts.ui.landing

data class AccountSummary(val bankName: String,
                          val number: String,
                          val debited: String,
                          val credited: String,
                          val net: String,
                          val netDebit: Boolean)