package project.pentacore.notebook.view;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;

import project.pentacore.notebook.R;

public class ProgressDialog extends AlertDialog {
    private final static String TAG = ProgressDialog.class.getSimpleName();
    private Context mContext;

    public ProgressDialog(Context context, int theme) {
        super(context, theme);
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_dialog_progress);

    }


}
