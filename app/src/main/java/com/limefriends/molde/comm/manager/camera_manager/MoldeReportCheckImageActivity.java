package com.limefriends.molde.comm.manager.camera_manager;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.limefriends.molde.R;
import com.limefriends.molde.ui.map.report.ReportActivity;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MoldeReportCheckImageActivity extends AppCompatActivity {
    @BindView(R.id.report_check_image)
    ImageView report_check_image;

    Button image_done_button;

    private final int ADD_IMAGE = 100;
    private Uri imagePath;
    private int imageSeq = 0;

    private String reportContent;
    private String reportAddress;
    private String reportDetailAddress;
    private String reportEmailName;
    private String reportEmailDomainPosition;
    private String reportLat;
    private String reportLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_check_image);
        ButterKnife.bind(this);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Intent cameraIntent = getIntent();
        imageSeq = cameraIntent.getIntExtra("imageSeq", 1);
        imagePath = cameraIntent.getParcelableExtra("imagePath");
        reportContent = cameraIntent.getStringExtra("reportContent");
        reportAddress = cameraIntent.getStringExtra("reportAddress");
        reportDetailAddress = cameraIntent.getStringExtra("reportDetailAddress");
        reportEmailName = cameraIntent.getStringExtra("reportEmailName");
        reportEmailDomainPosition = cameraIntent.getStringExtra("reportEmailDomainPosition");
        reportLat = cameraIntent.getStringExtra("reportLat");
        reportLng = cameraIntent.getStringExtra("reportLng");

        if(imagePath != null){
            report_check_image.setImageURI(imagePath);
        }

        image_done_button = getSupportActionBar().getCustomView().findViewById(R.id.done_button);
        image_done_button.setVisibility(View.VISIBLE);

        image_done_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), ReportActivity.class);
                intent.putExtra("imagePath", imagePath);
                intent.putExtra("imageSeq", imageSeq);
                intent.putExtra("reportContent", reportContent);
                intent.putExtra("reportAddress", reportAddress);
                intent.putExtra("reportDetailAddress", reportDetailAddress);
                intent.putExtra("reportEmailName", reportEmailName);
                intent.putExtra("reportEmailDomainPosition", reportEmailDomainPosition);
                intent.putExtra("reportLat", reportLat);
                intent.putExtra("reportLng", reportLng);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            File imageFile = new File(imagePath.getPath());
            imageFile.delete();
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(imageFile)));
            finish();
            return true;
        }
        return false;
    }


}
