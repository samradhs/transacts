package `in`.transacts.ui.base

import `in`.transacts.R
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_base.*

/**
 * Base Activity is parent activity for all activities other than login activity.
 * Common methods that are required by most of the activities such as setting layout and setting up toolbar, etc.
 */
abstract class BaseActivity : AppCompatActivity() {

    abstract fun getLayoutId(): Int

    companion object {
        private const val TAG = "BaseActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.e(TAG, "onCreate")
        setContentView(R.layout.activity_base)

        val child = layoutInflater.inflate(getLayoutId(), bse_container, false)
        bse_container.addView(child)

        title = getString(R.string.app_name)
        setSupportActionBar(bse_toolbar)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    fun setTitle(titleStr: String) {
        title = titleStr
    }
}