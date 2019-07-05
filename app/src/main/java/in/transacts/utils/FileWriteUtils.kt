package `in`.transacts.utils

import android.os.Environment
import org.json.JSONObject
import java.io.*


object FileWriteUtils {

    private const val FOLDER_NAME = "Transacts"

    fun  writeJsonToFileExternal(fileName: String, toWrite: JSONObject): Boolean {

        if (canAccessExternalStorage()) return false

        val folder = File(Environment.getExternalStorageDirectory(), FOLDER_NAME)
        if (!folder.exists()) {
            if (!folder.mkdir()) return false
        }

        val myFile = File(folder, fileName)
        myFile.createNewFile()

        val fOut = FileOutputStream(myFile, true)
        val myOutWriter = OutputStreamWriter(fOut)
        myOutWriter.append(toWrite.toString())
        myOutWriter.append("\n")
        myOutWriter.close()
        fOut.close()

        return true

    }

    fun readJsonFromFileExternal(fileName: String): String {

        if (canAccessExternalStorage()) return ""

        val folder = File(Environment.getExternalStorageDirectory(), FOLDER_NAME)
        if (!folder.exists())  return ""

        val myFile = File(folder, fileName)
        if (!myFile.exists()) return ""

        val bufferedReader = BufferedReader(InputStreamReader(FileInputStream(myFile)))
        return bufferedReader.use { it.readText() }
    }

    private fun canAccessExternalStorage(): Boolean {

        return Environment.MEDIA_MOUNTED_READ_ONLY == Environment.getExternalStorageState() &&
                Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()
    }

}