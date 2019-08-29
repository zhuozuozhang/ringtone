package com.hrtxn.ringtone.common.utils;

import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.mp3.MP3AudioHeader;
import org.jaudiotagger.audio.mp3.MP3File;

import java.io.File;

/**
 * @Author zcy
 * @Date 2019-8-27 16:57
 * @Description MP3文件工具类
 */
public class Mp3Util {

    /**
     * @Author zcy
     * @Date 2019-8-27 16:57
     * @Description 获取mp3文件长度 返回长度为秒，非mp3文件，返回-1
     */
    public static int getMp3TrackLength(File mp3File) {
        try {
            MP3File f = (MP3File) AudioFileIO.read(mp3File);
            MP3AudioHeader audioHeader = (MP3AudioHeader) f.getAudioHeader();
            return audioHeader.getTrackLength();
        } catch (Exception e) {
            return -1;
        }
    }


}
