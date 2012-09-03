package com.pc.nettols.example;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends ListActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ArrayAdapter adapter = ArrayAdapter.createFromResource(getApplicationContext(),
                R.array.examples, android.R.layout.simple_list_item_1);
        setListAdapter(adapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Class activityClass = null;

        switch (position) {
            case 0:
                activityClass = GetActivity.class;
                break;
            case 1:
                activityClass = PostActivity.class;
                break;
            case 2:
                activityClass = BasicAuthActivity.class;
                break;
            case 3:
                activityClass = ImagesActivity.class;
                break;
            case 4:
                activityClass = DOMActivity.class;
                break;
            case 5:
                activityClass = SAXActivity.class;
                break;
            case 6:
                activityClass = FileActivity.class;
                break;
        }

        Intent intent = new Intent(getApplicationContext(), activityClass);
        startActivity(intent);
    }
}