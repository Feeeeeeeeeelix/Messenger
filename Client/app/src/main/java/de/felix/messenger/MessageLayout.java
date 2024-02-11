package de.felix.messenger;

import android.content.Context;
import android.media.Image;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MessageLayout extends LinearLayout {

    TextView timeLabel;
    LinearLayout vertLayout;
    Context context;

    public MessageLayout(Context context) {
        super(context);
        this.context = context;

//        Configure top linLayout
        setOrientation(HORIZONTAL);
        setGravity(5);
        setBackgroundColor(0xFFC8E6C9);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0 ,10,0,10);
        this.setLayoutParams(layoutParams);
//        setPadding(8,8,8,8);

//        Define Vertical Layout for message and timestamp
        LinearLayout vertLayout = new LinearLayout(context);
        vertLayout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        vertLayout.setOrientation(VERTICAL);
        vertLayout.setBackgroundColor(0xFF7689F3);
        vertLayout.setGravity(Gravity.END);
        vertLayout.setPadding(8,8,8,8);
        this.addView(vertLayout);
        this.vertLayout = vertLayout;

//        Define timestamp textlabel
        TextView timeLabel = new TextView(context);
        timeLabel.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        timeLabel.setGravity(5);
        timeLabel.setText("");
        vertLayout.addView(timeLabel);
        this.timeLabel = timeLabel;

    }

    public void setTimeStamp(String timeStamp){
        timeLabel.setText(timeStamp);
    }

    public void setMessageContent(String messageText){
        TextView messageLabel = new TextView(context);
        messageLabel.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        messageLabel.setText(messageText);
//        messageLabel.setId(String.join(id, "-content"));

        vertLayout.addView(messageLabel,0);
    }

    public void setImageContent(Image imageContent){
        ImageView imageLabel = new ImageView(context);
        imageLabel.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
//        imageLabel.imageContent);

        vertLayout.addView(imageLabel, 0);
    }
}
