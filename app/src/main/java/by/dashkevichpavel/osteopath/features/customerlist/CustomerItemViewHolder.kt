package by.dashkevichpavel.osteopath.features.customerlist

import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import by.dashkevichpavel.osteopath.R
import by.dashkevichpavel.osteopath.databinding.ListitemCustomerBinding
import by.dashkevichpavel.osteopath.model.Customer
import by.dashkevichpavel.osteopath.model.CustomerStatus

class CustomerItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    private val binding = ListitemCustomerBinding.bind(itemView)

    fun bind(customer: Customer, customerClickListener: CustomerClickListener) {
        binding.tvCustomerName.text = customer.name
        binding.tvCustomerName.setOnClickListener {
            customerClickListener.onCustomerClick(customer.id)
        }

        binding.ivColorLabel.setColorFilter(
            ContextCompat.getColor(
                itemView.context,
                when(customer.customerStatusId) {
                    CustomerStatus.WORK.id -> R.color.customer_status_work
                    CustomerStatus.WORK_DONE.id -> R.color.customer_status_work_done
                    CustomerStatus.NO_HELP.id -> R.color.customer_status_no_help
                    else -> R.color.customer_status_work
                }
            )
        )
    }
}