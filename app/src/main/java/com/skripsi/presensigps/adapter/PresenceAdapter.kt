package com.skripsi.presensigps.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.skripsi.presensigps.R
import com.skripsi.presensigps.model.Result
import com.skripsi.presensigps.ui.activity.DetailPresenceActivity
import com.skripsi.presensigps.utils.PreferencesHelper
import kotlinx.android.synthetic.main.item_presence.view.*

class PresenceAdapter(
    private val presenceList: ArrayList<Result>
) :
    RecyclerView.Adapter<PresenceAdapter.PresenceViewHolder>() {
    private lateinit var sharedPref: PreferencesHelper

    inner class PresenceViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        @SuppressLint("UseCompatLoadingForDrawables")
        fun bind(dataResult: Result) {

            sharedPref = PreferencesHelper(itemView.context)

            if (dataResult.status == "1") {
                itemView.icStatus.setImageResource(R.drawable.ic_success)
            } else {
                itemView.icStatus.setImageResource(R.drawable.ic_success_not_yet)
            }

            itemView.tvName.text = dataResult.name

            itemView.cardPresence.setOnClickListener {
                itemView.context.startActivity(
                    Intent(itemView.context, DetailPresenceActivity::class.java)
                        .putExtra("id", dataResult.id)
                        .putExtra("name", dataResult.name)
                )
            }

        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PresenceAdapter.PresenceViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_presence, parent, false)
        return PresenceViewHolder(view)
    }

    override fun onBindViewHolder(holder: PresenceAdapter.PresenceViewHolder, position: Int) {
        holder.bind(presenceList[position])
    }

    override fun getItemCount(): Int = presenceList.size
}
