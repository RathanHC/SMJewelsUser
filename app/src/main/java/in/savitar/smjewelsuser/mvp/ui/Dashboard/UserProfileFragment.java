package in.savitar.smjewelsuser.mvp.ui.Dashboard;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.glxn.qrgen.android.QRCode;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import in.savitar.smjewelsuser.R;
import in.savitar.smjewelsuser.databinding.FragmentUserProfileBinding;



public class UserProfileFragment extends Fragment  implements DashboardContract.View{

  DashboardPresenter mPresenter;
  FragmentUserProfileBinding mBinding;


  public UserProfileFragment() {
    // Required empty public constructor
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    mBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_user_profile,container,false);
    mPresenter = new DashboardPresenter(this);
    init();
    return mBinding.getRoot();
  }

  private void init() {
    fetchInfo();
    mBinding.signOut.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        mPresenter.signOut(getActivity());
      }
    });
  }

  private void fetchInfo() {

    SharedPreferences preferences = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebaseDatabase.getReference();

    if (preferences.getString("Plan","").compareToIgnoreCase("PlanA") == 0){
      databaseReference.child(preferences.getString("Plan","")).child("UsersList").child(preferences.getString("SetName",""))
              .child(preferences.getString("UserKey","")).addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
          mBinding.userIdProfile.setText(snapshot.child("ID").getValue(Long.class).toString());
          mBinding.userNameProfile.setText(snapshot.child("Name").getValue(String.class));
          Glide
                  .with(getContext())
                  .load(snapshot.child("ProfilePhoto").getValue(String.class))
                  .into(mBinding.userPhotoProfile);
          generateQRCode(snapshot.child("ID").getValue(Long.class).toString());
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
      });
    } else if (preferences.getString("Plan","").compareToIgnoreCase("PlanB") == 0) {
      databaseReference.child(preferences.getString("Plan","")).child("UsersList")
              .child(preferences.getString("UserKey","")).addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
          mBinding.userIdProfile.setText(snapshot.child("ID").getValue(String.class));
          mBinding.userNameProfile.setText(snapshot.child("Name").getValue(String.class));
          Glide
                  .with(getContext())
                  .load(snapshot.child("ProfilePhoto").getValue(String.class))
                  .into(mBinding.userPhotoProfile);
          generateQRCode(snapshot.child("ID").getValue(String.class).toString());
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
      });
    } else if (preferences.getString("Plan", "").compareToIgnoreCase("PlanC") == 0) {
      databaseReference.child(preferences.getString("Plan", "")).child("UsersList")
              .child(preferences.getString("UserKey", "")).addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
          mBinding.userIdProfile.setText(snapshot.child("ID").getValue(String.class));
          mBinding.userNameProfile.setText(snapshot.child("Name").getValue(String.class));
          Glide
                  .with(getContext())
                  .load(snapshot.child("ProfilePhoto").getValue(String.class))
                  .into(mBinding.userPhotoProfile);
          generateQRCode(snapshot.child("ID").getValue(String.class).toString());
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
      });
    }


  }

  private void generateQRCode(String id) {

    Bitmap myBitmap = QRCode.from(id).withSize(200,200).bitmap();
    mBinding.userCodeProfile.setImageBitmap(myBitmap);

  }

  @Override
  public void onSuccess(String message) {

  }

  @Override
  public void onFailure(String message) {

  }
}