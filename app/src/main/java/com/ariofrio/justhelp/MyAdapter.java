package com.ariofrio.justhelp;

import androidx.appcompat.graphics.drawable.DrawableContainerCompat.*;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;

import java.util.List;



public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {


    private List <String> dataList, dataList2, dataList3,imagenes;
    public MyAdapter(List<String> dataList,List<String> dataList2,List<String> dataList3,List<String> imagenes){
        this.dataList = dataList;
        this.dataList2 = dataList2;
        this.dataList3 = dataList3;
        this.imagenes= imagenes;
    }
    @NonNull
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout,parent,false);
        return new MyViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull MyAdapter.MyViewHolder holder, int position){
        holder.textView.setText(dataList.get(position));
        holder.textView2.setText(dataList2.get(position));
        holder.textView3.setText(dataList3.get(position));
        if(dataList3.get(position).equals("Necesito")) {

            holder.textView3.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.darkorange));

        }
        String imagenUrl=imagenes.get(position); // detecto posicion de la lista

        if (imagenUrl != null && !imagenUrl.isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(imagenUrl) // URL de la imagen desde Supabase
                    .placeholder(R.drawable.usuariott)// Imagen por defecto mientras carga
                    .transform(new CircleCrop())
                    .skipMemoryCache(true) // No usar caché en memoria
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .error(R.drawable.usuariott) // Imagen en caso de error
                    .into(holder.imageView);
        } else {
            holder.imageView.setImageResource(R.drawable.usuariott);
        }
        }
    @Override
    public int getItemCount(){
        return dataList.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView textView, textView2, textView3;
        ImageView imageView;
        public MyViewHolder(@NonNull View itemView){
            super(itemView);
            textView = itemView.findViewById(R.id.textView_titulo);
            //AÑADIR DOS TEXTVIEW MAS ***************************************
            textView2 = itemView.findViewById(R.id.textView_direccion);
            textView3 = itemView.findViewById(R.id.textView_tipo);
            imageView= itemView.findViewById(R.id.imageView);
        }



    }







}
