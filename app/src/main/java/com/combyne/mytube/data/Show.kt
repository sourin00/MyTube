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
    val name: String,
    val releaseDate: Long?,
    val seasons: Double,
    val remoteID: String,
    val created: Long? = System.currentTimeMillis(),
    @PrimaryKey(autoGenerate = true) val id: Int = 0
) : Parcelable {
    val releaseDateFormatted: String?
        get() = if (releaseDate==null) null else DateFormat.getDateInstance().format(Date(releaseDate))
}
