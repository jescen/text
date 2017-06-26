package com.shbst.bst.text;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.webkit.WebView;

import java.io.IOException;

/**
 * Created by hegang on 2017-02-21.
 */
public class ScreenSurfaceView extends SurfaceView implements SurfaceHolder.Callback, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnInfoListener, MediaPlayer.OnPreparedListener {
    private static final String TAG = "ScreenSurfaceView";
    private Context mContext;
    private SurfaceHolder holder;
    private MediaPlayer player;
    private WebView webView;


    private boolean mIsThreadRunning = true; // 线程运行开关
    private boolean mIsDestroy = false;// 是否已经销毁

    private String[] mBitmapResourceIds;// 用于播放动画的图片资源数组
    private Canvas mCanvas;
    private Bitmap mBitmap;// 显示的图片

    private int mCurrentIndext;// 当前动画播放的位置
    private int mGapTime = 1000;// 每帧动画持续存在的时间

//    private OnFrameFinishedListener mOnFrameFinishedListener;// 动画监听事件


    public ScreenSurfaceView(Context context) {
        super(context);
        mContext = context;
    }

    public ScreenSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public ScreenSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    //加载图片
    private Drawable showImage(String imaePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inPurgeable = true;
        options.inSampleSize = 2;//图片宽高都为原来的二分之一，即图片为原来的四分之一
        options.inInputShareable = true;
        Bitmap bm = BitmapFactory.decodeFile(imaePath, options);
        BitmapDrawable drawable = new BitmapDrawable(bm);
        return drawable;
    }

    /**
     *  设置显示为图片
     * @param path 图片路径
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void setPicturesView(String path) {
        try {
            if(player.isPlaying()){
                //如果播放就先暂停 ，重置播放器
                player.stop();
                player.reset();
            }
        }catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        Drawable drawable = showImage(path);
        setBackground(drawable);

    }


    /**
     * 设置显示为视频view
     */
    public void setHolderView() {
        holder = getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        //下面开始实例化MediaPlayer对象
        player = new MediaPlayer();
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
        player.setOnInfoListener(this);
        player.setOnPreparedListener(this);

    }

    /**
     * 设置视频文件路径
     * @param dataPath 视频文件路径
     */
    public void setVideoPath(String dataPath){
        try {
            // 判断视频是否在播放
            if(player.isPlaying()){
                //如果播放就先暂停 ，重置播放器
                player.stop();
                player.reset();
            }
//            player = MediaPlayer.create(mContext, Uri.parse(dataPath));
            //设置资源路径
            setBackground(null);
            player.setDataSource(dataPath);
            // 准备
            player.prepare();
            //开始播放
            player.start();

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 结束正在播放的视频
     * 隐藏显示的图片
     */
    public void stopSurfacView() {
        // 判断视频是否在播放
        if(player.isPlaying()){
            //如果播放就先暂停 ，重置播放器
            player.stop();
            player.reset();
        }
        //设置资源路径
        setBackground(null);
    }


    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        //在这里我们指定MediaPlayer在当前的Surface中进行播放
        player.setDisplay(holder);
        //在指定了MediaPlayer播放的容器后，我们就可以使用prepare或者prepareAsync来准备播放了
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {

        Log.v("Play Over:::", "onComletion called");

        player.start();

    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        // 当一些特定信息出现或者警告时触发
        switch (i) {
            case MediaPlayer.MEDIA_INFO_BAD_INTERLEAVING:
                break;
            case MediaPlayer.MEDIA_INFO_METADATA_UPDATE:
                break;
            case MediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING:
                break;
            case MediaPlayer.MEDIA_INFO_NOT_SEEKABLE:
                break;
        }
        return false;
    }

    @Override
    public boolean onInfo(MediaPlayer mediaPlayer, int i, int i1) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        player.start();
    }

//    /**
//     * 开始动画
//     */
//    public void startPlayImage()
//    {
//        if (!mIsDestroy)
//        {
//            mCurrentIndext = 0;
//            mIsThreadRunning = true;
//            new Thread(this).start();
//        } else
//        {
//            // 如果SurfaceHolder已经销毁抛出该异常
//            try
//            {
//                throw new Exception("IllegalArgumentException:Are you sure the SurfaceHolder is not destroyed");
//            } catch (Exception e)
//            {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    /**
//     * 设置动画播放素材
//     * @param bitmapResourceIds	图片资源id
//     */
//    public void setBitmapResoursID(String[] bitmapResourceIds)
//    {
//        this.mBitmapResourceIds = bitmapResourceIds;
//    }
//
//    /**
//     * 设置每帧时间
//     */
//    public void setGapTime(int gapTime)
//    {
//        this.mGapTime = gapTime;
//    }
//
//    /**
//     * 结束动画
//     */
//    public void stop()
//    {
//        mIsThreadRunning = false;
//    }
//
//    /**
//     * 继续动画
//     */
//    public void reStart()
//    {
//        mIsThreadRunning = false;
//    }
//
//    /**
//     * 设置动画监听器
//     */
//    public void setOnFrameFinisedListener(OnFrameFinishedListener onFrameFinishedListener)
//    {
//        this.mOnFrameFinishedListener = onFrameFinishedListener;
//    }
//
//    /**
//     * 制图方法
//     */
//    private void drawView()
//    {
//        // 无资源文件退出
//        if (mBitmapResourceIds == null)
//        {
//            Log.e("frameview", "the bitmapsrcIDs is null");
//
//            mIsThreadRunning = false;
//
//            return;
//        }
//
//        // 锁定画布
//        mCanvas = holder.lockCanvas();
//        try
//        {
//            if (holder != null && mCanvas != null)
//            {
//                mCanvas.drawColor(Color.WHITE);
//                // 如果图片过大可以再次对图片进行二次采样缩放处理
//                BitmapFactory.Options options = new BitmapFactory.Options();
//                options.inPreferredConfig = Bitmap.Config.RGB_565;
//                options.inPurgeable = true;
//                options.inSampleSize = 2;//图片宽高都为原来的二分之一，即图片为原来的四分之一
//                options.inInputShareable = true;
//
//                mBitmap= BitmapFactory.decodeFile(mBitmapResourceIds[mCurrentIndext], options);
//                BitmapDrawable drawable = new BitmapDrawable(mBitmap);
////                mCanvas.drawBitmap(mBitmap, (getWidth() - mBitmap.getWidth()) / 2,
////                        (getHeight() - mBitmap.getHeight()) / 2, null);
//                setBackground(drawable);
//                Log.i(TAG, "mCurrentIndext: "+mCurrentIndext+"       mBitmapResourceIds   "+mBitmapResourceIds.length);
//                // 播放到最后一张图片，停止线程
//                if (mCurrentIndext == mBitmapResourceIds.length - 1)
//                {
//                    mIsThreadRunning = false;
//                }
//
//            }
//        } catch (Exception e)
//        {
//            e.printStackTrace();
//        } finally
//        {
//            mCurrentIndext++;
//
//            if (mCanvas != null)
//            {
//                // 将画布解锁并显示在屏幕上
//                holder.unlockCanvasAndPost(mCanvas);
//            }
//            if (mBitmap != null)
//            {
//                // 收回图片
//                mBitmap.recycle();
//            }
//        }
//    }
//
//    @Override
//    public void run() {
//        if (mOnFrameFinishedListener != null) {
//            mOnFrameFinishedListener.onStart();
//        }
//        // 每隔100ms刷新屏幕
//        while (mIsThreadRunning)
//        {
//            drawView();
//            try {
//                Thread.sleep(mGapTime);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//        if (mOnFrameFinishedListener != null) {
//            mOnFrameFinishedListener.onStop();
//        }
//    }
//
//    /**
//     * 动画监听器
//     * @author qike
//     *
//     */
//    public interface OnFrameFinishedListener {
//
//        /**
//         * 动画开始
//         */
//        void onStart();
//
//        /**
//         * 动画结束
//         */
//        void onStop();
//    }
//
//    /**
//     * 当用户点击返回按钮时，停止线程，反转内存溢出
//     */
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        // 当按返回键时，将线程停止，避免surfaceView销毁了,而线程还在运行而报错
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            mIsThreadRunning = false;
//        }
//        return super.onKeyDown(keyCode, event);
//    }
}
