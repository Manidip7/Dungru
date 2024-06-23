package com.aksar.dungru.utils

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMuxer
import android.net.Uri
import android.os.Environment
import java.io.File
import java.nio.ByteBuffer


object VideoCompressor {

    @SuppressLint("WrongConstant")
    @Throws(Exception::class)
    fun compressVideoAndGetUri(context: Context, inputUri: Uri): Uri {
        val outputPath = getOutputPath(context)
        val info = MediaCodec.BufferInfo()

        val extractor = MediaExtractor()
        extractor.setDataSource(context, inputUri, null)

        val muxer = MediaMuxer(outputPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)

        var trackIndex = -1
        for (i in 0 until extractor.trackCount) {
            val format = extractor.getTrackFormat(i)
            val mime = format.getString(MediaFormat.KEY_MIME)
            if (mime != null) {
                if (mime.startsWith("video/")) {
                    extractor.selectTrack(i)
                    trackIndex = muxer.addTrack(format)
                    muxer.start()
                    break
                }
            }
        }

        val buffer = ByteBuffer.allocate(1024 * 1024) // Adjust buffer size as needed

        while (true) {
            val sampleSize = extractor.readSampleData(buffer, 0)
            if (sampleSize < 0) {
                break
            }
            info.size = sampleSize
            info.presentationTimeUs = extractor.sampleTime
            info.flags = extractor.sampleFlags
            muxer.writeSampleData(trackIndex, buffer, info)
            extractor.advance()
        }

        muxer.stop()
        muxer.release()
        extractor.release()

        return Uri.fromFile(File(outputPath))
    }

    private fun getOutputPath(context: Context): String {
        val dir = File(context.getExternalFilesDir(Environment.DIRECTORY_MOVIES), "CompressedVideos")
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return File(dir, "compressed_video.mp4").absolutePath
    }
}