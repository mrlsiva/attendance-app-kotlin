package com.example.calendar

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.slings.vasantham.R

// CustomAdapter.java

class oneCustomAdapter(private val context: Context, items: List<CustomItem>) :
    RecyclerView.Adapter<oneCustomAdapter.ViewHolder>() {
    private val items:List<CustomItem>

    init {
        this.items = items
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.one_adapter, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item: CustomItem = items[position]
        holder.boldTextView.setText(item.boldText)
        holder.normalTextView.setText(item.normalText)
        holder.roundedButton.setOnClickListener { v: View? -> }
        holder.textButton.setOnClickListener { v: View? -> }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var circularImageView: ImageView
        var boldTextView: TextView
        var normalTextView: TextView
        var roundedButton: Button
        var textButton: Button

        init {
            circularImageView = itemView.findViewById(R.id.circularImageView)
            boldTextView = itemView.findViewById(R.id.boldTextView)
            normalTextView = itemView.findViewById(R.id.normalTextView)
            roundedButton = itemView.findViewById(R.id.roundedButton)
            textButton = itemView.findViewById(R.id.textButton)
        }
    }
}
