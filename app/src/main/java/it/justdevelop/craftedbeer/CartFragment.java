package it.justdevelop.craftedbeer;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import it.justdevelop.craftedbeer.adapters.cart_object;


/**
 * A simple {@link Fragment} subclass.
 */
public class CartFragment extends Fragment {
    private static final String LOG = "CartFragment :";
    private static final String APP_LOG_TAG = "CrafterBeer: ";


    public CartFragment() {
        // Required empty public constructor
    }

    View view;
    Context context;
    FirebaseDatabase database;
    private DatabaseReference myCart;
    ListView cartList;
    Button signInButton;
    private List<cart_object> cartAdapter = new ArrayList<>();
    TextView note;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_cart, container, false);


        initializeViews();

        return view;
    }


    private void initializeViews(){
        context = getActivity().getApplicationContext();
        cartList = view.findViewById(R.id.cartList);
        signInButton = view.findViewById(R.id.signInButton);
        note = view.findViewById(R.id.note);

        String email = context.getSharedPreferences("cb", Context.MODE_PRIVATE).getString("email","");
        if(email.equals("")){
            cartList.setVisibility(View.GONE);
            signInButton.setVisibility(View.VISIBLE);
            note.setVisibility(View.VISIBLE);
            note.setText("Sign in to view the cart");
            signInButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), GoogleSignUpActivity.class);
                    startActivity(intent);
                }
            });
        }else{
            cartAdapter.clear();
            database = FirebaseDatabase.getInstance();
            myCart = database.getReference("users").child(email).child("cart");
            myCart.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.getValue() != null){
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            cartAdapter.add(new cart_object(Integer.parseInt(snapshot.getKey()), snapshot.child("product_name").getValue().toString(),
                                    snapshot.child("status").getValue().toString(), Integer.parseInt(snapshot.child("product_quantity").getValue().toString())
                            ));
                        }
                        displayList();
                    }else{
                        cartList.setVisibility(View.GONE);
                        note.setVisibility(View.VISIBLE);
                        note.setText("No items in cart!");
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.i(APP_LOG_TAG, LOG+"error with firebase reference : "+databaseError);
                }
            });

        }
    }



    private void displayList(){
        final ArrayAdapter<cart_object> adapter = new myCartAdapterClass();
        cartList.setAdapter(adapter);
    }


    private class myCartAdapterClass extends ArrayAdapter<cart_object> {

        myCartAdapterClass() {
            super(context, R.layout.cart_list_item, cartAdapter);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            View itemView = convertView;
            if (itemView == null) {
                LayoutInflater inflater = LayoutInflater.from(context);
                itemView = inflater.inflate(R.layout.cart_list_item, parent, false);
            }
            final cart_object current = cartAdapter.get(position);

            ImageView cart_beer_image = itemView.findViewById(R.id.cart_beer_image);
            TextView cart_product_name = itemView.findViewById(R.id.cart_product_name);
            Button cart_product_quantity = itemView.findViewById(R.id.cart_product_quantity);
            Button status = itemView.findViewById(R.id.cart_status);


            int random_number = (int) (Math.random() * 20)+1;

            Picasso.get()
                    .load("https://s3.ap-south-1.amazonaws.com/test-hackerrank/"+random_number+".jpg")
                    .placeholder(R.drawable.default_beer)
                    .into(cart_beer_image);

            cart_product_name.setText(current.getProduct_name());
            cart_product_quantity.setText(""+current.getProduct_quantity());
            status.setText(current.getStatus().toUpperCase());

            return itemView;
        }
    }

}
