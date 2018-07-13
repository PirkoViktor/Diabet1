package com.pirko.diabetolog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import Data.Products;


public class AddAdapter extends ArrayAdapter<Products> {

    Context contextView;
    int resourceView, textViewId;
    List<Products> itemsList, tempItemsList, suggestionsList;

    public AddAdapter(Context contextView, int resourceView, int textViewResourceId, List<Products> items) {
        super(contextView, resourceView, textViewResourceId, items);
        this.contextView = contextView;
        this.resourceView = resourceView;
        this.textViewId =textViewResourceId;
        this.itemsList =items;
        tempItemsList =new ArrayList<Products>(items);
        suggestionsList =new ArrayList<Products>();
    }

    @Override
    public View getView(int cursor, View editedView, ViewGroup type) {
        View view=editedView;
        if (editedView == null) {
            LayoutInflater inflater=(LayoutInflater) contextView.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view=inflater.inflate(com.pirko.diabetolog.R.layout.row_people, type, false);
        }
        Products products= itemsList.get(cursor);
        if (products != null) {
            TextView productslName=(TextView) view.findViewById(com.pirko.diabetolog.R.id.lbl_products);

            if (productslName != null)
                productslName.setText(products.getName());
        }
        return view;
    }

    @Override
    public Filter getFilter() {
        return productsFilter;
    }

    /**
     *
     Реалізація спеціального фільтра для відображеення потрібних результатів
     */
    Filter productsFilter =new Filter() {
        @Override
        public CharSequence convertResultToString(Object resultValue) {
            String str=((Products) resultValue).getName()+" Вугл. "+((Products) resultValue).getId()+"гр.";
            return str;
        }

        @Override
        protected FilterResults performFiltering(CharSequence edittype) {
            if (edittype != null) {
                suggestionsList.clear();
                for (Products products : tempItemsList) {
                    if (products.getName().toLowerCase().contains(edittype.toString().toLowerCase())) {
                        suggestionsList.add(products);
                    }
                }
                FilterResults Results=new FilterResults();
                Results.values= suggestionsList;
                Results.count= suggestionsList.size();
                return Results;
            } else {
                return new FilterResults();
            }
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            List<Products> filterList=(ArrayList<Products>) results.values;
            if (results != null && results.count > 0) {
                clear();
                for (Products people : filterList) {
                    add(people);
                    notifyDataSetChanged();
                }
            }
        }
    };
}