package com.limefriends.molde.menu_map.cameraManager;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.limefriends.molde.R;
import com.limefriends.molde.menu_map.MoldeReportActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MoldeReportCheckImage extends AppCompatActivity {
    @BindView(R.id.report_check_image)
    ImageView report_check_image;
    @BindView(R.id.report_check_ok_button)
    ImageButton report_check_ok_button;
    @BindView(R.id.report_check_cancel_button)
    ImageButton report_check_cancel_button;

    private final int ADD_IMAGE = 100;
    private int imageSeq = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity_molde_report_check_image);
        ButterKnife.bind(this);

        Intent cameraIntent = getIntent();
        imageSeq = cameraIntent.getIntExtra("imageSeq", 1);
        final Uri uri = cameraIntent.getParcelableExtra("imagePath");
        if(uri != null){
            report_check_image.setImageURI(uri);
        }

        report_check_ok_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), MoldeReportActivity.class);
                intent.putExtra("imagePath", uri);
                intent.putExtra("imageSeq", imageSeq);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        report_check_cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }


}
