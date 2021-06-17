@file:Suppress("DEPRECATION")

package com.skripsi.presensigps.adapter

import android.app.ProgressDialog
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.skripsi.presensigps.R
import com.skripsi.presensigps.model.DataResponse
import com.skripsi.presensigps.model.Result
import com.skripsi.presensigps.network.ApiClient
import com.skripsi.presensigps.utils.Constant
import com.skripsi.presensigps.utils.PreferencesHelper
import kotlinx.android.synthetic.main.item_presence.view.btnVerifikasi
import kotlinx.android.synthetic.main.item_presence.view.tvDate
import kotlinx.android.synthetic.main.item_presence.view.tvName
import kotlinx.android.synthetic.main.item_presence.view.tvName2
import kotlinx.android.synthetic.main.item_presence.view.tvTime
import kotlinx.android.synthetic.main.item_report.view.*
import kotlinx.android.synthetic.main.item_report.view.icDetails
import kotlinx.android.synthetic.main.item_report.view.icStatus
import kotlinx.android.synthetic.main.item_report.view.parentDetails
import kotlinx.android.synthetic.main.item_report.view.parentNameList
import kotlinx.android.synthetic.main.item_report.view.tvVerifikasi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReportAdapter(private val reportList: ArrayList<Result>) :
    RecyclerView.Adapter<ReportAdapter.ReportViewHolder>() {
    private lateinit var sharedPref: PreferencesHelper
    private var positions: String? = null
    private lateinit var progressDialog: ProgressDialog
    private val verification = "1"

    inner class ReportViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(dataResult: Result) {
            progressDialog = ProgressDialog(itemView.context)
            progressDialog.setTitle("Loading")
            progressDialog.setMessage("Memverifikasi...")
            progressDialog.setCancelable(false)

            sharedPref = PreferencesHelper(itemView.context)
            positions = sharedPref.getString(Constant.PREF_USER_POSITION)

            if (positions == "manager") {
                itemView.btnVerifikasi.visibility = View.INVISIBLE
            }

            itemView.tvName.text = dataResult.name
            itemView.tvName2.text = dataResult.name
            Glide.with(itemView)
                .load(dataResult.img)
                .into(itemView.imgReport)
            itemView.tvLocation.text = dataResult.location_name
            itemView.tvLatitude.text = dataResult.latitude
            itemView.tvLongitude.text = dataResult.longitude
            itemView.tvDate.text = dataResult.date
            itemView.tvTime.text = dataResult.time
            itemView.tvNotes.text = dataResult.notes

            if (dataResult.expendable) {
                itemView.parentDetails.visibility = View.VISIBLE
                itemView.icDetails.setImageResource(R.drawable.ic_up)

            } else {
                itemView.parentDetails.visibility = View.GONE
                itemView.icDetails.setImageResource(R.drawable.ic_down)
            }

            if (dataResult.status == "1") {
                itemView.icStatus.setImageResource(R.drawable.ic_success)
            } else {
                itemView.icStatus.setImageResource(R.drawable.ic_success_not_yet)
            }

            if (dataResult.expendable && dataResult.status == "0") {
                itemView.tvVerifikasi.text = itemView.context.getString(R.string.not_verifikasi)
                itemView.btnVerifikasi.text = itemView.context.getString(R.string.verifikasi)
                itemView.btnVerifikasi.setTextColor(Color.BLACK)
                itemView.tvVerifikasi.setTextColor(Color.BLACK)
            } else {
                itemView.tvVerifikasi.text = itemView.context.getString(R.string.telah_diverifikasi)
                itemView.btnVerifikasi.text =
                    itemView.context.getString(R.string.telah_diverifikasi)
                itemView.btnVerifikasi.setTextColor(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.dark_green
                    )
                )
                itemView.tvVerifikasi.setTextColor(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.dark_green
                    )
                )
            }

            itemView.btnVerifikasi.setOnClickListener {
                if (dataResult.status == "0") {
                    Toast.makeText(itemView.context, "memverifikasi", Toast.LENGTH_SHORT).show()
                    reportVerification(dataResult.id, itemView)
                }
            }

            itemView.parentNameList.setOnClickListener {
                dataResult.expendable = !dataResult.expendable
                notifyDataSetChanged()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_report, parent, false)
        return ReportViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        holder.bind(reportList[position])
    }

    override fun getItemCount(): Int = reportList.size

    private fun reportVerification(id: String, itemView: View) {
        progressDialog.show()

        ApiClient.instance.verificationReport(id, verification)
            .enqueue(object : Callback<DataResponse> {
                override fun onResponse(
                    call: Call<DataResponse>,
                    response: Response<DataResponse>
                ) {
                    val value = response.body()?.value
                    val message = response.body()?.message

                    if (value.equals("1")) {
                        Toast.makeText(itemView.context, message.toString(), Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        Toast.makeText(itemView.context, message.toString(), Toast.LENGTH_SHORT)
                            .show()
                    }
                    progressDialog.dismiss()
                }

                override fun onFailure(call: Call<DataResponse>, t: Throwable) {
                    progressDialog.dismiss()
                    Toast.makeText(itemView.context, t.message.toString(), Toast.LENGTH_SHORT)
                        .show()
                }
            })
    }
}