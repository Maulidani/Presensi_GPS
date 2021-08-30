package com.skripsi.presensigps.adapter

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.skripsi.presensigps.R
import com.skripsi.presensigps.model.DataResponse
import com.skripsi.presensigps.model.Result
import com.skripsi.presensigps.network.ApiClient
import com.skripsi.presensigps.ui.activity.MapsInfoActivity
import com.skripsi.presensigps.utils.Constant
import com.skripsi.presensigps.utils.PreferencesHelper
import kotlinx.android.synthetic.main.item_report.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReportAdapter(
    private val reportList: ArrayList<Result>,
    private val mListener: IUserRecycler
) :
    RecyclerView.Adapter<ReportAdapter.ReportViewHolder>() {
    private lateinit var sharedPref: PreferencesHelper
    private var positions: String? = null
    private val verification = "1"
    private val cancelVerification = "0"

    inner class ReportViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(dataResult: Result) {

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
            itemView.notes.setText(dataResult.notes)

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
                    Toast.makeText(itemView.context, "verifikasi...", Toast.LENGTH_SHORT)
                        .show()

                    reportVerification(dataResult.id, itemView, dataResult)
                }
            }

            itemView.parentNameList.setOnClickListener {
                dataResult.expendable = !dataResult.expendable
                notifyDataSetChanged()
            }

            itemView.parentNameList.setOnLongClickListener {
                if (positions == "admin") {
                    optionAlert(itemView, dataResult)
                }
                true
            }

            itemView.parentDetails.setOnClickListener {
                    ContextCompat.startActivity(
                        itemView.context,
                        Intent(itemView.context, MapsInfoActivity::class.java)
                            .putExtra("cek", true)
                            .putExtra("name", dataResult.name)
                            .putExtra("img", dataResult.img)
                            .putExtra("location", dataResult.location_name)
                            .putExtra("latitude", dataResult.latitude)
                            .putExtra("longitude", dataResult.longitude)
                            .putExtra("date", dataResult.date)
                            .putExtra("time", dataResult.time)
                            .putExtra("notes", dataResult.notes)
                            .putExtra("status", dataResult.status),
                        null
                    )
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

    private fun reportVerification(id: String, itemView: View, dataResult: Result) {

        ApiClient.instance.verification(id, verification, "report")
            .enqueue(object : Callback<DataResponse> {
                override fun onResponse(
                    call: Call<DataResponse>,
                    response: Response<DataResponse>
                ) {
                    val value = response.body()?.value
                    val message = response.body()?.message

                    if (value.equals("1")) {

                        mListener.refreshView(dataResult, true, "report")

                    } else {
                        Toast.makeText(itemView.context, message.toString(), Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                override fun onFailure(call: Call<DataResponse>, t: Throwable) {
                    Toast.makeText(itemView.context, t.message.toString(), Toast.LENGTH_SHORT)
                        .show()
                }
            })
    }

    private fun reportCancelVerification(id: String, itemView: View, dataResult: Result) {
        ApiClient.instance.cancelVerification(id, cancelVerification, "report")
            .enqueue(object : Callback<DataResponse> {
                override fun onResponse(
                    call: Call<DataResponse>,
                    response: Response<DataResponse>
                ) {
                    val value = response.body()?.value
                    val message = response.body()?.message

                    if (value.equals("1")) {

                        mListener.refreshView(dataResult, true, "report")

                    } else {
                        Toast.makeText(itemView.context, message.toString(), Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                override fun onFailure(call: Call<DataResponse>, t: Throwable) {
                    Toast.makeText(itemView.context, t.message.toString(), Toast.LENGTH_SHORT)
                        .show()
                }

            })
    }

    private fun optionAlert(itemView: View, dataResult: Result) {
        val options: Array<String>
        val builder: AlertDialog.Builder = AlertDialog.Builder(itemView.context)
        builder.setTitle("Aksi")

        if (positions == "admin") {
            if (dataResult.status == "1") {
                options = arrayOf("Lihat lokasi", "Batalkan verifikasi")
                builder.setItems(
                    options
                ) { _, which ->
                    when (which) {
                        0 -> {
                            ContextCompat.startActivity(
                                itemView.context,
                                Intent(itemView.context, MapsInfoActivity::class.java)
                                    .putExtra("cek", true)
                                    .putExtra("name", dataResult.name)
                                    .putExtra("img", dataResult.img)
                                    .putExtra("location", dataResult.location_name)
                                    .putExtra("latitude", dataResult.latitude)
                                    .putExtra("longitude", dataResult.longitude)
                                    .putExtra("date", dataResult.date)
                                    .putExtra("time", dataResult.time)
                                    .putExtra("notes", dataResult.notes)
                                    .putExtra("status", dataResult.status),
                                null
                            )
                        }
                        1 -> reportCancelVerification(dataResult.id, itemView, dataResult)
                    }
                }
            } else {
                options = arrayOf("Lihat lokasi")
                builder.setItems(
                    options
                ) { _, which ->
                    when (which) {
                        0 -> {
                            ContextCompat.startActivity(
                                itemView.context,
                                Intent(itemView.context, MapsInfoActivity::class.java)
                                    .putExtra("cek", true)
                                    .putExtra("name", dataResult.name)
                                    .putExtra("img", dataResult.img)
                                    .putExtra("location", dataResult.location_name)
                                    .putExtra("latitude", dataResult.latitude)
                                    .putExtra("longitude", dataResult.longitude)
                                    .putExtra("date", dataResult.date)
                                    .putExtra("time", dataResult.time)
                                    .putExtra("notes", dataResult.notes)
                                    .putExtra("status", dataResult.status),
                                null
                            )
                        }
                    }
                }
            }
        }


        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    interface IUserRecycler {
        fun refreshView(dataResult: Result, onUpdate: Boolean, type_: String)
    }
}