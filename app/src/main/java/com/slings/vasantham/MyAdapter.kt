package com.slings.vasantham

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.slings.vasantham.data.model.AttendanceEntry
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MyAdapter(private val dataList: List<AttendanceEntry>) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataList[position]
        if(position==0){
            holder.titleLayout.visibility = View.VISIBLE
        }else{
            holder.titleLayout.visibility = View.GONE
        }
        // Bind the data to the views in the ViewHolder here

        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
        val outputFormat = SimpleDateFormat("hh:mm:aa", Locale.US)
        val outputDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

        try {
            val startTime = inputFormat.parse(item.startTime)
            val formattedStartTime = startTime?.let { outputFormat.format(it) }
            try{
                val endTime = item.endTime?.let { inputFormat.parse(it) }
                val formattedEndTime = endTime?.let { outputFormat.format(it) }
                holder.punchOut.text = formattedEndTime
            }catch (e:Exception){
                holder.punchOut.text = item.endTime
            }
            holder.date.text = startTime?.let { outputDateFormat.format(it) }
            holder.punchin.text = formattedStartTime


            // Now you can use the formatted start and end times in your Android app
            // For example, you can set these values to TextViews or store them in variables.
        } catch (e: ParseException) {
            e.printStackTrace()
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val date: TextView = itemView.findViewById(R.id.date)
        val punchin: TextView = itemView.findViewById(R.id.punchin)
        val punchOut: TextView = itemView.findViewById(R.id.punchOut)
        val titleLayout: LinearLayout = itemView.findViewById(R.id.title_layout)
    }
}