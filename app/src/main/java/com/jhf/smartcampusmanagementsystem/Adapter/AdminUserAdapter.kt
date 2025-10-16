package com.jhf.smartcampusmanagementsystem.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jhf.smartcampusmanagementsystem.Data.UserModel
import com.jhf.smartcampusmanagementsystem.R

class AdminUserAdapter(
    private val context: Context,
    private var userList: List<UserModel>
) : RecyclerView.Adapter<AdminUserAdapter.UserViewHolder>() {

    // ViewHolder class
    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgProfile: ImageView = itemView.findViewById(R.id.imgProfile)
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val tvUserId: TextView = itemView.findViewById(R.id.tvUserId)
        val tvRole: TextView = itemView.findViewById(R.id.tvRole)
        val tvEmail: TextView = itemView.findViewById(R.id.tvEmail)
        val tvMobile: TextView = itemView.findViewById(R.id.tvMobile)
        val tvExtra: TextView = itemView.findViewById(R.id.tvExtra)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view)
    }

    override fun getItemCount(): Int = userList.size

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]

        holder.tvName.text = user.name
        holder.tvUserId.text = "ID: ${user.user_id}"
        holder.tvRole.text = "Role: ${user.role?.replaceFirstChar { it.uppercase() }}"
        holder.tvEmail.text = "Email: ${user.email}"
        holder.tvMobile.text = "Mobile: ${user.mobile}"

        // Role অনুযায়ী extra info
        holder.tvExtra.text = when (user.role?.lowercase()) {
            "student" -> "Class: ${user.className ?: "N/A"}"
            "teacher" -> "Dept: ${user.department ?: "N/A"}"
            else -> ""
        }

        // Glide দিয়ে profile picture load করা
        Glide.with(context)
            .load(user.profileImage)
            .placeholder(R.drawable.blank_profile)
            .error(R.drawable.blank_profile)
            .centerCrop()
            .into(holder.imgProfile)
    }

    // Search filter update
    fun updateList(newList: List<UserModel>) {
        userList = newList
        notifyDataSetChanged()
    }
}
