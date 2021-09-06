package com.combyne.mytube.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.text.DateFormat
import java.util.Date

@Entity(tableName = "show")
@Parcelize
data class Show(
    // We use val to make our object immutable os that we always need a new object while updating data.
    // This will reduce state inconsistency in our data.
    val name: String,
    val releaseDate: Long?,
    val seasons: Double,
    val remoteID: String,
    val created: Long? = System.currentTimeMillis(),
    @PrimaryKey(autoGenerate = true) val id: Int = 0
) : Parcelable {

    // helper method to format date when required to display in UI
    val releaseDateFormatted: String?
        get() = if (releaseDate == null) null else DateFormat.getDateInstance()
            .format(Date(releaseDate))
}
