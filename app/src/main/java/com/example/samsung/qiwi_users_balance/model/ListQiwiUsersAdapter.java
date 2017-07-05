package com.example.samsung.qiwi_users_balance.model;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.samsung.qiwi_users_balance.R;

import java.util.List;

public class ListQiwiUsersAdapter extends RecyclerView.Adapter<ListQiwiUsersAdapter.UsersViewHolder>{

    private List<QiwiUsers> mDataset;

    // Конструктор
    public ListQiwiUsersAdapter(List<QiwiUsers> dataset) {
        mDataset = dataset;
    }

    // Создает новые views (вызывается layout manager-ом)
    @Override
    public UsersViewHolder onCreateViewHolder(
            ViewGroup parent, int viewType) {

        // create a new view
        View v = LayoutInflater.from(App.getApp().getBaseContext())
                .inflate(R.layout.recycler_item_users, parent, false);

        // тут можно программно менять атрибуты лэйаута (size, margins, paddings и др.)

        return new UsersViewHolder(v);
    }

    // Заменяет контент отдельного view (вызывается layout manager-ом)
    @Override
    public void onBindViewHolder(UsersViewHolder holder, int position) {
        QiwiUsers qiwiUser = mDataset.get(position);
        holder.bindQiwiUser(qiwiUser);
    }

    // Возвращает размер данных (вызывается layout manager-ом)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    // класс view holder-а с помощью которого мы получаем ссылку на каждый элемент
    // отдельного пункта списка
    static class UsersViewHolder extends RecyclerView.ViewHolder {
        // ълемент состоит из двух TextView
        private TextView mTvRecyclerItemId;
        private TextView mTvRecyclerItemName;

        public UsersViewHolder(View v) {
            super(v);
            mTvRecyclerItemId = (TextView) v.findViewById(R.id.tvRecyclerItemUsersId);
            mTvRecyclerItemName = (TextView) v.findViewById(R.id.tvRecyclerItemUsersName);
        }

        public void bindQiwiUser(QiwiUsers qiwiUsers) {
            mTvRecyclerItemId.setText(qiwiUsers.getId().toString());
            mTvRecyclerItemName.setText(qiwiUsers.getName().toString());
        }

        public TextView getTvRecyclerItemId() {
            return mTvRecyclerItemId;
        }

        public TextView getTvRecyclerItemName() {
            return mTvRecyclerItemName;
        }
    }

}
