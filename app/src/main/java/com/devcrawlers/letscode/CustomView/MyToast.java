package com.devcrawlers.letscode.CustomView;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.devcrawlers.letscode.R;

public class MyToast extends android.widget.Toast {
    private Context context;


    ImageView image;
    TextView textView;
    private ImageView imageIco;

    public MyToast(Context context) {
        super(context);
        this.context = context;

    }

    public static MyToast makeText(Context context, CharSequence text, int duration) {
        MyToast t;
        t = new MyToast(context);
        t.setText(text);
        t.setDuration(duration);
        return t;
    }

    @Override
    public View getView() {
        View layoutValue = LayoutInflater.from(context).inflate(R.layout.custom_toast_layout, null);

        image = layoutValue.findViewById(R.id.toast_image);
        textView = layoutValue.findViewById(R.id.toast_text);
        imageIco = layoutValue.findViewById(R.id.toast_typealert);

        return layoutValue;
    }

    @Override
    public void setText(CharSequence s) {
        textView.setText(s);
    }

    @Override
    public void setText(int s) {
        textView.setText(context.getString(s));
    }

    public void setImage(int imageRes) {
        this.image.setImageResource(imageRes);
    }


}
