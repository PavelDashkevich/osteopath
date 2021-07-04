package by.dashkevichpavel.osteopath.viewcontroller.customerlist

import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import by.dashkevichpavel.osteopath.R
import by.dashkevichpavel.osteopath.model.CustomerStatus
import by.dashkevichpavel.osteopath.persistence.entity.CustomerEntity

class CustomerItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    private val tvCustomerName: TextView = itemView.findViewById(R.id.tv_customer_name)
    private val ivColorLabel: AppCompatImageView = itemView.findViewById(R.id.iv_color_label)

    fun bind(customer: CustomerEntity, customerClickListener: CustomerClickListener) {
        tvCustomerName.text = customer.name
        tvCustomerName.setOnClickListener {
            customerClickListener.onCustomerClick(customer.id)
        }

        ivColorLabel.setColorFilter(
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