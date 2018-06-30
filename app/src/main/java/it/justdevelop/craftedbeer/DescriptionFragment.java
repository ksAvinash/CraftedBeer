package it.justdevelop.craftedbeer;


import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import it.justdevelop.craftedbeer.helpers.SQLiteDatabaseHelper;


/**
 * A simple {@link Fragment} subclass.
 */
public class DescriptionFragment extends Fragment {


    public DescriptionFragment() {
        // Required empty public constructor
    }

    View view;
    int beer_id;
    Context context;
    String email;
    FirebaseDatabase database;
    DatabaseReference myRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_description, container, false);


        initializeViews();

        return view;
    }

    private void initializeViews(){
        beer_id = getArguments().getInt("id");
        context = getActivity().getApplicationContext();

        email = context.getSharedPreferences("cb", Context.MODE_PRIVATE).getString("email", "");
        if(!email.equals("")){
            database = FirebaseDatabase.getInstance();

        }


        ImageView desc_beer_image = view.findViewById(R.id.desc_beer_image);
        final TextView beer_name = view.findViewById(R.id.beer_name);
        final TextView beer_cost = view.findViewById(R.id.beer_cost);
        TextView beer_style = view.findViewById(R.id.beer_style);
        TextView beer_ibu = view.findViewById(R.id.beer_ibu);
        TextView beer_abv = view.findViewById(R.id.beer_abv);
        Button add_to_cart = view.findViewById(R.id.add_to_cart);

        int random_number = (int) (Math.random() * 20)+1;

        Picasso.get()
                .load("https://s3.ap-south-1.amazonaws.com/test-hackerrank/"+random_number+".jpg")
                .placeholder(R.drawable.default_beer)
                .into(desc_beer_image);


        SQLiteDatabaseHelper helper = new SQLiteDatabaseHelper(context);
        Cursor cursor = helper.fetchBeerById(beer_id);
        while(cursor.moveToNext()){
            beer_name.setText(cursor.getString(1));
            beer_style.setText(cursor.getString(2));
            beer_abv.setText(cursor.getDouble(3)+" %");

            if(cursor.getInt(4) != 0)
                beer_ibu.setText(""+cursor.getInt(4));
            else
                beer_ibu.setText("0");
            beer_cost.setText(cursor.getDouble(5)+" Oz");
        }
        cursor.close();
        helper.close();


        add_to_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(database != null && email != null && !email.equals("")){
                    myRef = database.getReference().child("users").child(email).child("cart").child(String.valueOf(beer_id));

                    HashMap<String, String> cart = new HashMap<>();
                    cart.put("product_name", beer_name.getText().toString());
                    cart.put("product_quantity", "1");
                    cart.put("status", "in cart");
                    cart.put("cost", beer_cost.getText().toString());

                    myRef.setValue(cart);
                    Snackbar.make(view, "Great "+beer_name.getText().toString()+" added to cart!", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                }else{
                    Snackbar.make(view, "Sign in to add to cart", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                }
            }
        });

    }



}
