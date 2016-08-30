package com.kaws.agora.lib;

import android.content.Context;
import android.os.Handler;

import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;

/**
 * Created by yangcai on 16/8/29.
 */
public class RtcEngineManager extends IRtcEngineEventHandler {

    private RtcEngine rtcEngine;
    private AgoraView agoraView;
    private Handler mHander;
    private static RtcEngineManager instance;

    public static RtcEngineManager getInstance() {
        if (instance == null) {
            instance = new RtcEngineManager();
        }
        return instance;
    }

    private RtcEngineManager() {

    }

    public void setRtcEngine(Context context, String vendorKey, AgoraView agoraView) {
        this.agoraView = agoraView;
        if (rtcEngine == null) {
            mHander = new Handler();
            rtcEngine = RtcEngine.create(context, vendorKey, this);
            rtcEngine.setLogFile(context.getExternalFilesDir(null).toString() + "/agoraView.log");
            rtcEngine.enableVideo();
            rtcEngine.muteLocalVideoStream(false);
            rtcEngine.muteLocalAudioStream(false);
            rtcEngine.muteAllRemoteVideoStreams(false);
        }
    }

    public RtcEngine getRtcEngine() {
        if (rtcEngine == null) {
            throw new RuntimeException("RtcEngineManager must call setRtcEngine before");
        }
        return rtcEngine;
    }


    //显示房间内其他用户的视频
    @Override
    public void onFirstRemoteVideoDecoded(final int uid, final int width, final int height, final int elapsed) {
        if (agoraView != null) {
            mHander.post(new Runnable() {
                @Override
                public void run() {
                    agoraView.onFirstRemoteVideoDecoded(uid, width, height, elapsed);
                    agoraView.onUserChange();
                }
            });
        }
    }

    //用户进入
    @Override
    public void onUserJoined(final int uid, final int elapsed) {
        if (agoraView != null) {
            mHander.post(new Runnable() {
                @Override
                public void run() {
                    agoraView.onUserJoined(uid, elapsed);
                }
            });
        }
    }

    //用户退出
    @Override
    public void onUserOffline(final int uid, final int reason) {
        if (agoraView != null) {
            mHander.post(new Runnable() {
                @Override
                public void run() {
                    agoraView.onUserOffline(uid, reason);
                    agoraView.onUserChange();
                }
            });
        }
    }

    //监听其他用户是否关闭视频
    @Override
    public void onUserMuteVideo(final int uid, final boolean muted) {
        if (agoraView != null) {
            mHander.post(new Runnable() {
                @Override
                public void run() {
                    agoraView.onUserMuteVideo(uid, muted);
                }
            });
        }
    }

    //更新聊天数据
    @Override
    public void onRtcStats(final IRtcEngineEventHandler.RtcStats stats) {
        if (agoraView != null) {
            mHander.post(new Runnable() {
                @Override
                public void run() {
                    agoraView.onRtcStats(stats);
                }
            });
        }
    }


    @Override
    public void onLeaveChannel(final IRtcEngineEventHandler.RtcStats stats) {
        if (agoraView != null) {
            mHander.post(new Runnable() {
                @Override
                public void run() {
                    agoraView.onLeaveChannel(stats);
                }
            });
        }
    }


    @Override
    public void onError(final int err) {
        if (agoraView != null) {
            mHander.post(new Runnable() {
                @Override
                public void run() {
                    agoraView.onError(err);
                }
            });
        }
    }


    @Override
    public void onJoinChannelSuccess(final String channel, final int uid, final int elapsed) {
        if (agoraView != null) {
            mHander.post(new Runnable() {
                @Override
                public void run() {
                    agoraView.onJoinChannelSuccess(channel, uid, elapsed);
                }
            });
        }
    }
}
