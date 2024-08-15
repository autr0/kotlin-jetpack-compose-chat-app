package com.devautro.firebasechatapp.profile.data

import android.util.Log
import androidx.compose.runtime.snapshots.SnapshotStateMap
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
    private val database: FirebaseDatabase
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
                    Log.e("MyLog", "Failed to read value in getProfileData: $error")
                }

            }
        )

    }

}