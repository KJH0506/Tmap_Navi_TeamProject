package com.hansung.sherpa.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import com.hansung.sherpa.StaticValue
import com.hansung.sherpa.fcm.MessageViewModel
import com.hansung.sherpa.ui.preference.caregiver.CaregiverPermissionDialog

@Composable
fun PermissionAlam(messageViewModel: MessageViewModel) {
    val showDialog by messageViewModel.showDialog.observeAsState(false)
    val title by messageViewModel.title.observeAsState("")
    val body by messageViewModel.body.observeAsState("")

    if(showDialog) {
        val caretakerId = body.split("/")[0]
        val caretakerEmail = body.split("/")[1]
        CaregiverPermissionDialog(
            title,
            listOf(body),
            "승인",
            "취소",
            onConfirmation = { messageViewModel.onDialogDismiss() },
            onDismissRequest = { messageViewModel.onDialogDismiss()},
            caregiverId = StaticValue.userInfo.userId!!,
            caretakerId = caretakerId.toInt(),
            caregiverEmail = caretakerEmail
        )
    }
}