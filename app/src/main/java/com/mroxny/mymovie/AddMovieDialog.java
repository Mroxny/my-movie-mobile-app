package com.mroxny.mymovie;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;

import java.util.Objects;

public class AddMovieDialog extends AppCompatDialogFragment {
    private EditText editTextTitle;
    private EditText editTextOrgTitle;
    private EditText editTextYear;
    private EditText editTextDir;
    private EditText editTextProd;

    private AddMovieDialogListener listener;

    private final Context context;

    public AddMovieDialog(Context context){
        this.context = context;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_add_movie, null);

        builder.setView(view)
                .setTitle(getResources().getString(R.string.tag_add_movie))
                .setNegativeButton(getResources().getString(R.string.button_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton(getResources().getString(R.string.button_add), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if(editTextTitle.length()>0 && editTextYear.length()==4 && editTextDir.length()>0){


                            String title = editTextTitle.getText().toString();
                            String orgTitle = editTextOrgTitle.getText().toString();
                            orgTitle = title.equals(orgTitle)?"":orgTitle;
                            int year = Integer.parseInt(editTextYear.getText().toString());
                            String dir = editTextDir.getText().toString();
                            String prod = editTextProd.getText().toString();

                            listener.applyTexts(title, orgTitle, year,dir,prod);
                        }

                        else Toast.makeText(context, getResources().getString(R.string.error_fill_fields), Toast.LENGTH_SHORT).show();

                    }
                });

        editTextTitle = view.findViewById(R.id.am_title);
        editTextOrgTitle = view.findViewById(R.id.am_orgTitle);
        editTextYear = view.findViewById(R.id.am_year);
        editTextDir = view.findViewById(R.id.am_dir);
        editTextProd = view.findViewById(R.id.am_prod);

        return builder.create();
    }

    public void setOnAddMovieListener(AddMovieDialogListener listener) {
        this.listener = listener;
    }

    public interface AddMovieDialogListener {
        void applyTexts(String title, String orgTitle, int year, String dir, String prod);
    }
}
