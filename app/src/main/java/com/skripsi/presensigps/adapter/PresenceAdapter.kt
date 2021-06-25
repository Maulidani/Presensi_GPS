package com.skripsi.presensigps.adapter

import android.annotation.SuppressLint
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
import com.skripsi.presensigps.utils.Constant
import com.skripsi.presensigps.utils.PreferencesHelper
import kotlinx.android.synthetic.main.item_presence.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PresenceAdapter(
    private val presenceList: ArrayList<Result>,
    private val mListener: IUserRecycler
) :
    RecyclerView.Adapter<PresenceAdapter.PresenceViewHolder>() {
    private lateinit var sharedPref: PreferencesHelper
    private var positions: String? = null
    private val verification = "1"

    inner class PresenceViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        @SuppressLint("UseCompatLoadingForDrawables")
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
                .into(itemView.imgPresence)
            itemView.tvOffice.text = dataResult.office
            itemView.tvDate.text = dataResult.date
            itemView.tvTime.text = dataResult.time

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

                    presenceVerification(dataResult.id, itemView, dataResult)
                }
            }

            itemView.parentNameList.setOnClickListener {
                dataResult.expendable = !dataResult.expendable
                notifyDataSetChanged()
            }

            itemView.cardPresence.setOnLongClickListener {
                if (dataResult.status == "1") {
                    optionAlert(itemView, dataResult.id, dataResult)
                } else {
                    deleteAlert(itemView, dataResult.id, dataResult)
                }
                true
            }
            itemView.parentNameList.setOnLongClickListener {
                if (dataResult.status == "1") {
                    optionAlert(itemView, dataResult.id, dataResult)
                } else {
                    deleteAlert(itemView, dataResult.id, dataResult)
                }
                true
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

    private fun presenceVerification(id: String, itemView: View, dataResult: Result) {
        ApiClient.instance.verificationPresence(id, verification)
            .enqueue(object : Callback<DataResponse> {
                override fun onResponse(
                    call: Call<DataResponse>,
                    response: Response<DataResponse>
                ) {
                    val value = response.body()?.value
                    val message = response.body()?.message

                    if (value.equals("1")) {

                        mListener.refreshView(dataResult, true, "presence")

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

    private fun delete(itemView: View, id: String, dataResult: Result) {
        val type = "presence"
        ApiClient.instance.delete(id, type).enqueue(object : Callback<DataResponse> {
            override fun onResponse(call: Call<DataResponse>, response: Response<DataResponse>) {
                val value = response.body()?.value

                if (value.equals("1")) {

                    mListener.refreshView(dataResult, true, type)
                }

            }

            override fun onFailure(call: Call<DataResponse>, t: Throwable) {

                notifyDataSetChanged()
                Toast.makeText(itemView.context, t.message.toString(), Toast.LENGTH_SHORT)
                    .show()
            }

        })
    }

    private fun optionAlert(itemView: View, id: String, dataResult: Result) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(itemView.context)
        builder.setTitle("Aksi")
        val options = arrayOf("Batalkan verifikasi ?", "Hapus presensi")
        builder.setItems(
            options
        ) { _, which ->
            when (which) {
                0 -> Toast.makeText(itemView.context, "Batalkan verifikasi", Toast.LENGTH_SHORT)
                    .show()
                1 -> deleteAlert(itemView, id, dataResult)
            }
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun deleteAlert(itemView: View, id: String, dataResult: Result) {
        val builder = AlertDialog.Builder(itemView.context)
        builder.setTitle("Hapus")
        builder.setMessage("Hapus presensi ?")

        builder.setPositiveButton("Ya") { _, _ ->
            delete(itemView, id, dataResult)
        }

        builder.setNegativeButton("Tidak") { _, _ ->
            // cancel
        }
        builder.show()
    }

    interface IUserRecycler {
        fun refreshView(dataResult: Result, onUpdate: Boolean, type_: String)
    }
}