package com.kaws.agora.lib;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.kaws.agora.lib.widget.AgoraVideoView;

import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;

public class AgoraViewActivity extends Activity implements AgoraView {

    public final static String EXTRA_VENDOR_KEY = "EXTRA_VENDOR_KEY";
    public final static String EXTRA_CHANNEL_ID = "EXTRA_CHANNEL_ID";

    private AgoraVideoView agoraVideoView;
    private LinearLayout llBottomPanel;
    private LinearLayout llNoSound;
    private ImageView imgNoSound;
    private LinearLayout llHangUp;
    private LinearLayout llCanera;
    private ImageView imgNoSoundTip;
    private String vendorKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_agora_view);
        setupRtcEngine();
        initView();
        setupChannel();
        if (!NetworkConnectivityUtils.isConnectedToNetwork(this)) {
            onError(104);
        }
    }

    private void initView() {
        agoraVideoView = (AgoraVideoView) findViewById(R.id.avv);
        llBottomPanel = (LinearLayout) findViewById(R.id.ll_bottom_panel);
        llNoSound = (LinearLayout) findViewById(R.id.ll_no_sound);
        imgNoSound = (ImageView) findViewById(R.id.img_no_sound);
        llHangUp = (LinearLayout) findViewById(R.id.ll_hang_up);
        llCanera = (LinearLayout) findViewById(R.id.ll_canera);
        imgNoSoundTip = (ImageView) findViewById(R.id.img_no_sound_tip);
    }

    private void setupRtcEngine() {
        vendorKey = getIntent().getStringExtra(EXTRA_VENDOR_KEY);
        RtcEngineManager.getInstance().setRtcEngine(this, vendorKey, this);
    }

    void ensureLocalViewIsCreated() {
        SurfaceView localView = RtcEngineManager.getInstance().getRtcEngine().CreateRendererView(getApplicationContext());
        agoraVideoView.addView(localView);
        RtcEngineManager.getInstance().getRtcEngine().setupLocalVideo(new VideoCanvas(localView));
    }


    private void setupChannel() {
        ensureLocalViewIsCreated();
        String channelId = getIntent().getStringExtra(EXTRA_CHANNEL_ID);
        RtcEngineManager.getInstance().getRtcEngine().joinChannel(vendorKey, channelId, "", 0);
    }

    @Override
    public void onBackPressed() {

        // keep screen on - turned off
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onBackPressed();
    }

    @Override
    public synchronized void onFirstRemoteVideoDecoded(final int uid, int width, int height, int elapsed) {
        SurfaceView remoteUserView = (SurfaceView) agoraVideoView.findViewById(Math.abs(uid));
        if (remoteUserView == null) {
            SurfaceView remoteView = RtcEngine.CreateRendererView(getApplicationContext());
            remoteView.setId(Math.abs(uid));
            agoraVideoView.addView(remoteView);
            remoteView.setZOrderOnTop(true);
            remoteView.setZOrderMediaOverlay(true);
            remoteUserView = remoteView;
        }
        int successCode = RtcEngineManager.getInstance().getRtcEngine().setupRemoteVideo(new VideoCanvas(remoteUserView, VideoCanvas.RENDER_MODE_HIDDEN, uid));
        if (successCode < 0) {
            final SurfaceView finalRemoteUserView = remoteUserView;
            new android.os.Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    RtcEngineManager.getInstance().getRtcEngine().setupRemoteVideo(new VideoCanvas(finalRemoteUserView, VideoCanvas.RENDER_MODE_HIDDEN, uid));
                    finalRemoteUserView.invalidate();
                }
            }, 500);
        }
    }


    @Override
    public synchronized void onUserJoined(int uid, int elapsed) {

    }

    @Override
    public synchronized void onUserOffline(int uid, int reason) {

        if (isFinishing()) {
            return;
        }
        View userViewToRemove = agoraVideoView.findViewById(Math.abs(uid));
        if (userViewToRemove != null) {
            agoraVideoView.removeView(userViewToRemove);
        }
    }

    @Override
    public void onUserMuteVideo(int uid, boolean muted) {
        if (isFinishing()) {
            return;
        }
        View remoteView = agoraVideoView.findViewById(Math.abs(uid));
        if (remoteView != null) {
            remoteView.setVisibility(muted ? View.VISIBLE : View.GONE);
            remoteView.invalidate();
        }
    }

    @Override
    public synchronized void onRtcStats(IRtcEngineEventHandler.RtcStats stats) {

    }

    @Override
    public synchronized void onLeaveChannel(IRtcEngineEventHandler.RtcStats stats) {
        finish();
    }

    @Override
    public synchronized void onError(int err) {
        if (isFinishing()) {
            return;
        }
        Toast.makeText(this, "err code  " + err, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
        setUpView();
    }


    private void setUpView() {
        llNoSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imgNoSoundTip.getVisibility() == View.VISIBLE) {
                    imgNoSoundTip.setVisibility(View.GONE);
                    v.setSelected(false);
                } else {
                    imgNoSoundTip.setVisibility(View.VISIBLE);
                    v.setSelected(true);
                }
            }
        });
        llCanera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RtcEngineManager.getInstance().getRtcEngine().switchCamera();
            }
        });
        llHangUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        agoraVideoView.setOnClickListener(new View.OnClickListener() {
            boolean isPanelShow = true;//点击时 panel 是否显示
            private AnimatorListener listener = new AnimatorListener();

            @Override
            public void onClick(View v) {
                if (!listener.isFinshed) {
                    return;
                }
                if (isPanelShow) {
                    isPanelShow = false;
                    ObjectAnimator animatorDown = ObjectAnimator.ofFloat(llBottomPanel, "translationY", 0.0f, llBottomPanel.getHeight());
                    animatorDown.addListener(listener);
                    animatorDown.setDuration(300).start();
                } else {
                    ObjectAnimator animatorUp = ObjectAnimator.ofFloat(llBottomPanel, "translationY", llBottomPanel.getHeight(), 0.0f);
                    animatorUp.addListener(listener);
                    animatorUp.setDuration(300).start();
                    isPanelShow = true;
                }
            }
        });
    }

    @Override
    public void onUserChange() {
        int childCount = agoraVideoView.getChildCount();
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) agoraVideoView.getLayoutParams();
        if (childCount > 2) {
            llBottomPanel.setBackgroundResource(android.R.color.transparent);
            lp.addRule(RelativeLayout.ABOVE, llBottomPanel.getId());
        } else {
            llBottomPanel.setBackgroundResource(R.color.half_black);
            lp.addRule(RelativeLayout.ABOVE, 0);
        }
        agoraVideoView.setLayoutParams(lp);
    }


    @Override
    protected void onDestroy() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                RtcEngineManager.getInstance().getRtcEngine().leaveChannel();
            }
        }).run();
        super.onDestroy();
    }

    /**
     * 进入直播
     */
    public static void start(Context context, String vendorKey, String channelId) {
        Intent intent = new Intent(context, AgoraViewActivity.class);
        intent.putExtra(EXTRA_VENDOR_KEY, vendorKey);
        intent.putExtra(EXTRA_CHANNEL_ID, channelId);
        context.startActivity(intent);
    }
}
