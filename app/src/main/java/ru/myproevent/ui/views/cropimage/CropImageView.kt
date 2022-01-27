package ru.myproevent.ui.views.cropimage

import android.net.Uri
import androidx.activity.result.ActivityResultCaller

interface CropImageView : ActivityResultCaller {
    var newPictureUri: Uri?
}
