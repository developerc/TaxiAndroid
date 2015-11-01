package com.taxiandroid.ru.taxiandr;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class ActivityTwo extends AppCompatActivity {
    ArrayList<Two> arrayTwo;
    ListView lvTwo;
    CustomTwoAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two);

        populateTwoList();
        lvTwo.setOnItemClickListener(itemClickListener);
    }

    //обрабатываем нажатие на пункте списка заказов
    protected AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            view.setSelected(true);
            Two itemLV = (Two) parent.getItemAtPosition(position);
            String itemAct = null;
            //if (position ==1) {itemAct="Вы выбрали звонилку";}
            switch (position) {
                case 0: {
                    itemAct = "Вы выбрали Отправку СМС";
                }
                break;
                case 1: {
                    itemAct="Вы выбрали звонилку";
                }
                break;
                case 2: {
                    itemAct="Вы выбрали таксометр";
                    startActivity(new Intent(getApplicationContext(),ActivityThree.class));
                    finish();
                }
            }

            Toast.makeText(getApplicationContext(),
                    itemAct, Toast.LENGTH_SHORT).show();
        }
    };

        private void populateTwoList() {
        arrayTwo = Two.getTwoItem();
        adapter = new CustomTwoAdapter(this,arrayTwo);
        lvTwo = (ListView) findViewById(R.id.lvActTwo);
        lvTwo.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_two, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
