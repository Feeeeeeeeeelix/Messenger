
package de.felix.messenger;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;


@SuppressLint("ViewConstructor")
public class MessageLayout extends LinearLayout {

    TextView timeLabel;
    Context context;

    public MessageLayout(Context context, String messageText, String senderName, Integer Side) {
        super(context);
        this.context = context;
        int side = (Side==0) ? Gravity.START: Gravity.END;

//        Configure top linLayout
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0 ,10,0,10);
        this.setLayoutParams(layoutParams);
        setOrientation(HORIZONTAL);
        setGravity(side);


        Space space = new Space(context);
        space.setLayoutParams(new LayoutParams(150, ViewGroup.LayoutParams.MATCH_PARENT, 0));
        space.setMinimumWidth(50);


        LinearLayout outerVertLayout = new LinearLayout(context);
        outerVertLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1));
        outerVertLayout.setGravity(side);

//        Define Vertical Layout for message and timestamp
        LinearLayout vertLayout = new LinearLayout(context);
        vertLayout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 0));
        vertLayout.setOrientation(VERTICAL);
        vertLayout.setBackground(getResources().getDrawable(R.drawable.message_background));
        vertLayout.setGravity(side);
        vertLayout.setPadding(25, 25,25,25);
        outerVertLayout.addView(vertLayout);



        TextView senderLabel = new TextView(context);
        senderLabel.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        senderLabel.setTypeface(null, Typeface.BOLD);
        senderLabel.setGravity(Gravity.START);
        senderLabel.setText(senderName);
        vertLayout.addView(senderLabel,0);


        TextView messageLabel = new TextView(context);
        messageLabel.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        messageLabel.setText(messageText);
        vertLayout.addView(messageLabel,1);

        TextView timeLabel = new TextView(context);
        timeLabel.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        timeLabel.setText("");
        vertLayout.addView(timeLabel, 2);
        this.timeLabel = timeLabel;


        if (side == Gravity.END){
            addView(space);
            addView(outerVertLayout);
        }
        else{
            addView(outerVertLayout);
            addView(space);
        }
    }

    public void setTimeStamp(String timeStamp){
        timeLabel.setText(timeStamp);
    }
}
