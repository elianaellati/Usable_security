package com.example.usable_security;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class Adapter
        extends RecyclerView.Adapter<Adapter.ViewHolder>{

    private String[] name;
    private String[] email;
    private Context context;

    public Adapter( String[] name,String[] email){
       this.name=name;
       this.email=email;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView v = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.assigned,
                parent,
                false);
        context = parent.getContext();

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CardView cardView = holder.cardView;
        TextView namee = (TextView)cardView.findViewById(R.id.name);
        namee.setText(name[position]);
        TextView emaill = (TextView)cardView.findViewById(R.id.email);
        emaill.setText(email[position]);
    }

    @Override
    public int getItemCount() {
        return name.length;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{
        private CardView cardView;
        public ViewHolder(CardView cardView){
            super(cardView);
            this.cardView = cardView;
        }

    }
}