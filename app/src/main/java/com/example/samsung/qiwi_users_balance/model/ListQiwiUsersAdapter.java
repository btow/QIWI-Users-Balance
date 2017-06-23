package com.example.samsung.qiwi_users_balance.model;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.samsung.qiwi_users_balance.R;

import java.util.List;

public class ListQiwiUsersAdapter extends RecyclerView.Adapter<ListQiwiUsersAdapter.ViewHolder>{

    private List<QiwiUsers> mDataset;

    // класс view holder-а с помощью которого мы получаем ссылку на каждый элемент
    // отдельного пункта списка
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // ълемент состоит из двух TextView
        private TextView mTvRecyclerItemId;
        private TextView mTvRecyclerItemName;

        public ViewHolder(View v) {
            super(v);
            mTvRecyclerItemId = (TextView) v.findViewById(R.id.tvRecyclerItemId);
            mTvRecyclerItemName = (TextView) v.findViewById(R.id.tvRecyclerItemName);
        }

        public TextView getTvRecyclerItemId() {
            return mTvRecyclerItemId;
        }

        public TextView getTvRecyclerItemName() {
            return mTvRecyclerItemName;
        }
    }

    // Конструктор
    public ListQiwiUsersAdapter(List<QiwiUsers> dataset) {
        mDataset = dataset;
    }

    // Создает новые views (вызывается layout manager-ом)
    @Override
    public ListQiwiUsersAdapter.ViewHolder onCreateViewHolder(
            ViewGroup parent, int viewType) {

        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_item, parent, false);

        // тут можно программно менять атрибуты лэйаута (size, margins, paddings и др.)

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Заменяет контент отдельного view (вызывается layout manager-ом)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.getTvRecyclerItemId().setText(mDataset.get(position).getId());
        holder.getTvRecyclerItemName().setText(mDataset.get(position).getName());
    }

    // Возвращает размер данных (вызывается layout manager-ом)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}
