package com.taxiandroid.ru.taxiandr;

import android.content.SharedPreferences;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {
    ArrayList<Orders> arrayOfOrders;
    ListView lvOrders;
    CustomOrdersAdapter adapter;
    String httpPath;
    final Handler myHandler = new Handler();
   // Runnable runnable;
   // HTTGATask httgatask;  //обьявили класс для метода GEt
    private static final String TAG = "myLogs";
    final String textSource = "http://pchelka.teleknot.ru/api/user1/x11unkde/orders";
    public  static boolean ZakazEmpty = true;
    SharedPreferences sPref;
    final String SAVED_TEXT_LGN = "saved_text_lgn";
    final String SAVED_TEXT_PSW = "saved_text_psw";

    public static ArrayList<Integer> zakaz = new ArrayList<Integer>();
    public static ArrayList<String> telefon = new ArrayList<String>();
    public static ArrayList<String> kode = new ArrayList<String>();
    public static ArrayList<String> dat = new ArrayList<String>();
    public static ArrayList<String> tim = new ArrayList<String>();
    public static ArrayList<String> adres = new ArrayList<String>();
    public static ArrayList<String> car = new ArrayList<String>();
    public static ArrayList<String> predvar = new ArrayList<String>();

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        populateUsersList();
        lvOrders.setOnItemClickListener(itemClickListener);

        Timer myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                UpdateGUI();
            }
        }, 0, 15000);

        sPref = this.getSharedPreferences("pref",0);
        MyVariables.SAVED_TEXT_1 = sPref.getString(SAVED_TEXT_LGN, "");
        MyVariables.SAVED_TEXT_2 = sPref.getString(SAVED_TEXT_PSW, "");

        httpPath =MyVariables.HTTPAdress+MyVariables.SAVED_TEXT_1+"/"+MyVariables.SAVED_TEXT_2+"/orders";
        Log.d(TAG, "Запустилось! " );
    }

    private void UpdateGUI() {
//        i++;
        myHandler.post(myRunnable);
    }

    final Runnable myRunnable = new Runnable() {
        public void run() {
           Log.d(TAG, "Таймер работает! " );
            new GetAsincTask().execute(httpPath);
            if (ZakazEmpty == false) {
                updateUsersList();
            }
        }
    };

    public class GetAsincTask extends AsyncTask<String, Void, Void> {

        String textResult;

        @Override
        protected Void doInBackground(String... params) {

            try {
                Log.d(TAG, "*******************    Open Connection    *****************************");
                URL url = new URL(params[0]);
                Log.d(TAG, "Received URL:  " + url);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.connect();
                int response = conn.getResponseCode();
               // Log.d(TAG, "The response is: " + response);
                InputStream in = conn.getInputStream();
               // Log.d(TAG, "GetInputStream:  " + in);

               // Log.d(TAG, "*******************    String Builder     *****************************");
                String line = null;

                BufferedReader bufferReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                String StringBuffer;
                String stringText = "";
                while ((StringBuffer = bufferReader.readLine()) != null) {
                    stringText += StringBuffer;
                }
                bufferReader.close();

                textResult = stringText;

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                textResult = e.toString();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                textResult = e.toString();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            //получили JSON строку с сервера
           // Log.d(TAG, textResult);
            //обрабатываем JSON строку
            try {
                ZakazJson(textResult);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            super.onPostExecute(result);
        }
    }  //Закончился GetAsincTask


    //обработка JSON строки
    public void ZakazJson(String jsonString) throws JSONException {
        Log.d(TAG, "*******************    обрабатываем JSON строку     *****************************");
        if (jsonString.contains("ERROR: zakazi not found")) {
            ZakazEmpty = true;
            Log.d(TAG, "Заказов нет");
        }
        else {
            Log.d(TAG, "Заказы есть");
            ZakazEmpty = false;
            jsonString = "{\"myjsonarray\"="+jsonString+"}";
            Log.d(TAG, jsonString);
            JSONObject jo =  new JSONObject(jsonString);
            JSONArray jsonMainArr = jo.getJSONArray("myjsonarray");

            //Очищаем ArrayList
            zakaz.clear();
            telefon.clear();
            kode.clear();
            dat.clear();
            tim.clear();
            adres.clear();
            car.clear();
            predvar.clear();

            for(int i=0; i<jsonMainArr.length(); i++) {
                JSONObject json_data = jsonMainArr.getJSONObject(i);
                zakaz.add(json_data.getInt("zakaz"));
                telefon.add(json_data.getString("telefon"));
                kode.add(json_data.getString("kode"));
                dat.add(json_data.getString("dat"));
                tim.add(json_data.getString("tim"));
                adres.add(json_data.getString("adres"));
                car.add(json_data.getString("car"));
                predvar.add(json_data.getString("predvar"));
                // Log.d(TAG, "Заказ=" + zakaz.get(i) + "  Адрес:" + adres.get(i) + "  Предварительный:" + predvar.get(i));

            }
            for(int i=0; i<zakaz.size(); i++) {

                Log.d(TAG, "Заказ=" + zakaz.get(i) + "  Адрес:" + adres.get(i) + "  Предварительный:" + predvar.get(i) + " Дата:" + dat.get(i) + " Время:" + tim.get(i).substring(11,16) + " Машина:" + car.get(i));
            }

        }


    }

    /*class HTTGATask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            try {
                ShedZapros(params[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {  //выводим результат в GUI
            super.onPostExecute(result);

        }

        private void ShedZapros(String myurl) throws IOException {//зашедуленый запрос на прием заказа
            InputStream is = null;

            try {
                URL url = new URL(myurl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 );
                conn.setConnectTimeout(15000 );
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.connect();
                int response = conn.getResponseCode();
                Log.d("TAG", "The response is: " + response);
                is = conn.getInputStream();

            } finally {
                if (is != null) {
                    is.close();
                }
            } //конец ShedZapros
        }
    }*/

    //обрабатываем нажатие на пункте списка заказов
    protected AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            view.setSelected(true);
            Orders itemLV = (Orders) parent.getItemAtPosition(position);
            String itemZak = itemLV.adres;
            Toast.makeText(getApplicationContext(),
                    "Вы выбрали " + itemZak + " \n Для отказа нажмите на заказ", Toast.LENGTH_SHORT).show();
            //
            startActivity(new Intent(getApplicationContext(),ActivityTwo.class));
        }
    };

    private void populateUsersList() {
        // Construct the data source
        arrayOfOrders = Orders.getOrders();
        // Create the adapter to convert the array to views
        adapter = new CustomOrdersAdapter(this, arrayOfOrders);
        lvOrders = (ListView) findViewById(R.id.lvOrders);
        lvOrders.setAdapter(adapter);
    }
    private void updateUsersList() {
        // Construct the data source
        // ArrayList<User> arrayOfUsers = User.getUsers();
        arrayOfOrders = Orders.UpdateOrders();
        // Create the adapter to convert the array to views
        adapter = new CustomOrdersAdapter(this, arrayOfOrders);
        // Attach the adapter to a ListView
        lvOrders = (ListView) findViewById(R.id.lvOrders);
        lvOrders.setAdapter(adapter);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
               // startActivity(new Intent(getApplicationContext(),FourActivity.class));
                break;
            case 2:
                //mTitle = getString(R.string.title_section2);
                startActivity(new Intent(getApplicationContext(),FourActivity.class));
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
            case 4:
                mTitle = getString(R.string.title_section4);
                break;
            case 5:
                mTitle = getString(R.string.title_section5);
                break;
            case 6:
                finish();
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

}
