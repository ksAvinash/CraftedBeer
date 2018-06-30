package it.justdevelop.craftedbeer;


import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.like.LikeButton;
import com.like.OnLikeListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import it.justdevelop.craftedbeer.adapters.beer_object;
import it.justdevelop.craftedbeer.helpers.SQLiteDatabaseHelper;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchBeerFragment extends Fragment {
    private static final String LOG = "SearchBeerFragment :";
    private static final String APP_LOG_TAG = "CrafterBeer: ";

    public SearchBeerFragment() {
        // Required empty public constructor
    }

    View view;
    SQLiteDatabaseHelper helper;
    Context context;
    private ListView beerList;
    private List<beer_object> beerAdapter = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_search_beer, container, false);


        initializeViews();

        return view;
    }

    private void initializeViews(){
        context = getActivity().getApplicationContext();
        helper = new SQLiteDatabaseHelper(context);
        beerList = view.findViewById(R.id.beerList);

        String search_name = getArguments().getString("search");
        Log.i(APP_LOG_TAG, LOG+search_name);
        fetchBeers(search_name);
    }

    private void fetchBeers(String search_name){
        beerAdapter.clear();
        Cursor cursor =  helper.fetchBeersByName(search_name);
        Log.i(APP_LOG_TAG, LOG+" Total beers fetched : "+cursor.getCount());
        while(cursor.moveToNext()){
            beerAdapter.add(new beer_object(cursor.getInt(0), cursor.getString(1), cursor.getString(2),
                    cursor.getDouble(3), cursor.getInt(4), cursor.getDouble(5)
            ));
        }
        helper.close();
        cursor.close();
        displayList();
    }


    private void displayList(){
        final ArrayAdapter<beer_object> adapter = new myBeerAdapterClass();
        beerList.setAdapter(adapter);
        beerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                beer_object current = beerAdapter.get(position);

                DescriptionFragment descriptionFragment = new DescriptionFragment();
                Bundle fragment_agruments = new Bundle();

                fragment_agruments.putInt("id", current.getId());
                descriptionFragment.setArguments(fragment_agruments);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_fragment_container, descriptionFragment).addToBackStack(null).commit();
            }
        });
    }

    private class myBeerAdapterClass extends ArrayAdapter<beer_object> {

        myBeerAdapterClass() {
            super(context, R.layout.search_beer_list_item, beerAdapter);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            View itemView = convertView;
            if (itemView == null) {
                LayoutInflater inflater = LayoutInflater.from(context);
                itemView = inflater.inflate(R.layout.search_beer_list_item, parent, false);
            }
            final beer_object current = beerAdapter.get(position);

            ImageView beer_image = itemView.findViewById(R.id.beer_image);
            TextView beer_name = itemView.findViewById(R.id.beer_name);
            TextView beer_style = itemView.findViewById(R.id.beer_style);

            int random_number = (int) (Math.random() * 20)+1;

            Picasso.get()
                    .load("https://s3.ap-south-1.amazonaws.com/test-hackerrank/"+random_number+".jpg")
                    .placeholder(R.drawable.default_beer)
                    .into(beer_image);

            beer_name.setText(current.getName());
            beer_style.setText(current.getStyle());

            return itemView;
        }
    }



}
