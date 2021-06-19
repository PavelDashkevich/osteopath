package by.dashkevichpavel.osteopath

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import by.dashkevichpavel.osteopath.persistence.entity.CustomerEntity

class CustomerAdapter(
    var customers: MutableList<CustomerEntity>
    ): RecyclerView.Adapter<CustomerViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerViewHolder =
        CustomerViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.listitem_customer, parent, false)
        )

    override fun onBindViewHolder(holder: CustomerViewHolder, position: Int) {
        holder.bind(customers[position])
    }

    override fun getItemCount(): Int = customers.size
}