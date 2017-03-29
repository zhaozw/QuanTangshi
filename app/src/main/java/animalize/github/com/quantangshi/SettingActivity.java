package animalize.github.com.quantangshi;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;

import animalize.github.com.quantangshi.Database.BackupUtil;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {

    // 备份
    private TextView locPath;
    private String path;
    private Button backupButton;

    public static void actionStart(Context context) {
        Intent i = new Intent(context, SettingActivity.class);
        context.startActivity(i);
    }

    private static int getFileSize(File file) {
        try {
            int size;
            FileInputStream fis = new FileInputStream(file);
            size = fis.available();
            fis.close();
            return size;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        locPath = (TextView) findViewById(R.id.loc_path);

        path = BackupUtil.getBackupDir();
        locPath.setText(path);

        backupButton = (Button) findViewById(R.id.button_backup);
        backupButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_backup:
                if (!allowPermission()) {
                    requestPermission();
                } else {
                    doBackup();
                }
                break;
        }
    }

    private void doBackup() {
        backupButton.setEnabled(false);

        File f = BackupUtil.backup();

        String s = "已保存到:" + f.getAbsolutePath() + "\n共" + getFileSize(f) + "字节";
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();

        backupButton.setEnabled(true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED) {
                    doBackup();
                } else {
                    Toast.makeText(this,
                            "您没有授予权限，无法进行备份。",
                            Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private boolean allowPermission() {
        return ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
    }
}
