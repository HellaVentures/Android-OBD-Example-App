package veronika.hella.obdapp.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import veronika.hella.obdapp.R;

/**
 * Created by Veronika on 06.09.2016.
 */

class ResultAdapter extends ArrayAdapter<String> {

    private Context context;
    private ArrayList<String> values;

    ResultAdapter(Context context, ArrayList<String> values) {
        super(context, -1, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String[] vals = values.get(position).split("\\n");

        final MainListHolder mainListHolder;
        View rowView = convertView;
        if (convertView == null) {
            mainListHolder = new MainListHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.result_item, parent, false);
            mainListHolder.text1 = (TextView) rowView.findViewById(R.id.text1);
            mainListHolder.text2 = (TextView) rowView.findViewById(R.id.text2);
            rowView.setTag(mainListHolder);
        } else {
            mainListHolder = (MainListHolder) rowView.getTag();
        }
        mainListHolder.text1.setText(vals[0]);

        for (int i = 1; i < vals.length; i++) {
            if (i != 1)
                mainListHolder.text2.setText(mainListHolder.text2.getText() + "\n" + vals[i]);
            else
                mainListHolder.text2.setText(vals[i]);
        }
        return rowView;
    }

    private class MainListHolder {
        private TextView text1;
        private TextView text2;
    }
}
