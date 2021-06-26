package com.skripsi.presensigps.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.skripsi.presensigps.R
import com.skripsi.presensigps.model.DataResponse
import com.skripsi.presensigps.model.Result
import com.skripsi.presensigps.network.ApiClient
import com.skripsi.presensigps.ui.activity.RegisterActivity
import kotlinx.android.synthetic.main.item_user.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class UserAdapter(
    private val userList: ArrayList<Result>,
    private val mListener: IUserRecycler
) :
    RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(dataResult: Result) {

            itemView.tvName.text = dataResult.name
            itemView.tvName2.text = dataResult.name
            Glide.with(itemView)
                .load(dataResult.img)
                .into(itemView.imgUser)
            itemView.tvPosition.text = dataResult.position
            itemView.tvEmail.text = dataResult.email

            if (dataResult.expendable) {
                itemView.parentDetails.visibility = View.VISIBLE
                itemView.icDetails.setImageResource(R.drawable.ic_up)

            } else {
                itemView.parentDetails.visibility = View.GONE
                itemView.icDetails.setImageResource(R.drawable.ic_down)
            }

            itemView.parentNameList.setOnClickListener {
                dataResult.expendable = !dataResult.expendable
                notifyDataSetChanged()
            }

            itemView.cardUser.setOnLongClickListener {
                optionAlert(itemView, dataResult.id, dataResult)
                true
            }

            itemView.parentNameList.setOnLongClickListener {
                optionAlert(itemView, dataResult.id, dataResult)
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(userList[position])
    }

    override fun getItemCount(): Int = userList.size

    private fun delete(itemView: View, id: String, dataResult: Result) {
        val type = "user"
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
        val options = arrayOf("Edit akun", "Hapus akun")
        builder.setItems(
            options
        ) { _, which ->
            when (which) {
                0 -> {
                    startActivity(
                        itemView.context, Intent(itemView.context, RegisterActivity::class.java)
                            .putExtra("cek", true)
                            .putExtra("id", dataResult.id)
                            .putExtra("name", dataResult.name)
                            .putExtra("position", dataResult.position)
                            .putExtra("email", dataResult.email)
                            .putExtra("password", dataResult.password)
                    ,null)
                }
                1 -> deleteAlert(itemView, id, dataResult)
            }
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun deleteAlert(itemView: View, id: String, dataResult: Result) {
        val builder = AlertDialog.Builder(itemView.context)
        builder.setTitle("Hapus")
        builder.setMessage("Hapus akun ?")

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