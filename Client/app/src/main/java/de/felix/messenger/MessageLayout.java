
package de.felix.messenger;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;


@SuppressLint("ViewConstructor")
public class MessageLayout extends LinearLayout {

    TextView timeLabel;
    LinearLayout vertLayout;
    Context context;

    public MessageLayout(Context context, Integer Side) {
        super(context);
        this.context = context;
        int side = (Side==0) ? Gravity.START: Gravity.END;

//        Configure top linLayout
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0 ,10,0,10);
        this.setLayoutParams(layoutParams);
        setOrientation(HORIZONTAL);
//        setBackgroundColor(0xFFB6B9F3);
        setGravity(side);

        Space space = new Space(context);
        space.setLayoutParams(new LayoutParams(150, ViewGroup.LayoutParams.MATCH_PARENT));
        space.setMinimumWidth(50);
//        TODO: space when side=0

//        Define Vertical Layout for message and timestamp
        LinearLayout vertLayout = new LinearLayout(context);
        vertLayout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 0));
        vertLayout.setOrientation(VERTICAL);
        vertLayout.setBackgroundColor(0xFF7689F3);
        vertLayout.setGravity(side);
        vertLayout.setPadding(20, 20,20,20);
        this.vertLayout = vertLayout;

        if (side == Gravity.END){
            addView(space);
            addView(vertLayout);
        }
        else{
            addView(vertLayout);
            addView(space);
        }

//        Define timestamp textlabel
        TextView timeLabel = new TextView(context);
        timeLabel.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        timeLabel.setGravity(side);
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

        vertLayout.addView(messageLabel,0);
    }

}
