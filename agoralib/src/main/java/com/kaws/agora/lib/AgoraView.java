package com.kaws.agora.lib;

import io.agora.rtc.IRtcEngineEventHandler;

/**
 * Created by yangcai on 16/8/29.
 */
public interface AgoraView {


    //显示房间内其他用户的视频

    void onFirstRemoteVideoDecoded(int uid, int width, int height, int elapsed);


    //用户进入@Override
    void onUserJoined(int uid, int elapsed);


    //用户退出

    void onUserOffline(int uid, int reason);


    //监听其他用户是否关闭视频

    void onUserMuteVideo(int uid, boolean muted);


    //更新聊天数据

    void onRtcStats(IRtcEngineEventHandler.RtcStats stats);


    void onLeaveChannel(IRtcEngineEventHandler.RtcStats stats);


    void onError(int err);
    //加入房间成功
    void onJoinChannelSuccess(String channel, int uid, int elapsed);
    void onUserChange();
}
