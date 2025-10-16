package com.jhf.smartcampusmanagementsystem

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.jhf.smartcampusmanagementsystem.Adapter.AdminUserAdapter
import com.jhf.smartcampusmanagementsystem.Data.UserModel

class Admin_Users_Fragment : Fragment() {

    private lateinit var rvUsers: RecyclerView
    private lateinit var etSearch: EditText
    private val userList = mutableListOf<UserModel>()
    private lateinit var adapter: AdminUserAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_admin__users_, container, false)

        rvUsers = view.findViewById(R.id.recyclerUsers)
        etSearch = view.findViewById(R.id.etSearch)

        adapter = AdminUserAdapter(requireContext(), userList) // ✅ context পাঠানো হলো
        rvUsers.layoutManager = LinearLayoutManager(requireContext())
        rvUsers.adapter = adapter

        loadAllUsers()

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterUsers(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        return view
    }

    private fun loadAllUsers() {
        val dbRef = FirebaseDatabase.getInstance().getReference("users")
        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                if (!snapshot.exists()) {
                    Toast.makeText(requireContext(), "No users found", Toast.LENGTH_SHORT).show()
                    return
                }

                for(userSnap in snapshot.children){
                    val user = userSnap.getValue(UserModel::class.java)
                    user?.let { userList.add(it) }
                }

                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Failed to load users", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun filterUsers(query: String){
        val filtered = userList.filter {
            it.name?.contains(query,true) == true ||
                    it.user_id?.contains(query,true) == true
        }
        adapter.updateList(filtered)
    }
}
