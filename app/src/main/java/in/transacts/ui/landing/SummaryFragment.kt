package `in`.transacts.ui.landing

import `in`.transacts.R
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class SummaryFragment: Fragment() {

    companion object {
        private const val TAG = "SummaryFragment"

        fun newInstance(): SummaryFragment {

            val summaryFragment = SummaryFragment()
            return summaryFragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.e(TAG, "onCreate")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val rootView = inflater.inflate(R.layout.fragment_summary, container, false)
        return rootView
    }
}