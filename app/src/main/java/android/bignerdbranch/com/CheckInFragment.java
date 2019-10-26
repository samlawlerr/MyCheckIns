package android.bignerdbranch.com;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
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

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class CheckInFragment extends Fragment {
    private static final String ARG_CHECK_ID = "check_id";
    private static final String DIALOG_DATE = "DialogData";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_PHOTO= 1;
    private CheckIn mCheckIn;
    private EditText mTitleField;
    private EditText mDetailsField;
    private EditText mPlaceField;
    private Button mDateButton;
    private Button mDeleteButton;
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;
    private File mPhotoFile;
    private Button mReportButton;
    private GoogleApiClient mClient;
    private Button mMapButton;
    private TextView mLocation;


    public static CheckInFragment newInstance(UUID checkID) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CHECK_ID, checkID);
        CheckInFragment fragment = new CheckInFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID checkId = (UUID) getArguments().getSerializable(ARG_CHECK_ID);
        mCheckIn = CheckInLab.get(getActivity()).getCheckIn(checkId);
        mPhotoFile = CheckInLab.get(getActivity()).getPhotoFile(mCheckIn);

        mClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks(){
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        LocationRequest request = LocationRequest.create();
                        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                        request.setNumUpdates(1);
                        request.setInterval(0);

                        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED){
                            return;
                        }

                        LocationServices.FusedLocationApi.requestLocationUpdates(mClient, request, new LocationListener() {
                            @Override
                            public void onLocationChanged(Location location) {
                                Log.i("Location", "Got a fix: " + location);
                            }
                        });
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                    }
                })
                .build();
    }

    @Override
    public void onPause() {
        super.onPause();
        CheckInLab.get(getActivity())
                .updateCheckIn(mCheckIn);
    }

    @Override
    public void onStart(){
        super.onStart();
        mClient.connect();
    }

    @Override
    public void onStop(){
        super.onStop();
        mClient.disconnect();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_checkin, container, false);

        PackageManager packageManager = getActivity().getPackageManager();

        mTitleField = (EditText) v.findViewById(R.id.check_title);
        mTitleField.setText(mCheckIn.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(
                    CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(
                    CharSequence s, int start, int before, int count) {
                mCheckIn.setTitle(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mDetailsField = (EditText) v.findViewById(R.id.check_details);
        mDetailsField.setText(mCheckIn.getDetails());
        mDetailsField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(
                    CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCheckIn.setDetails(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mPlaceField = (EditText) v.findViewById(R.id.check_place);
        mPlaceField.setText(mCheckIn.getPlace());
        mPlaceField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(
                    CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCheckIn.setPlace(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mDeleteButton = (Button) v.findViewById(R.id.check_delete);
        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckInLab.get(getActivity()).deleteCheckIn(mCheckIn);

                getActivity().finish();
            }
        });

        mDateButton = (Button) v.findViewById(R.id.check_date);
        updateDate();
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment
                        .newInstance(mCheckIn.getDate());
                dialog.setTargetFragment(CheckInFragment.this, REQUEST_DATE);
                dialog.show(manager, DIALOG_DATE);
            }
        });

        mPhotoButton = (ImageButton) v.findViewById(R.id.check_camera);

        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        boolean canTakePhoto = mPhotoFile != null &&
                captureImage.resolveActivity(packageManager) != null;
        mPhotoButton.setEnabled(canTakePhoto);
        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = FileProvider.getUriForFile(getActivity(),
                        "com.bignerdranch.android.criminalintent.fileprovider",
                        mPhotoFile);
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                List<ResolveInfo> cameraActivities = getActivity()
                        .getPackageManager().queryIntentActivities(captureImage,
                                PackageManager.MATCH_DEFAULT_ONLY);
                for (ResolveInfo activity : cameraActivities) {
                    getActivity().grantUriPermission(activity.activityInfo.packageName,
                            uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }
                startActivityForResult(captureImage, REQUEST_PHOTO);
            }
        });

        mReportButton = (Button) v.findViewById(R.id.check_share);
        mReportButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, getCheckInReport());
                i = Intent.createChooser(i, getString(R.string.send_report));
                startActivity(i);
            }
        });

        mLocation = (TextView) v.findViewById(R.id.check_location);
        String coordinates = ("Latitude " + mCheckIn.getLat() + " Longitude " + mCheckIn.getLon());
        mLocation.setText(coordinates);


        mMapButton = (Button) v.findViewById(R.id.show_map);
        mMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MapsActivity.class);
                intent.putExtra("latitude", mCheckIn.getLat());
                intent.putExtra("longitude", mCheckIn.getLon());
                startActivity(intent);
            }
        });

        mPhotoView = (ImageView) v.findViewById(R.id.check_photo);
        updatePhotoView();

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_DATE) {
            Date date = (Date) data
                    .getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCheckIn.setDate(date);
            updateDate();
        } else if (requestCode == REQUEST_PHOTO) {
            Uri uri = FileProvider.getUriForFile(getActivity(),
                    "com.bignerdranch.android.criminalintent.fileprovider",
                    mPhotoFile);
            getActivity().revokeUriPermission(uri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            updatePhotoView();
        }
    }

    private void updatePhotoView() {
        if (mPhotoFile == null || !mPhotoFile.exists()) {
            mPhotoView.setImageDrawable(null);
        } else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(
                    mPhotoFile.getPath(), getActivity());
            mPhotoView.setImageBitmap(bitmap);
        }
    }

    private void updateDate() {
        mDateButton.setText(mCheckIn.getDate().toString());
    }

    private String getCheckInReport() {

        String dateFormat = "EEE, MMM dd";
        String dateString = DateFormat.format(dateFormat,
                mCheckIn.getDate()).toString();

        String report = getString(R.string.check_share, mCheckIn.getTitle(), dateString, mCheckIn.getLocation());

        return report;
    }

}

