package `in`.transacts.ui.login

import `in`.transacts.R
import `in`.transacts.message.MessageReader
import `in`.transacts.prefs.SharedPref
import `in`.transacts.ui.landing.LandingActivity
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.an.biometric.BiometricCallback
import com.an.biometric.BiometricManager
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.longToast
import org.jetbrains.anko.uiThread


class LoginActivity : AppCompatActivity(), BiometricCallback {

    companion object {
        private const val TAG = "LoginActivity"
        private const val REQUEST_READ_SMS      = 1001
        private const val REQUEST_WRITE_STORAGE = 1002
    }

    private var authorized: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        Log.e(TAG, "onCreate")
//        showBiometricDialog()
    }

    override fun onResume() {
        super.onResume()
        Log.e(TAG, "onResume")
        showBiometricDialog()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        when (requestCode) {
            REQUEST_READ_SMS -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                    Log.e(TAG, "yay!! got the permission")
                    afterSmsPermission()

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    showSmsRationaleDialog()
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

    override fun onSdkVersionNotSupported() {
        Log.e(TAG, "onSdkVersionNotSupported")
//        longToast("onSdkVersionNotSupported")
    }

    override fun onBiometricAuthenticationPermissionNotGranted() {
        Log.e(TAG, "onBiometricAuthenticationPermissionNotGranted")
//        longToast("onBiometricAuthenticationPermissionNotGranted")
    }

    override fun onAuthenticationCancelled() {
        Log.e(TAG, "onAuthenticationCancelled")
//        longToast("onAuthenticationCancelled")
        showNecessaryFingerprint()
    }

    override fun onBiometricAuthenticationInternalError(error: String?) {
        Log.e(TAG, "onBiometricAuthenticationInternalError: $error")
//        longToast("onBiometricAuthenticationInternalError: $error")
    }

    override fun onBiometricAuthenticationNotSupported() {
        Log.e(TAG, "onBiometricAuthenticationNotSupported")
//        longToast("onBiometricAuthenticationNotSupported")
    }

    override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
        Log.e(TAG, "onAuthenticationError: $errorCode, $errString")
        longToast("$errString")
        showNecessaryFingerprint()
    }

    override fun onAuthenticationHelp(helpCode: Int, helpString: CharSequence?) {
        Log.e(TAG, "onAuthenticationHelp: $helpCode, $helpString")
//        longToast("onAuthenticationHelp: $helpCode, $helpString")
        showNecessaryFingerprint()
    }

    override fun onAuthenticationSuccessful() {
        authorized = true
        Log.e(TAG, "onAuthenticationSuccessful")
        longToast("Authentication Successful")
        checkSmsPermission()
    }

    override fun onAuthenticationFailed() {
        Log.e(TAG, "onAuthenticationFailed")
//        longToast("onAuthenticationFailed")
//        showNecessaryFingerprint()
    }

    override fun onBiometricAuthenticationNotAvailable() {
        Log.e(TAG, "onBiometricAuthenticationNotAvailable")
//        longToast("onBiometricAuthenticationNotAvailable")
    }

    private fun checkSmsPermission() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) !=
                PackageManager.PERMISSION_GRANTED) {

//            Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show()

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_SMS)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                showSmsRationaleDialog()
            } else {
                // No explanation needed, we can request the permission.
                getSmsPermission()
            }

        } else {
//            Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show()
            //  Permission has already been granted
            afterSmsPermission()

        }
    }

//    private fun checkStoragePermission() {
//
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
//            PackageManager.PERMISSION_GRANTED) {
//
////            Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show()
//
//            // Permission is not granted
//            // Should we show an explanation?
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//                // Show an explanation to the user *asynchronously* -- don't block
//                // this thread waiting for the user's response! After the user
//                // sees the explanation, try again to request the permission.
//                showSmsRationaleDialog()
//            } else {
//                // No explanation needed, we can request the permission.
//                getSmsPermission()
//            }
//
//        } else {
////            Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show()
//            //  Permission has already been granted
//            afterSmsPermission()
//
//        }
//    }

    private fun getSmsPermission() {
        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS), REQUEST_READ_SMS)
    }
//
//    private fun getStoragePermission() {
//        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
//            REQUEST_WRITE_STORAGE)
//    }

    private fun showSmsRationaleDialog() {

        AlertDialog.Builder(this)
            .setTitle(R.string.perm_message_title)
            .setMessage(R.string.perm_message_text)
            .setPositiveButton(R.string.perm_message_pos_btn) { _, which ->
                Log.e(TAG, "dialog: $which")
                getSmsPermission()
            }
            .setNegativeButton(R.string.perm_message_neg_btn) { _, which ->
                Log.e(TAG, "dialog: $which")
                finish()
            }
            .setCancelable(false)
            .create()
            .show()
    }

//    private fun showStorageRationaleDialog() {
//
//        AlertDialog.Builder(this)
//            .setTitle(R.string.perm_message_title_storage)
//            .setMessage(R.string.perm_message_text)
//            .setPositiveButton(R.string.perm_message_pos_btn) { _, which ->
//                Log.e(TAG, "dialog: $which")
//                getSmsPermission()
//            }
//            .setNegativeButton(R.string.perm_message_neg_btn) { _, which ->
//                Log.e(TAG, "dialog: $which")
//                finish()
//            }
//            .setCancelable(false)
//            .create()
//            .show()
//    }

    private fun afterSmsPermission() {

        val context = this
        doAsync {

            if (!SharedPref.areMessagesRead(context)) {
                MessageReader.readSms(context)
                SharedPref.saveMessagesRead(context)
            }

            uiThread {

                val openLanding = Intent(this@LoginActivity, LandingActivity::class.java)
                openLanding.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(openLanding)
            }
        }
    }

    private fun showBiometricDialog() {

        if (authorized) return

        Log.e(TAG, "showBiometricDialog")
        BiometricManager.BiometricBuilder(this)
            .setTitle(getString(R.string.fingerprint_bottom_sheet_title))
            .setSubtitle(getString(R.string.fingerprint_bottom_sheet_subtitle))
            .setDescription(getString(R.string.fingerprint_bottom_sheet_description))
            .setNegativeButtonText(getString(R.string.fingerprint_bottom_sheet_neg_btn))
            .build()
            .authenticate(this)
    }

    private fun showNecessaryFingerprint() {

        Log.e(TAG, "showNecessaryFingerprint dialog")
        AlertDialog.Builder(this)
            .setTitle(R.string.fingerprint_dialog_title)
            .setMessage(R.string.fingerprint_dialog_message)
            .setPositiveButton(R.string.fingerprint_dialog_pos_btn) { _, _ ->
                showBiometricDialog()
            }
            .setNegativeButton(R.string.fingerprint_dialog_neg_btn) { _, _ ->
                finish()
            }
            .setCancelable(false)
            .create()
            .show()
    }
}
