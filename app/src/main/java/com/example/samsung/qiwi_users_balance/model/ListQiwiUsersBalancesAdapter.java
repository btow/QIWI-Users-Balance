package com.example.samsung.qiwi_users_balance.model;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.samsung.qiwi_users_balance.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ListQiwiUsersBalancesAdapter extends RecyclerView.Adapter<ListQiwiUsersBalancesAdapter.ViewHolder>{

    private List<QiwiUsersBalances> mDataset;

    // класс view holder-а с помощью которого мы получаем ссылку на каждый элемент
    // отдельного пункта списка
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // ълемент состоит из двух TextView
        private TextView mTvRecyclerItemBalance;

        public ViewHolder(View v) {
            super(v);
            mTvRecyclerItemBalance = (TextView) v.findViewById(R.id.tvRecyclerItemBalance);
        }

        public TextView getTvRecyclerItemBalance() {
            return mTvRecyclerItemBalance;
        }
    }

    // Конструктор
    public ListQiwiUsersBalancesAdapter(List<QiwiUsersBalances> dataset) {
        mDataset = dataset;
    }

    // Создает новые views (вызывается layout manager-ом)
    @Override
    public ListQiwiUsersBalancesAdapter.ViewHolder onCreateViewHolder(
            ViewGroup parent, int viewType) {

        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_item_balances, parent, false);

        // тут можно программно менять атрибуты лэйаута (size, margins, paddings и др.)

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Заменяет контент отдельного view (вызывается layout manager-ом)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        String pattern = "###,###.##";
        switch (mDataset.get(position).getCurrency()) {

            case "KZT":
                pattern += " тңг";
                break;
            case "RUB":
                pattern += " руб.";
                break;
            case "USD":
                pattern = "$ " + pattern;
                break;
            case "EUR":
                pattern = "€ " + pattern;
                break;
            default:
                break;
        }
        DecimalFormat df = new DecimalFormat(pattern);
        String output = df.format(mDataset.get(position).getAmount());
        holder.getTvRecyclerItemBalance().setText(output);
    }

    // Возвращает размер данных (вызывается layout manager-ом)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}
