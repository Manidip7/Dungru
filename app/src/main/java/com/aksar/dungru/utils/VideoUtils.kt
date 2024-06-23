package com.aksar.dungru.utils

import android.annotation.SuppressLint
import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMuxer
import java.nio.ByteBuffer

object VideoUtils {

    fun addTrackToMuxer(extractor: MediaExtractor, muxer: MediaMuxer): Int {
        val format = selectTrack(extractor)
        return muxer.addTrack(format)
    }

    fun addAudioTrackToMuxer(extractor: MediaExtractor, muxer: MediaMuxer): Int {
        val format = selectAudioTrack(extractor)
        return muxer.addTrack(format)
    }

    @SuppressLint("WrongConstant")
    fun copyTrack(extractor: MediaExtractor, muxer: MediaMuxer, trackIndex: Int, startTime: Long, endTime: Long): Int {
        val info = MediaCodec.BufferInfo()
        extractor.selectTrack(trackIndex)
        muxer.start()

        val inputBuffer = ByteBuffer.allocate(1024 * 1024) // Adjust buffer size as needed

        var offset = 0L
        while (true) {
            info.offset = offset.toInt()
            info.size = extractor.readSampleData(inputBuffer, offset.toInt())

            if (info.size < 0) {
                break
            }

            info.presentationTimeUs = extractor.sampleTime
            offset = extractor.sampleTime

            if (info.presentationTimeUs > endTime) {
                break
            }

            if (info.presentationTimeUs in startTime..endTime) {
                info.flags = extractor.sampleFlags
                muxer.writeSampleData(trackIndex, inputBuffer, info)
            }

            extractor.advance()
        }

        return inputBuffer.limit()
    }

    private fun selectTrack(extractor: MediaExtractor): MediaFormat {
        for (i in 0 until extractor.trackCount) {
            val format = extractor.getTrackFormat(i)
            val mime = format.getString(MediaFormat.KEY_MIME)
            if (mime?.startsWith("video/") == true) {
                extractor.selectTrack(i)
                return format
            }
        }
        throw IllegalArgumentException("No video track found")
    }

    private fun selectAudioTrack(extractor: MediaExtractor): MediaFormat {
        for (i in 0 until extractor.trackCount) {
            val format = extractor.getTrackFormat(i)
            val mime = format.getString(MediaFormat.KEY_MIME)
            if (mime?.startsWith("audio/") == true) {
                extractor.selectTrack(i)
                return format
            }
        }
        throw IllegalArgumentException("No audio track found")
    }
}