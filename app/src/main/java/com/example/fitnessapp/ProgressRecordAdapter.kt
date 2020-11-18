package com.example.fitnessapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.progress_item.view.*

class ProgressRecordAdapter internal constructor(
    context : Context
) : RecyclerView.Adapter<ProgressRecordAdapter.ProgressRecordViewHolder>() {

    private val inflater : LayoutInflater = LayoutInflater.from(context)
    private var progressRecords = emptyList<ProgressRecord>()

    inner class ProgressRecordViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val progressRecordView : LinearLayout = itemView.findViewById(R.id.progressItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProgressRecordViewHolder {
        val itemView = inflater.inflate(R.layout.progress_item, parent, false)
        return ProgressRecordViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ProgressRecordViewHolder, position: Int) {
        val current = progressRecords[position]

        holder.progressRecordView.textViewProgressDate.text = current.date
        holder.progressRecordView.textViewProgressWeight.text = current.weight.toString()

        holder.progressRecordView.setOnClickListener{
            //TODO: Set the code to update a record
        }


    }

    internal fun setProgressRecords(progressRecordsCollection : List<ProgressRecord>){
        this.progressRecords = progressRecordsCollection
        notifyDataSetChanged()
    }

    override fun getItemCount() = progressRecords.size
}