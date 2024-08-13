package com.devautro.firebasechatapp.profile.model

import android.util.Log
import androidx.compose.runtime.snapshots.SnapshotStateMap
import com.devautro.firebasechatapp.sign_in.model.UserData
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ProfileDataUploader {
    private val database = Firebase.database
    private val profileRef = database.getReference("profile")
    private val auth = Firebase.auth
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


    suspend fun getProfileData(profileData: SnapshotStateMap<String, Int>) {
        profileRef.child(currentUser.userId!!).addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    profileData.clear()
                    for (s in snapshot.children) {
                        val profile = s.getValue(ProfileData::class.java)
                        if (profile?.name != null && profile.size != null) {
                            profileData.putIfAbsent(profile.name, profile.size)
                        }

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w("MyLog", "Failed to read value in getProfileData: $error")
                }

            }
        )

    }

}