package `in`.transacts.enums

enum class Bank(val bankName: String, val templateFile: String) {

    ICICI("icici", "icici.json"),
    HDFC("hdfc", "hdfc.json"),
    HSBC("hsbc", "hsbc.json"),
    KOTAK("kotak", "kotak.json"),
    AMEX("amex", "amex.json")
}