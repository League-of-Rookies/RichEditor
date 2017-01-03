package com.stateless.lib.richedit.view;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.stateless.lib.richedit.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangjie on 16/12/27.
 */
public class WordStyleAdapter extends RecyclerView.Adapter<WordStyleAdapter.WordStyleViewHold>{

    private Context context;
    private String[] title={"B","/","$","U","\"","H1","H2","H3","H4"};

    private List<WordStyleModel> list;

    public List<WordStyleModel> getList() {
        return list;
    }

    public WordStyleAdapter(Context context) {
        this.context=context;
        list=new ArrayList<>();
        for (String s:title){
            WordStyleModel model=new WordStyleModel();
            model.setName(s);
            model.setSelect(false);
            list.add(model);
        }
    }

    @Override
    public WordStyleViewHold onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_word_style,parent,false);
        WordStyleViewHold vh = new WordStyleViewHold(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(WordStyleViewHold holder, int position) {
        if (list.get(position).isSelect()){
            holder.itemBg.setBackgroundColor(Color.RED);
        }else {
            holder.itemBg.setBackgroundColor(Color.GRAY);
        }
        holder.name.setText(list.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public static class WordStyleViewHold extends RecyclerView.ViewHolder {
        private RelativeLayout itemBg;
        private TextView name;

        public WordStyleViewHold(View itemView) {
            super(itemView);
            itemBg=(RelativeLayout) itemView.findViewById(R.id.rl_item_bg);
            name=(TextView)itemView.findViewById(R.id.tv_item_name);

        }


    }



}
