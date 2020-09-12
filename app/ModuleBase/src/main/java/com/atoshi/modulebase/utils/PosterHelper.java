package com.atoshi.modulebase.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * author：yang
 * created on：2020/9/9 10:28
 * description: 海报生成
 */
public class PosterHelper {
    private String mBgUrl;
    private String mAvatarUrl;
    private String mCodeUrl;
    private String mCodeStr;
    private String mIntroStr;
    private Callback mCallback;

    private PosterHelper(){}
    public static PosterHelper getInstance(){
        return new PosterHelper();
    }

    public PosterHelper setBgUrl(String bgUrl){
        this.mBgUrl = bgUrl;
        return this;
    }
    public PosterHelper setAvatarUrl(String avatarUrl){
        this.mAvatarUrl = avatarUrl;
        return this;
    }
    public PosterHelper setCodeUrl(String codeUrl){
        this.mCodeUrl = codeUrl;
        return this;
    }
    public PosterHelper setCodeStr(String codeStr){
        this.mCodeStr = codeStr;
        return this;
    }
    public PosterHelper setIntroStr(String introStr){
        this.mIntroStr = introStr;
        return this;
    }

    public void getPosterBitmap(Callback callback){
        this.mCallback = callback;
        new Thread(() -> {
            Bitmap bgBitmap = getBitmap(mBgUrl);
            Bitmap headerBitmap = getBitmap(mAvatarUrl);
            Bitmap QRCBitmap = getBitmap(mCodeUrl);
            long drawStart = SystemClock.currentThreadTimeMillis();
            Bitmap newBitmap = drawBitmap(bgBitmap, headerBitmap, QRCBitmap, mCodeStr, mIntroStr);
            System.out.println("PosterHelper.onCreate， drawTims = "+ (SystemClock.currentThreadTimeMillis() - drawStart));
            success(newBitmap);
        }).start();
    }

    private void success(Bitmap bitmap){
        new Handler(Looper.getMainLooper()).post(()->{
            if (bitmap != null) {
                mCallback.success(bitmap);
            }else {
                mCallback.failed("图片生成失败");
            }
        });
    }
    private void failed(String msg){
        new Handler(Looper.getMainLooper()).post(()->{
            mCallback.failed(msg);
        });
    }

    private Bitmap drawBitmap(Bitmap backgroundBitmap, Bitmap headerBitmap, Bitmap qrcBitmap, String codeStr, String introStr) {
        if (backgroundBitmap == null || qrcBitmap == null) {
            return null;
        }
        Bitmap bgBitmap = backgroundBitmap.copy(backgroundBitmap.getConfig(), true);
        Canvas canvas = new Canvas(bgBitmap);
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        System.out.println("PosterHelper.drawBitmap: wh: "+width+", "+height);

        Paint paint = new Paint();
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);

        //圆角背景
        RectF bgBotRect = new RectF(width*0.03f,height*0.77f, width*0.97f, height*0.97f);
        paint.setColor(0xffFFFFFF);
        canvas.drawRoundRect(bgBotRect, width*0.03f, width*0.03f, paint);
        //我的邀请码
        Rect codeRect = new Rect();
        paint.setColor(0xFF29b833);
        paint.setTextSize(width*0.055f);
        paint.setFakeBoldText(true);
        paint.getTextBounds(codeStr, 0, codeStr.length(), codeRect);
        System.out.println("PosterHelper.drawBitmap: code = "+codeRect);
        canvas.drawText(codeStr, bgBotRect.left*2, bgBotRect.top + bgBotRect.left*3/2 + Math.abs(codeRect.top), paint);
        //头像
        RectF disHeaderRectF = new RectF();
        disHeaderRectF.left = bgBotRect.left*2;
        disHeaderRectF.top = bgBotRect.top + bgBotRect.left*3 + Math.abs(codeRect.top);
        disHeaderRectF.bottom = bgBotRect.bottom - bgBotRect.left;
        disHeaderRectF.right = disHeaderRectF.left + (disHeaderRectF.bottom - disHeaderRectF.top);
        if (headerBitmap != null) canvas.drawBitmap(headerBitmap, null, disHeaderRectF, paint);

        //二维码
        RectF disCodeRectF = new RectF();
        disCodeRectF.top = bgBotRect.top + bgBotRect.left;
        disCodeRectF.right = bgBotRect.right - bgBotRect.left;
        disCodeRectF.bottom = bgBotRect.bottom - bgBotRect.left;
        disCodeRectF.left = disCodeRectF.right - (disCodeRectF.bottom - disCodeRectF.top);
        canvas.drawBitmap(qrcBitmap, null, disCodeRectF, paint);

        float introSize = width * 0.037f;
        String[] intros = introStr.split("\n");

        paint.setColor(Color.BLACK);
        paint.setTextSize(introSize);
        paint.setFakeBoldText(false);

        String intro1 = intros[0];
        Rect intro1Rect = new Rect();
        paint.getTextBounds(intro1, 0, intro1.length(), intro1Rect);
        intro1Rect.left = (int) (disHeaderRectF.right + bgBotRect.left);
        intro1Rect.right = (int) (disCodeRectF.left - bgBotRect.left);
        intro1Rect.top = (int) (disHeaderRectF.top - bgBotRect.left/3);
        intro1Rect.bottom = (int) (disHeaderRectF.top + introSize);
        canvas.drawText(intros[0], intro1Rect.left, intro1Rect.top + (intro1Rect.bottom - intro1Rect.top), paint);

        //介绍
        canvas.save();
        RectF introRect = new RectF();
        introRect.left = intro1Rect.left;
        introRect.top = intro1Rect.bottom + introSize/3*2;
        introRect.right = intro1Rect.right;
        introRect.bottom = disCodeRectF.bottom + bgBotRect.left;
        canvas.translate(introRect.left, introRect.top);
        TextPaint textPaint = new TextPaint(paint);
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(introSize);
        StaticLayout staticLayout = new StaticLayout(intros[1], textPaint, (int) (introRect.right - introRect.left), Layout.Alignment.ALIGN_NORMAL, 0.9f, 0, true);
        staticLayout.draw(canvas);
        canvas.restore();


        if(!backgroundBitmap.isRecycled()) backgroundBitmap.recycle();
        if(headerBitmap != null && !headerBitmap.isRecycled()) headerBitmap.recycle();
        if(!qrcBitmap.isRecycled()) qrcBitmap.recycle();
        System.out.println("PosterHelper.drawBitmap: isRecycled="+bgBitmap.isRecycled());
        return bgBitmap;
    }

    private Bitmap getBitmap(String imgUrl){
        System.out.println("PosterHelper.getBitmap: start...");
        long startTime = SystemClock.currentThreadTimeMillis();
        InputStream inputStream = null;
        ByteArrayOutputStream outputStream = null;
        URL url;
        try{
            url = new URL(imgUrl);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setReadTimeout(15000);
            httpURLConnection.connect();
            if (httpURLConnection.getResponseCode() == 200) {
                inputStream = httpURLConnection.getInputStream();
                outputStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024*10];
                int len;
                while ((len = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, len);
                }
                byte[] bytes = outputStream.toByteArray();
                Bitmap retBitmap = sampleBitmap(bytes);
                long endTime = SystemClock.currentThreadTimeMillis();
                System.out.println("PosterHelper.getBitmap：times："+ (endTime - startTime));
                return retBitmap;
            }else {
                System.out.println("PosterHelper.getBitmap, 获取图片失败："+httpURLConnection.getResponseCode());
                failed("获取图片失败："+httpURLConnection.getResponseCode());
            }
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("PosterHelper.getBitmap: e="+e.toString());
            failed(e.toString());
        }finally {
            System.out.println("PosterHelper.getBitmap: finally");
            try {
                inputStream.close();
            }catch (Exception e){ e.printStackTrace();}
            try {
                outputStream.close();
            }catch (Exception e){e.printStackTrace();}
        }
        return null;
    }

    private Bitmap sampleBitmap(byte[] bytes) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
        System.out.println("PosterHelper.sampleBitmap: row wh: "+ options.outWidth+", "+options.outHeight);
        int sampleSize = Math.max(1, Math.max(options.outWidth/500, options.outHeight/1000));
        options.inJustDecodeBounds = false;
        options.inSampleSize = sampleSize;
        System.out.println("PosterHelper.sampleBitmap: sampleSize = "+sampleSize);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
    }

    public interface Callback{
        void success(Bitmap bitmap);
        void failed(String errMsg);
    }
}
