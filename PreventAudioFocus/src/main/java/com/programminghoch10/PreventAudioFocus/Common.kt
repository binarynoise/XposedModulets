package com.programminghoch10.PreventAudioFocus

import android.media.AudioManager

val SHARED_PREFERENCES_NAME = "audiofocus"

val ENTRIES = AudioManager::class.java.declaredFields.filter { it.name.startsWith("AUDIOFOCUS_REQUEST") }.associate { it.name to it.getInt(null) }

val ENTRIES_DEFAULT = AudioManager.AUDIOFOCUS_REQUEST_GRANTED
