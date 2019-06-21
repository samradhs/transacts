package `in`.transacts.ui.transactions

import `in`.transacts.R
import `in`.transacts.databinding.ItemTransactionBinding
import `in`.transacts.ui.TransactionSer
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView

class TransactionAdapter (private val items: List<TransactionSer>): RecyclerView.Adapter<TransactionVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionVH {

        val inflater = LayoutInflater.from(parent.context)
        val binding= DataBindingUtil.inflate<ItemTransactionBinding>(inflater,
            R.layout.item_transaction, parent, false)
        return TransactionVH(binding)
    }

    override fun onBindViewHolder(holder: TransactionVH, position: Int) {

        val transaction = items[position]
        holder.binding.transaction = transaction
    }

    override fun getItemCount(): Int {
        return items.size
    }
}

class TransactionVH(val binding: ItemTransactionBinding): RecyclerView.ViewHolder(binding.root)