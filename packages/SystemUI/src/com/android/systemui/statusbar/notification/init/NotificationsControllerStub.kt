/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.systemui.statusbar.notification.init

import android.service.notification.StatusBarNotification
import com.android.systemui.bubbles.Bubbles
import com.android.systemui.plugins.statusbar.NotificationSwipeActionHelper.SnoozeOption
import com.android.systemui.statusbar.NotificationListener
import com.android.systemui.statusbar.NotificationPresenter
import com.android.systemui.statusbar.notification.NotificationActivityStarter
import com.android.systemui.statusbar.notification.collection.inflation.NotificationRowBinderImpl
import com.android.systemui.statusbar.notification.stack.NotificationListContainer
import com.android.systemui.statusbar.phone.StatusBar
import java.io.FileDescriptor
import java.io.PrintWriter
import java.util.Optional
import javax.inject.Inject

/**
 * Implementation of [NotificationsController] that's used when notifications rendering is disabled.
 */
class NotificationsControllerStub @Inject constructor(
    private val notificationListener: NotificationListener
) : NotificationsController {

    override fun initialize(
        statusBar: StatusBar,
        bubblesOptional: Optional<Bubbles>,
        presenter: NotificationPresenter,
        listContainer: NotificationListContainer,
        notificationActivityStarter: NotificationActivityStarter,
        bindRowCallback: NotificationRowBinderImpl.BindRowCallback
    ) {
        // Always connect the listener even if notification-handling is disabled. Being a listener
        // grants special permissions and it's not clear if other things will break if we lose those
        notificationListener.registerAsSystemService()
    }

    override fun requestNotificationUpdate(reason: String) {
    }

    override fun resetUserExpandedStates() {
    }

    override fun setNotificationSnoozed(sbn: StatusBarNotification, snoozeOption: SnoozeOption) {
    }

    override fun setNotificationSnoozed(sbn: StatusBarNotification, hoursToSnooze: Int) {
    }

    override fun getActiveNotificationsCount(): Int {
        return 0
    }

    override fun dump(
        fd: FileDescriptor,
        pw: PrintWriter,
        args: Array<String>,
        dumpTruck: Boolean
    ) {
        pw.println()
        pw.println("Notification handling disabled")
        pw.println()
    }
}