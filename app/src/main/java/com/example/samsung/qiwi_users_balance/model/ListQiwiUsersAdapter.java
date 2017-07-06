package com.example.samsung.qiwi_users_balance.model;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.samsung.qiwi_users_balance.R;

import java.util.List;

import static android.view.View.*;

public class ListQiwiUsersAdapter extends RecyclerView.Adapter<ListQiwiUsersAdapter.UsersViewHolder> {

    private List<QiwiUsers> mDataset;
    private OnItemClickListener mOnItemClickListener;
    // Конструктор
    public ListQiwiUsersAdapter(List<QiwiUsers> dataset) {
        this.mDataset = dataset;
    }

    // Создает новые views (вызывается layout manager-ом)
    @Override
    public UsersViewHolder onCreateViewHolder(
            ViewGroup parent, int viewType) {

        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_item_users, parent, false);

        // тут можно программно менять атрибуты лэйаута (size, margins, paddings и др.)
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams)v.getLayoutParams();
        params.height = RecyclerView.LayoutParams.WRAP_CONTENT;
        v.setLayoutParams(params);

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

    public List<QiwiUsers> getDataset() {
        return mDataset;
    }

    //Интерфейс обработки нажатия на элемент списка
    public interface OnItemClickListener {
        public void onItemClick(View view , int position);
    }

    public void SetOnItemClickListener(final OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }
    // класс view holder-а с помощью которого мы получаем ссылку на каждый элемент
    // отдельного пункта списка и обрабатываем его нажатие
    class UsersViewHolder extends RecyclerView.ViewHolder {
        // ълемент состоит из двух TextView
        TextView mTvRecyclerItemId;
        TextView mTvRecyclerItemName;

        public UsersViewHolder(final View itemView) {
            super(itemView);
            mTvRecyclerItemId = (TextView) itemView.findViewById(R.id.tvRecyclerItemUsersId);
            mTvRecyclerItemName = (TextView) itemView.findViewById(R.id.tvRecyclerItemUsersName);

            itemView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View itemView) {
                    if (mOnItemClickListener != null) {
                        //noinspection deprecation
                        mOnItemClickListener.onItemClick(itemView, getLayoutPosition());
                    }
                }
            });
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
