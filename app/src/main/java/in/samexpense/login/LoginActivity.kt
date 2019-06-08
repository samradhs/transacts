package `in`.samexpense.login

import `in`.samexpense.R
import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast


class LoginActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "LoginActivity"
        private const val REQUEST_READ_SMS = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        Log.e(TAG, "onCreate")

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) !=
                PackageManager.PERMISSION_GRANTED) {

            Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show()

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_SMS)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                showRationaleDialog()
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.READ_SMS), REQUEST_READ_SMS)
            }

        } else {
            Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show()
            //  Permission has already been granted
            readSms()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        when (requestCode) {
            REQUEST_READ_SMS -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                    Log.e(TAG, "yay!! got the permission")
                    readSms()
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }

            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }

    private fun showRationaleDialog() {

        AlertDialog.Builder(this)
            .setTitle("Title")
            .setMessage("Message??")
            .setPositiveButton("Positive") { _, which ->
                Log.e(TAG, "dialog: $which")
            }
            .setNegativeButton("Negative") { _, which ->
                Log.e(TAG, "dialog: $which")
            }
            .create()
            .show()
    }

    private fun readSms() {

        // public static final String INBOX = "content://sms/inbox";
        // public static final String SENT = "content://sms/sent";
        // public static final String DRAFT = "content://sms/draft";
        val cursor = contentResolver.query(Uri.parse("content://sms/inbox"), null, null, null, null)

        if (cursor!!.moveToFirst()) { // must check the result to prevent exception
            do {
                var msgData = ""
                for (idx in 0 until cursor.columnCount) {
                    val msg = cursor.getColumnName(idx) + ":" + cursor.getString(idx)
                    msgData += " $msg"
                }

                // use msgData
                Log.e(TAG, "msgData: $msgData")
            } while (cursor.moveToNext())
        } else {
            // empty box, no SMS
        }

        cursor.close()
    }
}
