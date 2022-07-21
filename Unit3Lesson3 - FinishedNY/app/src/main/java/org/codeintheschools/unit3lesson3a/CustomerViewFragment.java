package org.codeintheschools.unit3lesson3a;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import org.codeintheschools.unit3lesson3a.databinding.CustomerViewBinding;

import java.util.ArrayList;

public class CustomerViewFragment extends Fragment {

  private CustomerViewBinding binding;
  private ArrayList<Customer> myCustomers;
  private int currentIndex;


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    binding = CustomerViewBinding.inflate(inflater, container, false);

    //Set the initial view of the previous and next buttons to invisible
    binding.buttonNext.setVisibility(View.INVISIBLE);
    binding.buttonPrevious.setVisibility(View.INVISIBLE);

    return binding.getRoot();
  }

  public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

      DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("customers");
      //Create a new listener that gets all of the Customers in a single call to the database
      ValueEventListener allCustomerQueryEventListener =
              new ValueEventListener() {
                  @Override
                  public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                      //Initialize our customer array
                      myCustomers = new ArrayList<Customer>();

                      // Iterate through all the children in the snapshot, this should be
                      // all the children in the "customers" object
                      for (DataSnapshot customerSnapShot : snapshot.getChildren()) {
                          //From our snapshot, get the value of our key/value pair. This value
                          //contains a customer object
                          Customer myCustomer = customerSnapShot.getValue(Customer.class);
                          myCustomers.add(myCustomer);
                          Log.d("onDataChange()", "New Customer: " + myCustomer.name);
                      }
                      //Check if we have any customers
                      if (myCustomers.size() > 0) {
                          //Set the current index to 0, which is the first entry in the array
                          currentIndex = 0;
                          //Get the first customer
                          Customer firstCustomer = myCustomers.get(currentIndex);
                          //Load the first customer into the view with our new function
                          loadCustomerIntoView(myCustomers.get(currentIndex));
                      }

                      //Check if we have more than 1 customer
                      if (myCustomers.size() > 1) {
                          //If we do, make the NEXT button visible so you can navigate to a different customer
                          binding.buttonNext.setVisibility(View.VISIBLE);
                      }
                  }

                  @Override
                  public void onCancelled(@NonNull @NotNull DatabaseError error) {
                  }
              };
      mDatabase.addValueEventListener(allCustomerQueryEventListener);

      // Next Button
      binding.buttonNext.setOnClickListener(
              new View.OnClickListener() {
                  @Override
                  public void onClick(View view) {
                      //Add 1 to the currentIndex
                      currentIndex = currentIndex + 1;

                      //Get the next customer and load them in the view
                      loadCustomerIntoView(myCustomers.get(currentIndex));

                      //If this is the last customer, turn off the NEXT button
                      if (currentIndex == myCustomers.size() - 1) {
                          binding.buttonNext.setVisibility(View.INVISIBLE);
                      }

                      //If there are previous customers, enable the PREVIOUS button
                      if (currentIndex > 0) {
                          binding.buttonPrevious.setVisibility(View.VISIBLE);
                      }
                  }
              });
      // Previous Button
      binding.buttonPrevious.setOnClickListener(
              new View.OnClickListener() {
                  @Override
                  public void onClick(View view) {
                      //Subtract 1 from the currentIndex
                      currentIndex = currentIndex - 1;

                      //Get the previous customer and load them in the view
                      loadCustomerIntoView(myCustomers.get(currentIndex));

                      //If this is the first item in the index, turn off the PREVIOIUS button
                      if (currentIndex == 0) {
                          binding.buttonPrevious.setVisibility(View.INVISIBLE);
                      }

                      //If there are more customers in the index, turn on the NEXT button
                      if (currentIndex < myCustomers.size() - 1) {
                          binding.buttonNext.setVisibility(View.VISIBLE);
                      }
                  }
              });
  }


  @Override
  public void onDestroyView() {
    super.onDestroyView();
    binding = null;
  }

    private void loadCustomerIntoView(Customer myCustomer) {
        //Get references for all of the views in this fragment
        EditText newCustomerName = (EditText) getView().findViewById(R.id.editTextTextCustomerName);
        EditText newCustomerEmail = (EditText) getView().findViewById(R.id.editTextCustomerEmail);
        EditText newCustomerPhone = (EditText) getView().findViewById(R.id.editTextCustomerPhone);
        EditText newCustomerZip = (EditText) getView().findViewById(R.id.editTextZipcode);
        CheckBox newCustomerSendMarketing =
                (CheckBox) getView().findViewById(R.id.checkBoxReceiveMarketing);

        //Set the values of the views based on the Customer object passed in to this method
        newCustomerName.setText(myCustomer.name);
        newCustomerEmail.setText(myCustomer.email);
        newCustomerPhone.setText(myCustomer.phone);
        newCustomerZip.setText(String.valueOf(myCustomer.zipcode));
        newCustomerSendMarketing.setChecked(myCustomer.receiveMarketing);
    }
}
