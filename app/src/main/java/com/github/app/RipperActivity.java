package com.github.app;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

import com.github.layout.SubTextView;

/**
 * 水波纹效果展示
 *
 * @author ZhongDaFeng
 */
public class RipperActivity extends AppCompatActivity {
    private SubTextView tvTag;
    private SubTextView btnUpdate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ripper);
        tvTag = (SubTextView) findViewById(R.id.tv_tag);
        btnUpdate = (SubTextView) findViewById(R.id.tv_update);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean enabled = tvTag.isEnabled();
                tvTag.setEnabled(!enabled);
                btnUpdate.setText(String.valueOf(!enabled));
            }
        });
    }
}
