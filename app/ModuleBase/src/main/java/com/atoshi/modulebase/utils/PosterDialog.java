package com.atoshi.modulebase.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import androidx.annotation.NonNull;

import com.atoshi.modulebase.R;

/**
 * author：yang
 * created on：2020/9/8 16:30
 * description:
 */
public class PosterDialog extends Dialog {
    private Bitmap mPostBitmap;
    private ShareClickListener mClickListener;;

    public interface ShareClickListener{
        void shareFriends();
        void shareFriendsCircle();
    }


    public static PosterDialog getInstance(Context context){
        return new PosterDialog(context);
    }
    private PosterDialog(@NonNull Context context) {
        super(context, R.style.custom_dialog);
    }

    public PosterDialog setPosterBitmap(Bitmap bitmap){
        this.mPostBitmap = bitmap;
        return this;
    }

    public PosterDialog setShareClickListener(ShareClickListener listener){
        this.mClickListener = listener;
        return this;
    }

    public PosterDialog init() {
        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_poster, null);
        setContentView(contentView);

        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        WindowManager.LayoutParams attributes = getWindow().getAttributes();
        attributes.width = displayMetrics.widthPixels;
        attributes.height = displayMetrics.heightPixels;
        getWindow().setAttributes(attributes);

        ImageView ivPoster = contentView.findViewById(R.id.ivPoster);
        ivPoster.setImageBitmap(mPostBitmap);
        contentView.findViewById(R.id.iv_friends).setOnClickListener(v -> {
            mClickListener.shareFriends();
            dismiss();
        });
        contentView.findViewById(R.id.iv_friends_circle).setOnClickListener(v -> {
            mClickListener.shareFriendsCircle();
            dismiss();
        });

        contentView.findViewById(R.id.iv_dismiss).setOnClickListener(v -> dismiss());
        return this;
    }

    @Override
    public void show() {
        Context context = getContext();
        if (context instanceof Activity) {
            Activity act = (Activity) context;
            if(act.isFinishing()) return;
        }
        super.show();
    }
}
