package by.dashkevichpavel.osteopath.features.session

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import by.dashkevichpavel.osteopath.R
import by.dashkevichpavel.osteopath.helpers.recyclerviewutils.DefaultDiffUtil
import by.dashkevichpavel.osteopath.model.Disfunction

class DisfunctionInSessionAdapter(
    private val deleteClickListener: DisfunctionDeleteClickListener
) : RecyclerView.Adapter<DisfunctionInSessionViewHolder>() {
    private val disfunctionsInSession: MutableList<Disfunction> = mutableListOf()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DisfunctionInSessionViewHolder = DisfunctionInSessionViewHolder(
        LayoutInflater
            .from(parent.context)
            .inflate(R.layout.listitem_session_disfunction, parent, false)
    )

    override fun onBindViewHolder(holder: DisfunctionInSessionViewHolder, position: Int) {
        holder.bind(disfunctionsInSession[position], deleteClickListener)
    }

    override fun getItemCount(): Int = disfunctionsInSession.size

    fun setItems(newItems: MutableList<Disfunction>) {
        val result = DiffUtil.calculateDiff(DefaultDiffUtil(disfunctionsInSession, newItems))
        disfunctionsInSession.clear()
        disfunctionsInSession.addAll(newItems)
        result.dispatchUpdatesTo(this)
    }
}