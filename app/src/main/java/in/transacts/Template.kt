package `in`.transacts

import java.io.Serializable

//class Template {
//    var msg: String?    = null
//    var a_type: String? = null
//    var t_type: String? = null
//    var dateF: String?  = null
//
//    constructor() : super()
//
//    constructor(msg: String, a_type: String, t_type: String, dateF: String?) : super() {
//
//        this.msg    = msg
//        this.a_type = a_type
//        this.t_type = t_type
//        if (dateF != null) this.dateF = dateF
//    }
//}

data class Template(val msg: String, val a_type: String, val t_type: String, val dateF: String?)