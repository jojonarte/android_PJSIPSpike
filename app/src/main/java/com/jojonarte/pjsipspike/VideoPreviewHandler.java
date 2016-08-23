package com.jojonarte.pjsipspike;

import android.view.SurfaceHolder;

import org.pjsip.pjsua2.VideoPreviewOpParam;
import org.pjsip.pjsua2.VideoWindowHandle;

class VideoPreviewHandler implements SurfaceHolder.Callback
{
    public boolean videoPreviewActive = false;

    public void updateVideoPreview(SurfaceHolder holder)
    {
        if (MainActivity.currentCall != null &&
                MainActivity.currentCall.vidWin != null &&
                MainActivity.currentCall.vidPrev != null)
        {
            if (videoPreviewActive) {
                VideoWindowHandle vidWH = new VideoWindowHandle();
                vidWH.getHandle().setWindow(holder.getSurface());
                VideoPreviewOpParam vidPrevParam = new VideoPreviewOpParam();
                vidPrevParam.setWindow(vidWH);
                try {
                    MainActivity.currentCall.vidPrev.start(vidPrevParam);
                } catch (Exception e) {
                    System.out.println(e);
                }
            } else {
                try {
                    MainActivity.currentCall.vidPrev.stop();
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h)
    {
        updateVideoPreview(holder);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
        try {
            MainActivity.currentCall.vidPrev.stop();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
