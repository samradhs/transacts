package `in`.transacts


interface Const {

    interface ParseResponse {

        companion object {
            const val ACC_TYPE      = "a_type"
            const val TRANS_TYPE    = "t_type"
            const val AMOUNT        = "amt"
            const val NUMBER        = "num"
            const val MERCHANT      = "merchant"
            const val BANK          = "bank"
            const val REJECT        = "reject"
        }
    }
}