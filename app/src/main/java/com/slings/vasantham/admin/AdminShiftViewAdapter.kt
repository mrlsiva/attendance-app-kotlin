package com.slings.vasantham.admin

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.slings.vasantham.CustomItem
import com.slings.vasantham.R

// CustomAdapter.java

class AdminShiftViewAdapter(private val context: Context, items: List<CustomItem>) :
    RecyclerView.Adapter<AdminShiftViewAdapter.ViewHolder>() {
    private val items:List<CustomItem>

    init {
        this.items = items
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.admin_shift_adapter, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item: CustomItem = items[position]
        holder.boldTextView.setText(item.boldText)
        holder.normalTextView.setText(item.normalText)
        holder.roundedButton.setOnClickListener { v: View? -> }
        if(position == 1){
            holder.circularImageView.setImageDrawable(context.getDrawable(R.drawable.admin_shit_time_gre))
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var circularImageView: ImageView
        var boldTextView: TextView
        var normalTextView: TextView
        var roundedButton: Button

        init {
            circularImageView = itemView.findViewById(R.id.circularImageView)
            boldTextView = itemView.findViewById(R.id.boldTextView)
            normalTextView = itemView.findViewById(R.id.normalTextView)
            roundedButton = itemView.findViewById(R.id.roundedButton)
        }
    }
}
