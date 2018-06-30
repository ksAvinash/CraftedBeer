package it.justdevelop.craftedbeer;


import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import it.justdevelop.craftedbeer.adapters.beer_object;
import it.justdevelop.craftedbeer.helpers.SQLiteDatabaseHelper;


/**
 * A simple {@link Fragment} subclass.
 */
public class BeerFragment extends Fragment {
    private static final String LOG = "BeerFragment :";
    private static final String APP_LOG_TAG = "CrafterBeer: ";

    public BeerFragment() {
        // Required empty public constructor
    }

    View view;
    SQLiteDatabaseHelper helper;
    Context context;
    private ListView beerList;
    private List<beer_object> beerAdapter = new ArrayList<>();
    FloatingActionButton order;
    boolean ascending = true;
    FirebaseDatabase database;
    DatabaseReference myRef;
    String email;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_beer, container, false);
        Log.i(APP_LOG_TAG, LOG+"Beer Fragment Launched!");

        initializeViews();

        return view;
    }


    private void initializeViews(){
        context = getActivity().getApplicationContext();
        helper = new SQLiteDatabaseHelper(context);
        beerList = view.findViewById(R.id.beerList);
        fetchAllBears();
        ascending = false;

        email = context.getSharedPreferences("cb", Context.MODE_PRIVATE).getString("email", "");
        if(!email.equals("")){
            database = FirebaseDatabase.getInstance();
        }


        order = view.findViewById(R.id.order);
        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ascending){
                    ascending = false;
                    order.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.down_arrow));
                    Snackbar.make(view, "Sorting by Alcohol ascending", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                    fetchBeersAscending();
                }else{
                    ascending = true;
                    order.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.up_arrow));
                    Snackbar.make(view, "Sorting by Alcohol descending", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                    fetchBeersDescending();
                }
            }
        });

    }


    private void fetchAllBears(){
        beerAdapter.clear();
        Cursor cursor =  helper.fetchAllBeers();
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


    private void fetchBeersDescending(){
        beerAdapter.clear();
        Cursor cursor =  helper.fetchBeersByAlcoholDescending();
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


    private void fetchBeersAscending(){
        beerAdapter.clear();
        Cursor cursor =  helper.fetchBeersByAlcoholAscending();
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
            super(context, R.layout.beer_list_item, beerAdapter);
        }

        @SuppressLint("SetTextI18n")
        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            View itemView = convertView;
            if (itemView == null) {
                LayoutInflater inflater = LayoutInflater.from(context);
                itemView = inflater.inflate(R.layout.beer_list_item, parent, false);
            }
            final beer_object current = beerAdapter.get(position);

            ImageView beer_image = itemView.findViewById(R.id.beer_image);
            TextView beer_name = itemView.findViewById(R.id.beer_name);
            TextView beer_style = itemView.findViewById(R.id.beer_style);
            TextView beer_ounces = itemView.findViewById(R.id.beer_ounces);


            int random_number = (int) (Math.random() * 20)+1;

            Picasso.get()
                    .load("https://s3.ap-south-1.amazonaws.com/test-hackerrank/"+random_number+".jpg")
                    .placeholder(R.drawable.default_beer)
                    .into(beer_image);

            beer_name.setText(current.getName());
            beer_style.setText(current.getStyle());
            beer_ounces.setText(current.getOunces()+" Oz");
            final LikeButton add_to_card = itemView.findViewById(R.id.add_to_cart_icon);


            add_to_card.setOnLikeListener(new OnLikeListener() {
                @Override
                public void liked(LikeButton likeButton) {
                    if(database != null && email != null && !email.equals("")){
                        myRef = database.getReference().child("users").child(email).child("cart").child(String.valueOf(current.getId()));

                        HashMap<String, String> cart = new HashMap<>();
                        cart.put("product_name", current.getName());
                        cart.put("product_quantity", "1");
                        cart.put("status", "in cart");
                        cart.put("cost", current.getOunces()+"");

                        myRef.setValue(cart);
                        Snackbar.make(view, "Great "+current.getName()+" added to cart!", Snackbar.LENGTH_SHORT)
                                .setAction("Action", null).show();
                        add_to_card.setEnabled(false);
                    }else{
                        Snackbar.make(view, "Sign in to add to cart", Snackbar.LENGTH_SHORT)
                                .setAction("Action", null).show();
                    }
                }

                @Override
                public void unLiked(LikeButton likeButton) {

                }
            });


            return itemView;
        }
    }





}
