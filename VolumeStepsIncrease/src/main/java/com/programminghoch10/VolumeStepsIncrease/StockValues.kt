package com.programminghoch10.VolumeStepsIncrease

import android.media.AudioManager
import androidx.annotation.Keep

@Keep
object StockValues {
    
    // from com.android.server.media.AudioService
    
    /** Maximum volume index values for audio streams  */
    var MAX_STREAM_VOLUME: IntArray = intArrayOf(
        5,  // STREAM_VOICE_CALL
        7,  // STREAM_SYSTEM
        7,  // STREAM_RING
        15, // STREAM_MUSIC
        7,  // STREAM_ALARM
        7,  // STREAM_NOTIFICATION
        15, // STREAM_BLUETOOTH_SCO
        7,  // STREAM_SYSTEM_ENFORCED
        15, // STREAM_DTMF
        15, // STREAM_TTS
        15, // STREAM_ACCESSIBILITY
        15, // STREAM_ASSISTANT
    )
    
    /** Minimum volume index values for audio streams  */
    var MIN_STREAM_VOLUME: IntArray = intArrayOf(
        1,  // STREAM_VOICE_CALL
        0,  // STREAM_SYSTEM
        0,  // STREAM_RING
        0,  // STREAM_MUSIC
        1,  // STREAM_ALARM
        0,  // STREAM_NOTIFICATION
        0,  // STREAM_BLUETOOTH_SCO
        0,  // STREAM_SYSTEM_ENFORCED
        0,  // STREAM_DTMF
        0,  // STREAM_TTS
        1,  // STREAM_ACCESSIBILITY
        0,  // STREAM_ASSISTANT
    )
    
    // from android.media.AudioSystem
    
    const val STREAM_VOICE_CALL: Int = AudioManager.STREAM_VOICE_CALL
    
    /** @hide Used to identify the volume of audio streams for system sounds
     */
    const val STREAM_SYSTEM: Int = AudioManager.STREAM_SYSTEM
    
    /** @hide Used to identify the volume of audio streams for the phone ring and message alerts
     */
    const val STREAM_RING: Int = AudioManager.STREAM_RING
    
    /** @hide Used to identify the volume of audio streams for music playback
     */
    const val STREAM_MUSIC: Int = AudioManager.STREAM_MUSIC
    
    /** @hide Used to identify the volume of audio streams for alarms
     */
    const val STREAM_ALARM: Int = AudioManager.STREAM_ALARM
    
    /** @hide Used to identify the volume of audio streams for notifications
     */
    const val STREAM_NOTIFICATION: Int = AudioManager.STREAM_NOTIFICATION
    
    /** @hide
     * Used to identify the volume of audio streams for phone calls when connected on bluetooth
     */
    @Deprecated("use {@link #STREAM_VOICE_CALL} instead ")
    const val STREAM_BLUETOOTH_SCO: Int = 6
    
    /** @hide Used to identify the volume of audio streams for enforced system sounds in certain
     * countries (e.g camera in Japan)
     */
    const val STREAM_SYSTEM_ENFORCED: Int = 7
    
    /** @hide Used to identify the volume of audio streams for DTMF tones
     */
    const val STREAM_DTMF: Int = AudioManager.STREAM_DTMF
    
    /** @hide Used to identify the volume of audio streams exclusively transmitted through the
     * speaker (TTS) of the device
     */
    const val STREAM_TTS: Int = 9
    
    /** @hide Used to identify the volume of audio streams for accessibility prompts
     */
    const val STREAM_ACCESSIBILITY: Int = AudioManager.STREAM_ACCESSIBILITY
    
    /** @hide Used to identify the volume of audio streams for virtual assistant
     */
    const val STREAM_ASSISTANT: Int = 11
}
