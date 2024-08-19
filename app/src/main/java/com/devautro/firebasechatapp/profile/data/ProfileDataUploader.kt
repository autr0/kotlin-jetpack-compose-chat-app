package com.devautro.firebasechatapp.profile.data

import android.util.Log
import com.devautro.firebasechatapp.profile.data.model.ProfileData
import com.devautro.firebasechatapp.core.data.model.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import javax.inject.Inject

class ProfileDataUploader @Inject constructor(
    private val auth: FirebaseAuth,
    database: FirebaseDatabase
) {
    private val profileRef = database.getReference("profile")
    var currentUser = UserData(
        auth.currentUser?.uid,
        auth.currentUser?.displayName,
        auth.currentUser?.photoUrl.toString()
    )

    fun updateCurrentUser() {
        val user = auth.currentUser
        currentUser = UserData(
            user?.uid,
            user?.displayName,
            user?.photoUrl.toString()
        )
    }

    fun getProfileData(onProfileDataReceived: (Map<String, Int>) -> Unit) {
        profileRef.child(currentUser.userId!!).addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val profileData = mutableMapOf<String, Int>()
                    for (s in snapshot.children) {
                        val profile = s.getValue(ProfileData::class.java)
                        if (profile?.name != null && profile.size != null) {
                            profileData.putIfAbsent(profile.name, profile.size)
                        }

                    }
                    onProfileDataReceived(profileData)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("MyLog", "Failed to read value in getProfileData: $error")
                }

            }
        )

    }

}