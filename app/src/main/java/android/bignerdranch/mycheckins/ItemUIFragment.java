package android.bignerdranch.mycheckins;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class ItemUIFragment extends Fragment {

    private Item mItem;
    private File mPhotoFile;

    // EditTexts
    private EditText mTitleField;
    private EditText mPlaceField;
    private EditText mDetailsField;

    // Buttons
    private Button mDateButton;
    private Button mDeleteButton;
    private Button mFriendButton;
    private Button mReportButton;
    private ImageButton mPhotoButton;
    private Button mShowMapButton;

    private TextView mLocationField;

    private ImageView mPhotoView;
    private GoogleApiClient mClient;

    private double mLat;
    private double mLng;


    // CONSTANTS
    private static final String ARG_ITEM_ID = "item_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_CONTACT = 1;
    private static final int REQUEST_PHOTO = 2;
    public static final int REQUEST_LOCATION = 3;

    /**
     * Create a new fragment
     * @param itemId
     * @return
     */
    public static ItemUIFragment newInstance(UUID itemId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_ITEM_ID, itemId);
        ItemUIFragment fragment = new ItemUIFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID itemId = (UUID) getArguments().getSerializable(ARG_ITEM_ID);
        mItem = ItemLab.get(getActivity()).getItem(itemId);
        mPhotoFile = ItemLab.get(getActivity()).getPhotoFile(mItem);


        mClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        LocationRequest request = LocationRequest.create();
                        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                        request.setNumUpdates(1);
                        request.setInterval(0);


                        if (ContextCompat.checkSelfPermission(getActivity(),
                                Manifest.permission.ACCESS_FINE_LOCATION)
                                != PackageManager.PERMISSION_GRANTED) {

                            ActivityCompat.requestPermissions(getActivity(),
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_LOCATION);
                            return;
                        }

                        Location location = LocationServices.FusedLocationApi.getLastLocation(mClient);
                        mLat = location.getLatitude();
                        mLng = location.getLongitude();
                    }

                    @Override
                    public void onConnectionSuspended(int i) {}
                })
                .build();

        mItem.setLatitude(mLat);
        mItem.setLongitude(mLng);
    }


    @Override
    public void onStart() {
        super.onStart();
        mClient.connect();

    }

    @Override
    public void onStop() {
        super.onStop();
        mClient.disconnect();
    }

    @Override
    public void onPause() {
        super.onPause();
        ItemLab.get(getActivity()).updateItem(mItem);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /* ----- TITLE ----- */
        View v = inflater.inflate(R.layout.fragment_item, container, false);
        mTitleField = v.findViewById(R.id.item_title);
        mTitleField.setText(mItem.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(
                    CharSequence s, int start, int count, int after) {
                // This space intentionally left blank
            }
            @Override
            public void onTextChanged(
                    CharSequence s, int start, int before, int count) {
                mItem.setTitle(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {
                // This one too
            }
        });

        /* ----- PLACE ----- */
        mPlaceField = v.findViewById(R.id.item_place);
        mPlaceField.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(
                    CharSequence s, int start, int count, int after) {
                // This space intentionally left blank
            }
            @Override
            public void onTextChanged(
                    CharSequence s, int start, int before, int count) {
                mItem.setPlace(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {
                // This one too
            }
        });

        /* ----- DETAILS ----- */
        mDetailsField = v.findViewById(R.id.item_place);
        mPlaceField.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(
                    CharSequence s, int start, int count, int after) {
                // This space intentionally left blank
            }
            @Override
            public void onTextChanged(
                    CharSequence s, int start, int before, int count) {
                mItem.setDetails(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {
                // This one too
            }
        });


        /* ----- DATE ----- */
        mDateButton = (Button) v.findViewById(R.id.item_date);
        updateDate();

        mDateButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mItem.getDate());
                dialog.setTargetFragment(ItemUIFragment.this, REQUEST_DATE);
                dialog.show(manager, DIALOG_DATE);
            }

        });

        /* ----- LOCATION ----- */
        mLocationField = v.findViewById(R.id.item_location);
        String latlng;
        if (ActivityCompat.checkSelfPermission(getContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            Log.d("LAT", String.valueOf(mLat));
            Log.d("LNG", String.valueOf(mItem.getLongitude()));
            latlng = "Lat: " + mLat + " Lng: " + mLng;
            mLocationField.setText(latlng);
        }


        /* ----- DELETE ----- */
        mDeleteButton = (Button) v.findViewById(R.id.item_delete_button);
        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ItemLab.get(getActivity()).deleteItem(mItem);
                getActivity().finish();
                getActivity().onBackPressed();
            }
        });


        /* ----- SHARE ----- */
        final Uri uri = FileProvider.getUriForFile(getActivity(),
                "com.bignerdranch.android.mycheckins.fileprovider",
                mPhotoFile);

        mReportButton = (Button) v.findViewById(R.id.item_report);
        mReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.setType("image/png");
                i.putExtra(Intent.EXTRA_TEXT, getItemReport());
                i.putExtra(Intent.EXTRA_STREAM, uri);
                i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.item_report_subject));
                i = Intent.createChooser(i, getString(R.string.send_report));
                startActivity(i);
            }
        });

        /* ----- FRIEND ----- */
        final Intent pickContact = new Intent(Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI);

        mFriendButton = (Button) v.findViewById(R.id.item_friend);
        mFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(pickContact, REQUEST_CONTACT);
            }
        });

        if (mItem.getFriend() != null) {
            mFriendButton.setText(mItem.getFriend());
        }

        // Guard against no contacts app
        PackageManager packageManager = getActivity().getPackageManager();

        if (packageManager.resolveActivity(pickContact,
                PackageManager.MATCH_DEFAULT_ONLY) == null) {
            mFriendButton.setEnabled(false);
        }

        /* ----- PHOTO ----- */
        mPhotoButton = (ImageButton) v.findViewById(R.id.item_camera);

        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        boolean canTakePhoto = mPhotoFile != null &&
                captureImage.resolveActivity(packageManager) != null;

        mPhotoButton.setEnabled(canTakePhoto);

        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = FileProvider.getUriForFile(getActivity(),
                        "com.bignerdranch.android.mycheckins.fileprovider",
                        mPhotoFile);

                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);

                List<ResolveInfo> cameraActivities = getActivity().getPackageManager()
                        .queryIntentActivities(captureImage , PackageManager.MATCH_DEFAULT_ONLY);

                for (ResolveInfo activity : cameraActivities) {
                    getActivity().grantUriPermission(activity.activityInfo.packageName,
                            uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }
                startActivityForResult(captureImage, REQUEST_PHOTO);
            }
        });

        mPhotoView = (ImageView) v.findViewById(R.id.item_photo);
        updatePhotoView();


        /* ----- MAP ACTIVITY ----- */
        mShowMapButton = v.findViewById(R.id.show_map);
        final Intent showMap = new Intent(getActivity(), MapsActivity.class);

        mShowMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMap.putExtra("Lat", mLat);
                showMap.putExtra("Lng", mLng);
                startActivity(showMap);
            }
        });

        /* RETURN THE VIEW */
        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        /* ----- DATE ----- */
        if (requestCode == REQUEST_DATE) {
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mItem.setDate(date);
            updateDate();
        }
        /* ----- CONTACT ----- */
        else if (requestCode == REQUEST_CONTACT && data != null) {
            Uri contactUri = data.getData();

            // Specify which fields you want your query to return values for
            String[] queryFields = new String[] {
                    ContactsContract.Contacts.DISPLAY_NAME
            };

            // Perform your query - the contactUri is like a "where" clause here
            Cursor c = getActivity().getContentResolver()
                    .query(contactUri, queryFields, null, null, null);

            try {
                // Double-check that you actually got results
                if(c.getCount() == 0) {
                    return;
                }

                // Pull out the first column of the first row of data -
                // that is your suspect's name
                c.moveToFirst();
                String suspect = c.getString(0);
                mItem.setFriend(suspect);
                mFriendButton.setText(suspect);
            } finally {
                c.close();
            }
        }
        /* ----- CAMERA ----- */
        else if (requestCode == REQUEST_PHOTO) {
            Uri uri = FileProvider.getUriForFile( getActivity(),
                    "com.bignerdranch.android.mycheckins.fileprovider",
                    mPhotoFile);

            getActivity().revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            updatePhotoView();
        }
    }

    /**
     * Update the day
     */
    private void updateDate() {
        mDateButton.setText(mItem.getDate().toString());
    }

    /**
     * Create an item report
     * @return
     */
    private String getItemReport() {


        Date date = mItem.getDate();

        // Friend
        String friend = mItem.getFriend();

        if (friend == null) {
            friend = ".";
        } else {
            friend = " with " + getString(R.string.item_report_friend, friend);
        }

        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        String report = getString(R.string.item_report, currentTime,
                getFormattedDate(date), mItem.getPlace() + friend);

        if (mItem.getDetails() != null) {
            report = report + " " + mItem.getDetails();
        }

        if (mPhotoFile != null && mPhotoFile.exists()) {
            report = report + " A photo is attached.";
        }


        return report;
    }

    /**
     * Update the Item with a photo
     */
    private void updatePhotoView() {
        if (mPhotoFile == null || !mPhotoFile.exists()) {
            mPhotoView.setImageDrawable(null);
        }
        else {
            Bitmap bitmap = PictureUtils.getScaledBitmap( mPhotoFile.getPath(), getActivity());
            mPhotoView.setImageBitmap(bitmap);
        }
    }

    private static String getFormattedDate(Date date){
        Calendar cal=Calendar.getInstance();
        cal.setTime(date);
        //2nd of march 2015
        int day=cal.get(Calendar.DATE);

        if(!((day>10) && (day<19)))
            switch (day % 10) {
                case 1:
                    return new SimpleDateFormat("d'st' 'of' MMMM yyyy").format(date);
                case 2:
                    return new SimpleDateFormat("d'nd' 'of' MMMM yyyy").format(date);
                case 3:
                    return new SimpleDateFormat("d'rd' 'of' MMMM yyyy").format(date);
                default:
                    return new SimpleDateFormat("d'th' 'of' MMMM yyyy").format(date);
            }
        return new SimpleDateFormat("d'th' 'of' MMMM yyyy").format(date);
    }
}
