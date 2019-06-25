package com.yurifernandes.askmentor.ChatFolder;

import android.view.View;
import android.widget.TextView;

import com.yurifernandes.askmentor.R;

public class ChatResponseVH extends BaseViewHolder {

    private TextView tvResponseText;

    public ChatResponseVH(View itemView) {
        super(itemView);
        this.tvResponseText = (TextView) itemView.findViewById(R.id.tv_response_text);
    }

    @Override
    public void onBindView(ChatObject object) {
        this.tvResponseText.setText(object.getText());
    }
}
