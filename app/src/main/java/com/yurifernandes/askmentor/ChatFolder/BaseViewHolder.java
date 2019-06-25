package com.yurifernandes.askmentor.ChatFolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;

public abstract class BaseViewHolder extends RecyclerView.ViewHolder {
    BaseViewHolder(View itemView) {
        super(itemView);
    }

    public abstract void onBindView(ChatObject object);
}
