package by.dashkevichpavel.osteopath.viewcontroller.customerlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import by.dashkevichpavel.osteopath.R
import by.dashkevichpavel.osteopath.persistence.entity.CustomerEntity

class CustomerItemAdapter(
    var customers: MutableList<CustomerEntity>,
    private var customerClickListener: CustomerClickListener
    ): RecyclerView.Adapter<CustomerItemViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerItemViewHolder =
        CustomerItemViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.listitem_customer, parent, false)
        )

    override fun onBindViewHolder(holder: CustomerItemViewHolder, position: Int) {
        holder.bind(customers[position], customerClickListener)
    }

    override fun getItemCount(): Int = customers.size
}