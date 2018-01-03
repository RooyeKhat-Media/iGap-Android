/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package net.iGap.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RadioButton;
import java.util.ArrayList;
import net.iGap.R;
import net.iGap.fragments.FragmentRegister;
import net.iGap.module.structs.StructCountry;
import net.iGap.viewmodel.FragmentRegisterViewModel;

public class AdapterDialog extends BaseAdapter implements Filterable {

    public static int mSelectedVariation = -1;
    public static boolean po = false;
    Context context;
    ArrayList<StructCountry> countrylist;
    ArrayList<StructCountry> mStringFilterList;
    ValueFilter valueFilter;
    private RadioButton name_tv;

    public AdapterDialog(Context context, ArrayList<StructCountry> countrylist) {
        this.context = context;
        this.countrylist = countrylist;
        mStringFilterList = countrylist;
    }

    @Override public int getCount() {
        return countrylist.size();
    }

    @Override public Object getItem(int position) {
        return countrylist.get(position);
    }

    @Override public long getItemId(int position) {
        return countrylist.indexOf(getItem(position));
    }

    @Override public View getView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        convertView = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.rg_adapter_dialog, null);
            name_tv = (RadioButton) convertView.findViewById(R.id.rg_radioButton);
            StructCountry structCountry = countrylist.get(position);
            name_tv.setText(structCountry.getName());
        }

        name_tv.setChecked(countrylist.get(position).getId() == mSelectedVariation);
        name_tv.setTag(position);
        name_tv.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {

                mSelectedVariation = (Integer) v.getTag();

                FragmentRegister.positionRadioButton = countrylist.get(position).getId();
                mSelectedVariation = countrylist.get(position).getId();
                notifyDataSetChanged();

                FragmentRegister.edtCodeNumber.setText(("+ " + countrylist.get(position).getCountryCode()));
                if (countrylist.get(position).getPhonePattern() != null) {
                    if (countrylist.get(position).getPhonePattern().equals(" ")) {
                        FragmentRegister.edtPhoneNumber.setMask("###-###-####");
                    } else {

                        FragmentRegister.edtPhoneNumber.setMask((countrylist.get(position).getPhonePattern().replace("X", "#").replace(" ", "-")));
                    }
                } else {
                    FragmentRegister.edtPhoneNumber.setMask("###-###-####");
                }
                FragmentRegister.btnChoseCountry.setText((countrylist.get(position).getName()));
                FragmentRegisterViewModel.isoCode = countrylist.get(position).getAbbreviation();
                FragmentRegisterViewModel.btnOk.performClick();
                FragmentRegisterViewModel.dialogChooseCountry.dismiss();
            }
        });

        return convertView;
    }

    @Override public Filter getFilter() {
        if (valueFilter == null) {
            valueFilter = new ValueFilter();
        }
        return valueFilter;
    }

    private class ValueFilter extends Filter {
        @Override protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if (constraint != null && constraint.length() > 0) {
                ArrayList<StructCountry> filterList = new ArrayList<StructCountry>();
                for (int i = 0; i < mStringFilterList.size(); i++) {
                    if ((mStringFilterList.get(i).getName().toUpperCase()).contains(constraint.toString().toUpperCase())) {

                        StructCountry structCountry = new StructCountry();
                        structCountry.setId(mStringFilterList.get(i).getId());
                        structCountry.setName(mStringFilterList.get(i).getName());
                        structCountry.setCountryCode(mStringFilterList.get(i).getCountryCode());
                        structCountry.setPhonePattern(mStringFilterList.get(i).getPhonePattern());
                        structCountry.setAbbreviation(mStringFilterList.get(i).getAbbreviation());
                        filterList.add(structCountry);
                    }
                }
                results.count = filterList.size();
                results.values = filterList;
            } else {
                results.count = mStringFilterList.size();
                results.values = mStringFilterList;
            }
            return results;
        }

        @Override protected void publishResults(CharSequence constraint, FilterResults results) {
            countrylist = (ArrayList<StructCountry>) results.values;
            notifyDataSetChanged();
        }
    }
}
