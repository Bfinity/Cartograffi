package com.detroitlabs.cartograffi.fragments;


import android.app.ActionBar;
import android.app.Fragment;
import android.app.Service;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.detroitlabs.cartograffi.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 */
public class SaveFragment extends Fragment implements View.OnClickListener {
    private Bitmap mapImage;
    private EditText filenameEditText;
    public static final File directory =
            new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Cartograffi");

    public static SaveFragment newInstance(Bitmap mapImage) {
        Bundle args = new Bundle();
        args.putParcelable(CreateFragment.MAP_IMAGE_KEY, mapImage);
        SaveFragment saveFrag = new SaveFragment();
        saveFrag.setArguments(args);
        return saveFrag;
    }

    public SaveFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.id.action_share).setVisible(false).setEnabled(false);
        menu.findItem(R.id.action_hide).setVisible(false).setEnabled(false);
        menu.findItem(R.id.action_save).setVisible(false).setEnabled(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()) {
                case android.R.id.home:
                    closeKeyboard();
                    getFragmentManager().popBackStack();
                    return true;
            }
            return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar ab = getActivity().getActionBar();
        if (ab != null){
            ab.setHomeButtonEnabled(true);
            ab.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_save, container, false);

        if (getActivity().getActionBar() != null){
            getActivity().getActionBar().setTitle(getString(R.string.title_activity_save));

        }
        setHasOptionsMenu(true);

        if (getArguments() != null) {
            mapImage = getArguments().getParcelable(CreateFragment.MAP_IMAGE_KEY);
        }

        ImageView snapshotImageView = (ImageView) root.findViewById(R.id.save_snapshot_container);
        snapshotImageView.setImageBitmap(mapImage);

        Button saveButton = (Button) root.findViewById(R.id.save_save_button);
        saveButton.setOnClickListener(this);

        filenameEditText = (EditText) root.findViewById(R.id.save_title_editText);

        return root;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.save_save_button:
                closeKeyboard();
                validateFilename();
        }
    }

    private void closeKeyboard() {
        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Service.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(filenameEditText.getWindowToken(), 0);
    }

    public void validateFilename() {

        String userEntry = filenameEditText.getText().toString().trim();
        Boolean validEntry = true;
        if (userEntry.length() < 1) {
            Toast.makeText(getActivity(), "You must first enter a filename", Toast.LENGTH_SHORT).show();
            validEntry = false;
        } else if (userEntry.length() > 20) {
            Toast.makeText(getActivity(), "Filename must be 20 characters or under", Toast.LENGTH_SHORT).show();
            validEntry = false;
        } else {
            for (char currentChar : userEntry.toCharArray()) {
                if (!(Character.isLetterOrDigit(currentChar) || currentChar == '_' || currentChar == '-' || currentChar == '.')) {
                    Toast.makeText(getActivity(), "Filename may only include characters, numbers, '_' , '-' , and '.'", Toast.LENGTH_SHORT).show();
                    validEntry = false;
                }
            }
        }

        if (validEntry) {
            saveFile(userEntry + ".jpg");
        }

    }

    public void saveFile(String filename) {

        String state = Environment.getExternalStorageState();
        //is says that it's mounted when it isnt...
        if (Environment.MEDIA_MOUNTED.equals(state)) {

            File mapImageFile = new File(directory, filename);

            if (mapImageFile.exists()) {
                Toast.makeText(getActivity(), "Filename already in use.  Rename file.", Toast.LENGTH_SHORT).show();
            } else {

                try {
                    FileOutputStream fos = new FileOutputStream(mapImageFile);
                    mapImage.compress(Bitmap.CompressFormat.JPEG, 90, fos);
                    fos.close();
                    Toast.makeText(getActivity(), "File saved", Toast.LENGTH_SHORT).show();
                    getFragmentManager().popBackStack();
                } catch (FileNotFoundException e) {
                    Toast.makeText(getActivity(), "File not found", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    Toast.makeText(getActivity(), "Error accessing file", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Toast.makeText(getActivity(), "External Storage not found", Toast.LENGTH_SHORT).show();
        }
    }
}
